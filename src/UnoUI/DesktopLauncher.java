package UnoUI;

/**
 * Created by TheNexus on 19/02/17.
 */

import UnoRMI.*;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
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


public class DesktopLauncher {

// Porta di comunicazione e variabile Registry relativa al lookup RMI sull'host considerato
    private static int PORT = 1234;
    private static Registry registry;

// Risoluzione dell'applicazione
    public static int resX = 1024;
    public static int resY = 768;

    public static void main (String[] arg) {
// Creo una istanza del Gamestate, Player e Arena fittizia: non appena il giocatore si unira' ad una partita, o
// diventera' Server, queste saranno nuovamente allocate con i dati reali di gioco
        Manager m = Manager.getInstance();
        m.setGameState(new GameState());
        m.setMyPlayer(new Player());
        m.setArena(new Arena(1));
        m.setIdPlaying(-1);
        m.setWinner(-1);
        m.setStatusString("Gioco in fase di inizializzazione...");
// Abilito il pannello di configurazione del gioco
        ConfigPanel sp;
        try { sp = new ConfigPanel(); }
        catch (ParseException e) {e.printStackTrace();}
 //       bootGUI();
    }

// Metodo atto a creare il contesto OpenGL associato all'interfaccia grafica, quando questa deve essere avviata
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

// Metodo che recupera le informazioni memorizzate dal pannello di configurazione (Username, IP server) e tenta una connessione
// con lo stesso, oppure avvia il server di registrazione se sono io stesso il server
    public static void connect(ConfigPanel sp) throws RemoteException, AlreadyBoundException, UnknownHostException, SocketException, NotBoundException, InterruptedException, ServerNotActiveException {
        String IP = sp.getServerIP();
// Se sono il server di registrazione, avvio il relativo servizio ed aggiungo me stesso alla nuova Arena
        if (IP.equals("SERVER")) {
            int nPlayers = sp.getNPlayers();
            String username = sp.getUsername();
            Host myHost = new Host(AddressManager.getInstance().getHostAddress(), PORT);
            Player myPlayer = new Player(username, myHost);
            Manager.getInstance().setMyPlayer(myPlayer);
            startDeamon();
            startDeamonRegistration(nPlayers, myPlayer);
            sp.getJFrame().setVisible(false);
            bootGUI();
        }
// Altrimenti, tento una connessione con il server tramite il metodo addPlayer
        else if (IP.startsWith("1")) {
            startDeamon();
            InterfaceRegistration registrationServer = null;
            Registry register = LocateRegistry.getRegistry(IP, PORT);
            registrationServer = (InterfaceRegistration) register.lookup("RegistrationService");
            String username = sp.getUsername();
            Host myHost = new Host(AddressManager.getInstance().getHostAddress(), PORT);
            Player myPlayer = new Player(username, myHost);
// Il giocatore alloca il proprio oggetto Player (ma non ne imposta ancora l'ID), ed aggiunge se stesso alla Arena
            Manager.getInstance().setMyPlayer(myPlayer);
            Manager.getInstance().setGameState(registrationServer.addPlayer(myPlayer));
            registrationServer.receivedGameState();
            sp.getJFrame().setVisible(false);
//            System.out.println("REGISTRATION: Host "+ myHost.getIp() +" registered");
            bootGUI();
        }
    }

// Metodo che si occupa di inizializzare il servizio RMI in ascolto, relativo alla comunicazione di gioco
    public static void startDeamon() throws UnknownHostException, SocketException, RemoteException, AlreadyBoundException {
        System.setProperty("java.rmi.server.hostname", AddressManager.getInstance().getHostAddress());
        System.setProperty("java.rmi.disableHttp", "true");
        registry = LocateRegistry.createRegistry(PORT);
//        System.out.println("REGISTRATION: Server is waiting...");
        ServerCommunication server = new ServerCommunication();
        Manager.getInstance().setCommunication(server);
        registry.bind("Communication", server);
    }

// Metodo atto ad inizializzare il servizio RMI di registrazione, relativo al server, e richiamato dagli altri giocatori
    public static void startDeamonRegistration(int nPlayers, Player myPlayer) throws AccessException, RemoteException, AlreadyBoundException, InterruptedException, ServerNotActiveException {
        System.out.println("REGISTRATION: Server is waiting...");
        ServerRegistration serverRegistration = new ServerRegistration(nPlayers, myPlayer);
        registry.bind("RegistrationService", serverRegistration);
        serverRegistration.receivedGameState();
    }

}