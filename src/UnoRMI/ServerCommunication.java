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

    public void send(Message m) throws RemoteException {
        try{
            System.out.println("[MSG RECEIVED] Msg from :" + Manager.getInstance().getIpFromUuid(m.getUuid()) +
                    "  -  type:"+ m.getPayload().getClass());
        } catch (Exception e) {
            System.out.println("[MSG RECEIVED] Msg from UUID:" + m.getUuid());
        }

        if(!m.getUuid().equals(Manager.getInstance().getMyHost().getUuid()))
            processMessage(m);
        else if(m.getPayload() instanceof Room) {//uuid != my_uuid AND type==Room
            System.out.println("[CONFIGURATION MSG RETURNED] Ring configured!");
            //todo gameState

            System.out.println("[GAME STATE MSG] Sending game state!");


            //todo gui

        }
        else if(m.getPayload() instanceof GameState) {
            //todo inizio turno
        }
        else
            System.out.println("[RETURNED MSG] End Ring");
    }

    private void processMessage(Message message) {
        if(message.getPayload() instanceof Room) {
            System.out.println("[CONFIGURATION MSG] Ring configured!");
            this.configureRing((Room) message.getPayload());
            //todo gui
        }
        else if(message.getPayload() instanceof GameState) {

        }
        else if (message.getPayload() instanceof Card) {
            System.out.println("[CARD MSG] Played Card: Number: " + ((Card) message.getPayload()).number + " Color: "
                    + ((Card) message.getPayload()).color + "Type: " + ((Card) message.getPayload()).type);

            Card card = (Card) message.getPayload();
            //todo getType e azioni da eseguire
            if(card.active && (card.type == Card.BLOCKTYPE || card.type == Card.PLUSTWOTYPE || card.type == Card.PLUSFOURTYPE))
                card.active = false;
            else if(card.active && card.type == Card.CHANGEDIRTYPE)
                card.active = false;
            else if(card.active)
                card.active = false;

            message.setPayload(card);
        }
        else if (message.getPayload() instanceof Player) {//todo
            System.out.println("[PLAYER MSG] Player: ");
            CrashManager.getInstance().repairRing((Player) message.getPayload());
        }
        else if(message.getPayload() instanceof Deck) {

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
