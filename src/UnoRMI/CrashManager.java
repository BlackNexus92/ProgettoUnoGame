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
        Manager.getInstance().setStatusString("Rilevato crash di "+p.getUsername()+"!");
        Manager.getInstance().getRoom().removePlayer(p);
        String uuid = Manager.getInstance().getMyHost().getUuid();
        Message m = new Message(uuid, p);
        Manager.getInstance().getCommunication().getNextHostInterface().send(m);
        if(Manager.getInstance().getRoom().getCurrentPlayers()==1) {
            Manager.getInstance().setStatusString("Sei l'unico giocatore sopravvissuto, hai vinto!");
            Manager.getInstance().setWinner(Manager.getInstance().getMyPlayer().getId());
        }

        if(Manager.getInstance().getIdPlaying() == p.getId()) {
            Message m2 = new Message(Manager.getInstance().getMyHost().getUuid(),null);
            if (Manager.getInstance().getGameState().getDeck().getReverse()) {
                m2.setIdNextPlayer(Manager.getInstance().getRoom().getPrevious(p).getId());
            } else {
                m2.setIdNextPlayer(Manager.getInstance().getRoom().getNext(p).getId());
            }
            m2.type = Message.PASS;
            m2.setPayload(Manager.getInstance().getGameState().getDeck());
            m2.setSeqNumber(Manager.getInstance().getGameState().getSeqNumber());
            m2.setPlayerCards(Manager.getInstance().getGameState().getHand().size());
            m2.setIdPlayer(Manager.getInstance().getMyPlayer().getId());
            Manager.getInstance().setIdPlaying((Integer) m2.getIdNextPlayer());

            try {
                Manager.getInstance().getCommunication().getNextHostInterface().send(m2);
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
    }


}
