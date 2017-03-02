package UnoRMI;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;

/**
 * Interfaccia dei metodi remoti che permettono la comunicazione dei vari player connessi
 *
 * Created by angelo on 20/02/17.
 */
public interface InterfaceCommunication extends Remote {
    // permette l'invio del messaggio in una rete ad anello
    void send(Message m) throws RemoteException, ServerNotActiveException;
}
