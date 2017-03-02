package UnoRMI;

import UnoGame.GameState;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Classe che implementa il servizio di registrazione, in remoto, dei player.
 * Un host funge da server centrale per permettere agli altri player di iscriversi all'arena di gioco
 *
 * Created by angelo on 20/02/17.
 */
public class ServerRegistration extends UnicastRemoteObject implements InterfaceRegistration {

    private static final long serialVersionUID = 1L;
    /*stanza di gioco*/
    private Arena arena;
    /*stato del gioco*/
    private GameState gameState;
    /*serve per creare id dei player*/
    private int idPlayer = 0;
    /*visualizza l'indirizzo dell'host al quale connettersi per iniziare la partita*/
    private String serverIPString;

    // costruttore che inizializza l'arena di gioco, lo stato del gioco e altre caratteristiche della partita
    public ServerRegistration(int n, Player p) throws RemoteException, InterruptedException, ServerNotActiveException {
        this.arena = new Arena(n);
        this.gameState = new GameState();
        Manager.getInstance().setGameState(this.gameState);
        Manager.getInstance().setIdPlaying(0);
        Manager.getInstance().setWinner(-1);
        this.addPlayer(p);
    }

    // aggiunge un player all'arena di gioco e restituisce lo stato di gioco attuale
    public GameState addPlayer(Player p) throws RemoteException, InterruptedException, ServerNotActiveException {
        p.setId(idPlayer++);
        this.arena.addPlayer(p);
        try {
            this.serverIPString = "IP Macchina: " + AddressManager.getInstance().getHostAddress();
        } catch (UnknownHostException e1) {
            e1.printStackTrace();
        } catch (SocketException e1) {
            e1.printStackTrace();
        }
        Manager.getInstance().setStatusString("Giocatori registrati: "+this.arena.getCurrentPlayers()+"/"+this.arena.getNumStartingPlayers()+" - "+this.serverIPString);
        System.out.println("REGISTRATION: Added Host IP: "+p.getHost().getIp()+" and PORT:"+p.getHost().getPort());
        return Manager.getInstance().getGameState();
    }

    // tenta di avviare il gioco se tutti i player sono pronti e invia un messaggio contenente l'arena di gioco a cui connettersi
    public void receivedGameState() throws RemoteException, InterruptedException, ServerNotActiveException {
        if(this.arena.isFull())
        {
            System.out.println("REGISTRATION: Arena is full, send configuration message");
            String uuid = Manager.getInstance().getMyHost().getUuid();
            Manager.getInstance().setArena(this.arena);
            Message m = new Message(uuid,this.arena);
            m.setIdPlayer(Manager.getInstance().getMyPlayer().getId());
            m.type = Message.ARENA;
            try {
                Manager.getInstance().getCommunication().getNextHostInterface().send(m);
            } catch (RemoteException e) {
                System.out.println("*EXCEPTION*: RemoteException in receivedGameState ");
            } catch (NotBoundException e) {
                System.out.println("*EXCEPTION*: NotBoundException in receivedGameState ");
            }

        }
    }

}
