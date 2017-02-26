package UnoRMI;

import UnoGame.Card;
import UnoGame.Deck;
import UnoGame.GameState;
import com.badlogic.gdx.Game;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by angelo on 20/02/17.
 */
public class ServerCommunication extends UnicastRemoteObject implements InterfaceCommunication {

    private static final long serialVersionUID = 1L;
    static int PORT = 1234;

    public ServerCommunication() throws RemoteException { }

    private void configureRing(Room room) {
        Manager.getInstance().setRoom(room);
        Room r = Manager.getInstance().getRoom();

        for(int i=0; i<r.getCurrentPlayers(); i++) {
            Player current = r.getPlayers().get(i);
            //todo reverse?
            if(current.getHost().getIp().equals(Manager.getInstance().getMyHost().getIp()))
                Manager.getInstance().getMyPlayer().setId(current.getId());
            Player next = r.getNext(current);
            System.out.println("[RING CONFIGURATION] Host "+i+", ip: "+current.getHost().getIp()+
                    "  ==> next ip: "+next.getHost().getIp());
        }
    }

    public void send(Message m) throws RemoteException, ServerNotActiveException {
        //Timer t = new Timer();
        //t.schedule(new TimerTask(),delay);

        try{
            System.out.println("[MSG RECEIVED] Msg from :" + Manager.getInstance().getIpFromUuid(m.getUuid()) +
                    "  -  type:"+ m.getPayload().getClass());
        } catch (Exception e) {
            System.out.println("[MSG RECEIVED ERROR] Msg from UUID:" + m.getUuid());
        }

        boolean toSend = false;
        Message toSendMsg = null;

        if(!m.getUuid().equals(Manager.getInstance().getMyHost().getUuid()))
            processMessage(m);
        else if(m.type == Message.ROOM) {//uuid != my_uuid AND type==Room
            System.out.println("[CONFIGURATION MSG RETURNED] Ring configured, sending Game State!");
            Manager.getInstance().getGameState().initializeHand(Manager.getInstance().getMyPlayer().getId(), Manager.getInstance().getRoom().getNumStartingPlayers());
            Manager.getInstance().getGameState().triggerTopCard();
            CheckTimer t = new CheckTimer();
            Timer timer = new Timer();
            Manager.getInstance().setTimer(timer);
            Manager.getInstance().getTimer().scheduleAtFixedRate(t, 1, 10);

        }
        else if(m.type == Message.PLAYER) {
            System.out.println("[RETURNED PLAYER MSG] Crashed player removed from Ring!");
        }
        else if(m.type == Message.MOVE || m.type == Message.PASS || m.type == Message.SHUFFLEPASS || m.type == Message.SHUFFLEMOVE) {
            System.out.println("[RETURNED MOVE MSG] Move message returned!");
            if(Manager.getInstance().getRoom().getCurrentPlayers()==1)
                updateGamestate(m);
        }

        if(toSend) {
            try {
                //todo testare se usare this or Manager.getInstance()
                Manager.getInstance().getCommunication().getNextHostInterface().send(toSendMsg);
                //this.getNextHostInterface().send(toSendMsg);
            } catch (RemoteException e) {
                System.out.println("# REMOTE EXCEPTION # in ServerCommunication.send ");
            } catch (NotBoundException e) {
                System.out.println("# NOT BOUND EXCEPTION # in ServerCommunication.send ");
            }
        }
        else
            System.out.println("[RETURNED MSG] End Ring");
    }

    private void processMessage(Message message) {
        if(message.type == Message.ROOM) {
            System.out.println("[CONFIGURATION MSG] Ring configured!");
            this.configureRing((Room) message.getPayload());
            Manager.getInstance().getGameState().initializeHand(Manager.getInstance().getMyPlayer().getId(), Manager.getInstance().getRoom().getNumStartingPlayers());
            CheckTimer t = new CheckTimer();
            Timer timer = new Timer();
            Manager.getInstance().setTimer(timer);
            Manager.getInstance().getTimer().scheduleAtFixedRate(t, 1, 10);
        }
        else if (message.type == Message.PLAYER) {
            System.out.println("[PLAYER MSG] Player " + ((Player) message.getPayload()).getId() + " crashed!");
            CrashManager.getInstance().repairRing((Player) message.getPayload());
        }
        else if(message.type == Message.MOVE || message.type == Message.PASS || message.type == Message.SHUFFLEPASS || message.type == Message.SHUFFLEMOVE) {
            updateGamestate(message);
        }


        try{
            this.getNextHostInterface().send(message);
        } catch (NotBoundException e) {
            System.out.println("# NOT BOUND EXCEPTION # in ServerCommunication.processMessage ");
        } catch (RemoteException e) {
            System.out.println("# REMOTE EXCEPTION # in ServerCommunication.processMessage ");
        } catch (ServerNotActiveException e) {
            System.out.println("# SERVER NOT ACTIVE EXCEPTION # in ServerCommunication.processMessage");
        }
    }

    private void updateGamestate(Message message) {
        //todo gestione sequence number equivalenti
        Player p = Manager.getInstance().getRoom().getPlayerFromId(message.getIdPlayer());
        if(message.getSeqNumber()>=Manager.getInstance().getGameState().getSeqNumber() && p!=null) {
            Manager.getInstance().getGameState().setSeqNumber(message.getSeqNumber());

            p.setnCards(message.getPlayerCards());
            Manager.getInstance().getGameState().setDeck((Deck) message.getPayload());

            Card c = Manager.getInstance().getGameState().getDeck().getTopCard();

            if (message.getPlayerCards() == 0) Manager.getInstance().setWinner(message.getIdPlayer());

            System.out.println("[TURN MSG] Turn of player " + message.getIdNextPlayer() + "!");
            Manager.getInstance().setIdPlaying(message.getIdNextPlayer());
            if (Manager.getInstance().isPlaying()) {
                Manager.getInstance().getGameState().triggerTopCard();
            }
        }
    }


    public InterfaceCommunication getNextHostInterface() throws RemoteException, ServerNotActiveException, NotBoundException {
        InterfaceCommunication remote = null;
        Player myPlayer = Manager.getInstance().getMyPlayer();
        //todo scegliere next o previous
        Player nextPlayer;
        if(Manager.getInstance().getGameState().getDeck().getReverse())
            nextPlayer = Manager.getInstance().getRoom().getPrevious(myPlayer);
        else
            nextPlayer = Manager.getInstance().getRoom().getNext(myPlayer);

        Registry register = null;

        try{
            register = LocateRegistry.getRegistry(nextPlayer.getHost().getIp(), PORT);
            remote = (InterfaceCommunication) register.lookup("Communication"); //todo
        } catch (RemoteException e) {
            System.out.println("# REMOTE EXCEPTION # in ServerCommunication.getNextHostInterface ");
            //todo inoltrare msg
            return CrashManager.getInstance().repairCrash(nextPlayer);
        } catch (Exception e) {
            System.out.println("# EXCEPTION # in ServerCommunication.getNextHostInterface ");
            e.printStackTrace();
        }

        return remote;
    }
}
