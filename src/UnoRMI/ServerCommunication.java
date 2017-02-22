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
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by angelo on 20/02/17.
 */
public class ServerCommunication extends UnicastRemoteObject implements InterfaceCommunication {

    private static final long serialVersionUID = 1L;
    static int PORT = 1099;

    public ServerCommunication() throws RemoteException { }

    private void configureRing(Room room) {
        Manager.getInstance().setRoom(room);
        Room r = Manager.getInstance().getRoom();

        for(int i=0; i<r.getCurrentPlayers(); i++) {
            Player current = r.getPlayers().get(i);
            //todo reverse?
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
        else if(m.getPayload() instanceof Room) {//uuid != my_uuid AND type==Room
            System.out.println("[CONFIGURATION MSG RETURNED] Ring configured, sending Game State!");
            //todo gameState
            toSend = true;
            toSendMsg = new Message(Manager.getInstance().getMyHost().getUuid(), Manager.getInstance().getGameState());
            //todo gui
        }
        else if(m.getPayload() instanceof GameState) {
            //todo inizio turno
            System.out.println("[RETURNED GAME STATE MSG] Choose turn!");
        }
        else if(m.getPayload() instanceof Player) {
            System.out.println("[RETURNED PLAYER MSG] Crashed player removed from Ring!");
            if(Manager.getInstance().getGameState() == null) { //quando crash player è stato notificato a tutti
                System.out.println("[REQUEST MSG] Sending Game State request!");
                String s = "Request";
                toSend = true;
                toSendMsg = new Message(Manager.getInstance().getMyHost().getUuid(), s);
            }
            else {
                System.out.println("[GAME STATE MSG] Sending Game State!");
                toSend = true;
                toSendMsg = new Message(Manager.getInstance().getMyHost().getUuid(), Manager.getInstance().getGameState());
            }
        }
        else if(m.getPayload() instanceof String) {
            System.out.println("[REQUEST MSG RETURNED] Game State request complete!");
        }
        else if(m.getPayload() instanceof Integer) { //check msg da inviare quando timer è scaduto
            if((Integer) m.getPayload() == 0)
                System.out.println("[CHECK MSG RETURNED] No Crashes, a player is thinking!");
            else
                System.out.println("[CHECK MSG RETURNED] There is an undefined Crash!");
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
        if(message.getPayload() instanceof Room ) {
            System.out.println("[CONFIGURATION MSG] Ring configured!");
            this.configureRing((Room) message.getPayload());
            //todo gui
        }
        else if(message.getPayload() instanceof GameState) {
            System.out.print("[GAME STATE MSG] ");
            if(Manager.getInstance().getGameState() == null) {
                System.out.println("Initial hand! ");
                GameState gs = (GameState) message.getPayload();
                gs.setHand(gs.getDeck().drawCards(7));
                Manager.getInstance().setGameState(gs);
                message.setPayload(gs);
            }
            else {
                System.out.println("Game State changed! ");
                GameState gs = (GameState) message.getPayload();
                //todo
            }

        }
        else if (message.getPayload() instanceof Player) {//todo
            System.out.println("[PLAYER MSG] Player: ");
            CrashManager.getInstance().repairRing((Player) message.getPayload());
        }
        else if(message.getPayload() instanceof String) {
            System.out.print("[REQUEST MSG]  ");
            if(Manager.getInstance().getGameState() != null) {
                System.out.println("Player: " + Manager.getInstance().getMyHost().getIp() + " has a valid Game State!");
                message.setUuid(Manager.getInstance().getMyHost().getUuid());
                message.setPayload(Manager.getInstance().getGameState());
            }
            else
                System.out.println("Player: " + Manager.getInstance().getMyHost().getIp() + " has NOT a valid Game State. Forwarding!");
        }
        else if(message.getPayload() instanceof Integer) {
            Integer num = (Integer) message.getPayload();
            num-=1;
            message.setPayload(num);
            System.out.print("[CHECK MSG] Remaing hop =  " + num);
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

    public InterfaceCommunication getNextHostInterface() throws RemoteException, ServerNotActiveException, NotBoundException {
        InterfaceCommunication remote = null;
        Player myPlayer = Manager.getInstance().getMyPlayer();
        //todo scegliere next o previous
        Player nextPlayer;
        if(true/*reverse*/)
            nextPlayer = Manager.getInstance().getRoom().getNext(myPlayer);
        else
            nextPlayer = Manager.getInstance().getRoom().getPrevious(myPlayer);

        Registry register = null;

        try{
            register = LocateRegistry.getRegistry(nextPlayer.getHost().getIp(), PORT);
            remote = (InterfaceCommunication) register.lookup("Communication"); //todo
        } catch (RemoteException e) {
            System.out.println("# REMOTE EXCEPTION # in ServerCommunication.getNextHostInterface ");
            //todo crashManager
            return CrashManager.getInstance().repairCrash(nextPlayer);
        } catch (Exception e) {
            System.out.println("# EXCEPTION # in ServerCommunication.getNextHostInterface ");
            e.printStackTrace();
        }

        return remote;
    }
}
