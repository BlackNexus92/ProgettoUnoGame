package UnoRMI;

import UnoGame.Deck;
import UnoUI.TextureLoader;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Timer;

/**
 * Classe che implementa i metodi remoti necessari per la comunicazione dei vari player
 *
 * Created by angelo on 20/02/17.
 */
public class ServerCommunication extends UnicastRemoteObject implements InterfaceCommunication {

    private static final long serialVersionUID = 1L;
    /*numero di porta*/
    static int PORT = 1234;

    public ServerCommunication() throws RemoteException { }

    // inizializza l'arena di gioco per il player che richiama il metodo e assegna l'id univoco
    private void configureRing(Arena arena) {
        Manager.getInstance().setArena(arena);
        Arena a = Manager.getInstance().getArena();

        for(int i=0; i<a.getCurrentPlayers(); i++) {
            Player current = a.getPlayers().get(i);
            if(current.getHost().getIp().equals(Manager.getInstance().getMyHost().getIp()))
                Manager.getInstance().getMyPlayer().setId(current.getId());
            Player next = a.getNext(current);
            System.out.println("CONFIGURATION: Host "+i+", IP: "+current.getHost().getIp()+
                    "  ==> next IP: "+next.getHost().getIp());
        }
    }

    // permette l'invio del messaggio in una rete ad anello
    public void send(Message m) throws RemoteException, ServerNotActiveException {

        try {
            System.out.println("COMMUNICATION: Message from IP: " + Manager.getInstance().getIpFromUuid(m.getUuid()) +
                    " - id: "+ m.getIdPlayer());
        } catch (Exception e) {
            System.out.println("*EXCEPTION*: Exception in send!");
            e.printStackTrace();
        }

        if(!m.getUuid().equals(Manager.getInstance().getMyHost().getUuid()))
            processMessage(m);
        else
            processReturnedMessage(m);
    }

    // processa un messaggio che è ritornato al mittente
    private void processReturnedMessage(Message message) {
        if(message.type == Message.ARENA) {
            System.out.println("COMMUNICATION: Arena msg RETURNED -> Ring configured!");
            Manager.getInstance().setStatusString("Gioca il tuo turno!");
            Manager.getInstance().setIdPlaying(Manager.getInstance().getMyPlayer().getId());
            Manager.getInstance().getGameState().initializeHand(Manager.getInstance().getMyPlayer().getId(), Manager.getInstance().getArena().getNumStartingPlayers());
            Manager.getInstance().getGameState().triggerTopCard();
            TextureLoader.setChanged();

            CheckTimer t = new CheckTimer();
            Timer timer = new Timer();
            Manager.getInstance().setTimer(timer);
            Manager.getInstance().getTimer().scheduleAtFixedRate(t, 1, 40);
        }
        else if(message.type == Message.PLAYER) {
            System.out.println("COMMUNICATION: Player msg RETURNED -> Crashed player removed from Ring!");
        }
        else if(message.type == Message.MOVE || message.type == Message.PASS || message.type == Message.SHUFFLEPASS || message.type == Message.SHUFFLEMOVE) {
            System.out.println("COMMUNICATION: Move msg RETURNED -> Game State updated!");
            if(Manager.getInstance().getArena().getCurrentPlayers()==1)
                updateGameState(message);
        }

//        System.out.println("[RETURNED MSG] End Ring");
    }

