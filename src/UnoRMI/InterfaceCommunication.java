package UnoRMI;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by angelo on 20/02/17.
 */
public interface InterfaceCommunication extends Remote {
    void send(Message m) throws RemoteException;
}