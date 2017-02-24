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

        Message toSendMsg = null;
        if(Manager.getInstance().getIdPlaying() == p.getId()) {
            if (Manager.getInstance().getGameState().getReverse()) {
                toSendMsg = new Message(Manager.getInstance().getMyHost().getUuid(),
                        Manager.getInstance().getRoom().getPrevious(p).getId());
            } else {
                toSendMsg = new Message(Manager.getInstance().getMyHost().getUuid(),
                        Manager.getInstance().getRoom().getNext(p).getId());

            }
            toSendMsg.type = Message.TURN;
            Manager.getInstance().setIdPlaying((Integer) toSendMsg.getPayload());

            try {
                //todo testare se usare this or Manager.getInstance()
                Manager.getInstance().getCommunication().getNextHostInterface().send(toSendMsg);
                //this.getNextHostInterface().send(toSendMsg);
            } catch (RemoteException e) {
                System.out.println("# REMOTE EXCEPTION # in ServerCommunication.send ");
            } catch (NotBoundException e) {
                System.out.println("# NOT BOUND EXCEPTION # in ServerCommunication.send ");
            }
        }
        return Manager.getInstance().getCommunication().getNextHostInterface();
    }

    /*riceve notifica del crash e ripara ring e GUI*/
    public void repairRing(Player p) {
        System.out.println("[CRASH NOTIFIED] Host " + p.getHost().getIp() + " crashed!");
        Manager.getInstance().getRoom().removePlayer(p);
        //todo Manager.getInstance().getGUI.refreshGui
    }


}
