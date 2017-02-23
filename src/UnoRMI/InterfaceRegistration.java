package UnoRMI;

import UnoGame.GameState;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;

/**
 * Created by angelo on 19/02/17.
 */
public interface InterfaceRegistration extends Remote {

    GameState addPlayer(Player p) throws RemoteException, InterruptedException, ServerNotActiveException;

    void receivedGamestate() throws RemoteException, InterruptedException, ServerNotActiveException;

    int getNumActivePlayers() throws RemoteException;

    Room getRoom() throws RemoteException;
}
