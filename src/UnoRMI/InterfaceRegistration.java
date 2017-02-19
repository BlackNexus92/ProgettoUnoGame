package UnoRMI;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by angelo on 19/02/17.
 */
public interface InterfaceRegistration  extends Remote {
    void addPlayer() throws RemoteException;
}
