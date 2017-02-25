package UnoRMI;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.util.TimerTask;

/**
 * Created by angelo on 24/02/17.
 */
public class CheckTimer extends TimerTask {

    public void run() {
        try {
            //todo testare se usare this or Manager.getInstance()
            Manager.getInstance().getCommunication().getNextHostInterface();
            System.out.println(" CheckTimer ");
            //this.getNextHostInterface().send(toSendMsg);
        } catch (RemoteException e) {
            System.out.println("# REMOTE EXCEPTION # in CheckTimer ");
        } catch (NotBoundException e) {
            System.out.println("# NOT BOUND EXCEPTION # in CheckTimer ");
        } catch (ServerNotActiveException e) {
            e.printStackTrace();
        }
    }
}
