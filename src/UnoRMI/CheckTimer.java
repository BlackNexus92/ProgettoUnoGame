package UnoRMI;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.util.TimerTask;

/**
 * Classe che implementa un task eseguito periodicamente alla scadenza di un timer,
 * per rilevare eventuali crash nella rete
 *
 * Created by angelo on 24/02/17.
 */
public class CheckTimer extends TimerTask {

    public void run() {
        try {
            Manager.getInstance().getCommunication().getNextHostInterface();
        } catch (RemoteException e) {
            System.out.println("*EXCEPTION*: RemoteException in CheckTimer ");
        } catch (NotBoundException e) {
            System.out.println("*EXCEPTION*: NotBoundException in CheckTimer ");
        } catch (ServerNotActiveException e) {
            e.printStackTrace();
        }
    }
}