    // processa, in base al tipo, un messaggio arrivato da un altro player e lo inoltra nell'anello
    private void processMessage(Message message) {
        if(message.type == Message.ARENA) {
            System.out.println("COMMUNICATION: Arena msg RECEIVED -> Configuring Ring...");
            this.configureRing((Arena) message.getPayload());
            Manager.getInstance().getGameState().initializeHand(Manager.getInstance().getMyPlayer().getId(), Manager.getInstance().getArena().getNumStartingPlayers());
            Manager.getInstance().setIdPlaying(message.getIdPlayer());
            Player p = Manager.getInstance().getArena().getPlayerFromId(message.getIdPlayer());
            if(p!=null) Manager.getInstance().setStatusString(p.getUsername() + " gioca il suo turno...");
            TextureLoader.setChanged();

            CheckTimer t = new CheckTimer();
            Timer timer = new Timer();
            Manager.getInstance().setTimer(timer);
            Manager.getInstance().getTimer().scheduleAtFixedRate(t, 1, 40);
        }
        else if (message.type == Message.PLAYER) {
            System.out.println("COMMUNICATION: Player msg RECEIVED -> Removing Player " + ((Player) message.getPayload()).getId() + "...");
            Manager.getInstance().setStatusString(((Player) message.getPayload()).getUsername()+" ha abbandonato la partita.");
            CrashManager.getInstance().notifiedCrash((Player) message.getPayload());
        }
        else if(message.type == Message.MOVE || message.type == Message.PASS || message.type == Message.SHUFFLEPASS || message.type == Message.SHUFFLEMOVE) {
            System.out.println("COMMUNICATION: Move msg RECEIVED -> Updating Game State...");
            updateGameState(message);
        }


        try{
            System.out.println("DONE! Forwarding message!");
            Manager.getInstance().getCommunication().getNextHostInterface().send(message);
        } catch (NotBoundException e) {
            System.out.println("*EXCEPTION*: NotBoundException in processMessage ");
        } catch (RemoteException e) {
            System.out.println("*EXCEPTION*: RemoteException in processMessage ");
        } catch (ServerNotActiveException e) {
            System.out.println("*EXCEPTION*: ServerNotActiveException in processMessage ");
        }
    }

    // aggiorna lo stato di gioco in seguito alla ricezione di una mossa da parte di altri player
    private void updateGameState(Message message) {
        Player p = Manager.getInstance().getArena().getPlayerFromId(message.getIdPlayer());
        if(message.getSeqNumber()>=Manager.getInstance().getGameState().getSeqNumber() && p!=null) {
            Manager.getInstance().getGameState().setSeqNumber(message.getSeqNumber());

            p.setnCards(message.getPlayerCards());
            Manager.getInstance().getGameState().setDeck((Deck) message.getPayload());

            if (message.getPlayerCards() == 0) {
                Manager.getInstance().setWinner(message.getIdPlayer());
                Player p2 = Manager.getInstance().getArena().getPlayerFromId(message.getIdPlayer());
                if(p2!=null) Manager.getInstance().setStatusString(p2.getUsername() + " ha vinto.");
            }

            System.out.println("TURN: Turn of player " + message.getIdNextPlayer() + "!");

            TextureLoader.setChanged();

            Manager.getInstance().setIdPlaying(message.getIdNextPlayer());
            if (Manager.getInstance().isPlaying()) {
                Manager.getInstance().setStatusString("Gioca il tuo turno!");
                Manager.getInstance().getGameState().triggerTopCard();
            }
            else if(Manager.getInstance().getWinner()<0){
                Player p2 = Manager.getInstance().getArena().getPlayerFromId(message.getIdNextPlayer());
                if(p2!=null) Manager.getInstance().setStatusString(p2.getUsername() + " gioca il suo turno...");
            }
        }
    }

    // restituisce il riferimento remoto del prossimo player a cui inoltrare il messaggio
    public InterfaceCommunication getNextHostInterface() throws RemoteException, ServerNotActiveException, NotBoundException {
        InterfaceCommunication remote = null;
        Player myPlayer = Manager.getInstance().getMyPlayer();
        Player nextPlayer;
        if(Manager.getInstance().getGameState().getDeck().getReverse())
            nextPlayer = Manager.getInstance().getArena().getPrevious(myPlayer);
        else
            nextPlayer = Manager.getInstance().getArena().getNext(myPlayer);

        Registry register = null;

        try{
            register = LocateRegistry.getRegistry(nextPlayer.getHost().getIp(), PORT);
            remote = (InterfaceCommunication) register.lookup("Communication");
        } catch (RemoteException e) {
            System.out.println("*EXCEPTION*: RemoteException in getNextHostInterface ");
            return CrashManager.getInstance().manageCrash(nextPlayer);
        } catch (Exception e) {
            System.out.println("*EXCEPTION*: Exception in getNextHostInterface ");
            e.printStackTrace();
        }

        return remote;
    }

}
