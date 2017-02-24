package UnoRMI;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;

/**
 * Created by angelo on 21/02/17.
 */
public class CrashManager {

    private static CrashManager instance;

    public static CrashManager getInstance() {
        if(instance == null)
            instance = new CrashManager();
        return instance;
    }

    /*ripara crash notificando gli altri host e riparando ring e GUI*/
    public InterfaceCommunication repairCrash(Player p) throws NotBoundException, ServerNotActiveException, RemoteException {
        System.out.println("[CRASH MANAGER] Host " + p.getHost().getIp() + " crashed!");
        Manager.getInstance().getRoom().removePlayer(p);
        String uuid = Manager.getInstance().getMyHost().getUuid();
        Message m = new Message(uuid, p);
        Manager.getInstance().getCommunication().getNextHostInterface().send(m);
        if(Manager.getInstance().getRoom().getCurrentPlayers()==1)
            Manager.getInstance().setWinner(Manager.getInstance().getMyPlayer().getId());

        return Manager.getInstance().getCommunication().getNextHostInterface();
    }

    /*riceve notifica del crash e ripara ring e GUI*/
    public void repairRing(Player p) {
        System.out.println("[CRASH NOTIFIED] Host " + p.getHost().getIp() + " crashed!");
        Manager.getInstance().getRoom().removePlayer(p);
        //todo Manager.getInstance().getGUI.refreshGui
    }


}
