package UnoRMI;

import UnoGame.Card;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by angelo on 20/02/17.
 */
public class ServerCommunication extends UnicastRemoteObject implements InterfaceCommunication {

    private static final long serialVersionUID = 1L;
    static int PORT = 1099;

    public ServerCommunication() throws RemoteException { }

    public void send(Message m) throws RemoteException {
        if(!m.getUuid().equals(Manager.getInstance().getMyHost().getUuid()))
            processMessage(m);
        else if(m.getPayload() instanceof Room) {//uuid != my_uuid AND type=Room
            //todo
        }
        else
            System.out.println("[MSG RRETURNED] ");
    }

    private void processMessage(Message message) {
        if(message.getPayload() instanceof Room) {//todo
        }
        else if (message.getPayload() instanceof Card) {
            Card card = (Card) message.getPayload();
        }
        else if (message.getPayload() instanceof Player) {
        }

        try{
            //todo boolean per vedere se giro è invertito
            this.getNextHostInterface();
        } catch (NotBoundException e) {
            System.out.println("# NOT BOUND EXCEPTION # in ServerCommunication.processMessage ");
        } catch (RemoteException e) {
            System.out.println("# REMOTE EXCEPTION # in ServerCommunication.processMessage ");
        }
    }

    public InterfaceCommunication getNextHostInterface() throws RemoteException, NotBoundException {
        InterfaceCommunication remote = null;
        Player myPlayer = Manager.getInstance().getMyPlayer();
        //todo scegliere next o previous
        Player nextPlayer = Manager.getInstance().getRoom().getNext(myPlayer);
//        Player previousPlayer = Manager.getInstance().getRoom().getPrevious(myPlayer);
        Registry register = null;

        try{
            register = LocateRegistry.getRegistry(nextPlayer.getHost().getIp(), PORT);
            //register = LocateRegistry.getRegistry(previousPlayer.getHost().getIp(), PORT);
            remote = (InterfaceCommunication) register.lookup("a"); //todo
        } catch (RemoteException e) {
            System.out.println("# REMOTE EXCEPTION # in ServerCommunication.getNextHostInterface ");
            //todo crashManager

        } catch (Exception e) {
            System.out.println("# EXCEPTION # in ServerCommunication.getNextHostInterface ");
            e.printStackTrace();
        }

        return remote;
    }
}
