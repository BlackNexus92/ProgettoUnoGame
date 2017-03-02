package UnoRMI;

import UnoGame.GameState;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;

/**
 * Interfaccia di registrazione all'arena di gioco tramite RMI
 *
 * Created by angelo on 19/02/17.
 */
public interface InterfaceRegistration extends Remote {

    // aggiunge un player all'arena di gioco e restituisce lo stato di gioco attuale
    GameState addPlayer(Player p) throws RemoteException, InterruptedException, ServerNotActiveException;

    // notifica la ricezione dello stato di gioco e tenta di avviare il gioco se tutti i player sono pronti
    void receivedGameState() throws RemoteException, InterruptedException, ServerNotActiveException;

}
