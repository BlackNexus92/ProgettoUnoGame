package UnoUI;

/**
 * Created by TheNexus on 19/02/17.
 */

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import UnoRMI.Manager;
import UnoGame.GameState;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ServerNotActiveException;
import java.text.ParseException;


import UnoRMI.Manager;
import UnoRMI.Host;
import UnoRMI.NetworkUtility;
import UnoRMI.ServerCommunication;
import UnoRMI.InterfaceRegistration;
import UnoRMI.Player;
import UnoRMI.ServerRegistration;

import java.text.ParseException;


public class DesktopLauncher {

    private static int PORT = 1099;
    private static Registry registry;

    public static int resX = 1024;
    public static int resY = 768;

    public static void main (String[] arg) {

        Manager m = Manager.getInstance();
        GameState g = new GameState();
        g.setTurn(false);
        m.setGameState(g);

        ConfigPanel sp;
        try { sp = new ConfigPanel(); }
        catch (ParseException e) {e.printStackTrace();}
 //       bootGUI();
    }

    public static void bootGUI()
    {
        new Thread(new Runnable() {
            public void run() {
                LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
                config.width=resX;
                config.height=resY;
                config.resizable=false;
                config.title ="DistributedUno";
                config.fullscreen =false;
                new LwjglApplication(new UnoUIContext(),config); }
        }).start();
    }

    /**
     * Connessione alla partita
     * @param sp finestra di configurazione
     * @throws RemoteException
     * @throws AlreadyBoundException
     * @throws UnknownHostException
     * @throws SocketException
     * @throws NotBoundException
     * @throws InterruptedException
     * @throws ServerNotActiveException
     */
    public static void connect(ConfigPanel sp) throws RemoteException, AlreadyBoundException, UnknownHostException, SocketException, NotBoundException, InterruptedException, ServerNotActiveException {
        String IP = sp.getServerIP();
        if (IP.equals("SERVER")) {
            bootGUI();
            int nPlayers = sp.getNPlayers();
            String username = sp.getUsername();
            Host myHost = new Host(NetworkUtility.getInstance().getHostAddress(), PORT);
            Player myPlayer = new Player(username, myHost);
            Manager.getInstance().setMyPlayer(myPlayer);
            startDeamon();
            startDeamonRegistration(nPlayers, myPlayer);
        }
        //se gioco da solo
        else if (IP.startsWith("1")) {
            bootGUI();
            startDeamon();
            InterfaceRegistration registrationServer = null;
            Registry register = LocateRegistry.getRegistry(IP, PORT);
            registrationServer = (InterfaceRegistration) register.lookup("RegistrationService");
            String username = sp.getUsername();
            Host myHost = new Host(NetworkUtility.getInstance().getHostAddress(), PORT);
            Player myPlayer = new Player(username, myHost);
            Manager.getInstance().setMyPlayer(myPlayer);
            Manager.getInstance().setGameState(registrationServer.addPlayer(myPlayer));
            System.out.println("[HOST REGISTRED]");
        }
    }

    /**
     * Inizializzazione del Demone server in ascolto
     * @throws UnknownHostException
     * @throws SocketException
     * @throws RemoteException
     * @throws AlreadyBoundException
     */
    public static void startDeamon() throws UnknownHostException, SocketException, RemoteException, AlreadyBoundException {
        System.setProperty("java.rmi.server.hostname", NetworkUtility.getInstance().getHostAddress());
        System.setProperty("java.rmi.disableHttp", "true");
        registry = LocateRegistry.createRegistry(PORT);
        System.out.println("[SERVER] IN ASCOLTO...");
        ServerCommunication server = new ServerCommunication();
        Manager.getInstance().setCommunication(server);
        registry.bind("Communication", server);
    }

    /**
     * Inizializzazione del demone server di registrazione in ascolto
     * @param nPlayers
     * @param myPlayer
     * @throws AccessException
     * @throws RemoteException
     * @throws AlreadyBoundException
     * @throws InterruptedException
     * @throws ServerNotActiveException
     */
    public static void startDeamonRegistration(int nPlayers, Player myPlayer) throws AccessException, RemoteException, AlreadyBoundException, InterruptedException, ServerNotActiveException {
        System.out.println("[REGISTRATION SERVICE] IN ASCOLTO...");
        ServerRegistration serverRegistration = new ServerRegistration(nPlayers, myPlayer);
        registry.bind("RegistrationService", serverRegistration);
    }

}