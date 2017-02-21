package UnoRMI;

import java.rmi.RemoteException;
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

    public ServerRegistration(int n, Player p) throws RemoteException {
        this.room = new Room(n);
        this.addPlayer(p);
    }

    //todo
    public void addPlayer(Player p) throws RemoteException {
        p.setId((char) idPlayer );
        idPlayer++;
        this.room.addPlayer(p);
        if(this.room.isFull())
        {
            System.out.println("[REGISTRAZIONE]: room completa, invio message");
            String uuid = Manager.getInstance().getMyHost().getUuid();
            Manager.getInstance().setRoom(this.room);
            Message m = new Message(uuid,this.room);
            try {
                Manager.getInstance().getCommunication().send(m);
            } catch (RemoteException e) {
                System.out.println("# REMOTE EXCEPTION # in ServerRegistration.addPlayer ");
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
