package UnoRMI;

import UnoGame.GameState;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by angelo on 20/02/17.
 */
public class ServerRegistration extends UnicastRemoteObject implements InterfaceRegistration {

    private static final long serialVersionUID = 1L;
    /*stanza di gioco*/
    private Room room;
    /*stato del gioco*/
    private GameState gameState;
    /*serve per creare id dei player*/
    private int idPlayer = 1;

    public ServerRegistration(int n, Player p) throws RemoteException, InterruptedException, ServerNotActiveException {
        this.room = new Room(n);
        this.gameState = new GameState();
        this.addPlayer(p);
    }

    //todo
    public void addPlayer(Player p) throws RemoteException, InterruptedException, ServerNotActiveException {
        System.out.println("[REGISTRATION] Added Host IP: "+p.getHost().getIp()+" and PORT:"+p.getHost().getPort());
        p.setId(idPlayer);
        idPlayer++;
        this.room.addPlayer(p);
        //		Controller.getInstance().getStartPanel().informServerHostRegistred(this.room);

        if(this.room.isFull())
        {
            System.out.println("[REGISTRAZIONE]: Room is full, send configuration message");
            String uuid = Manager.getInstance().getMyHost().getUuid();
            Manager.getInstance().setRoom(this.room);
            //todo
            Manager.getInstance().setGameState(this.gameState);
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
