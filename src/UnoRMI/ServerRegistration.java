package UnoRMI;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by angelo on 20/02/17.
 */
public class ServerRegistration extends UnicastRemoteObject implements InterfaceRegistration {

    private static final long serialVersionUID = 1L;
    /*Stanza di gioco*/
    private Room room;
    /*serve per creare id dei player*/
    private int idPlayer = 1;

    public ServerRegistration(int n, Player p) throws RemoteException, InterruptedException, ServerNotActiveException {
        this.room = new Room(n);
        this.addPlayer(p);
    }

    //todo
    public void addPlayer(Player p) throws RemoteException, InterruptedException, ServerNotActiveException {
        System.out.println("[REGISTRATION] Aggiunto Host IP: "+p.getHost().getIp()+" e PORT:"+p.getHost().getPort());
        p.setId((char) idPlayer );
        idPlayer++;
        this.room.addPlayer(p);
        //		Controller.getInstance().getStartPanel().informServerHostRegistred(this.room);

        if(this.room.isFull())
        {
            System.out.println("[REGISTRAZIONE]: Room completa, invio message");
            String uuid = Manager.getInstance().getMyHost().getUuid();
            Manager.getInstance().setRoom(this.room);
            Message m = new Message(uuid,this.room);
            try {
                Manager.getInstance().getCommunication().getNextHostInterface().send(m);
            } catch (RemoteException e) {
                System.out.println("# REMOTE EXCEPTION # in ServerRegistration.addPlayer ");
            } catch (NotBoundException e) {
                System.out.println("# NOT BOUND EXCEPTION # in ServerRegistration.addPlayer ");
            }

        }
    }

    public int getNumActivePlayers() throws RemoteException {
        return this.room.getCurrentPlayers();
    }

    public Room getRoom() throws RemoteException {
        return this.room;
    }

}
