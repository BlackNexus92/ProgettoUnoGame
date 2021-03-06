package UnoRMI;

import UnoGame.GameState;

import java.util.Timer;

/**
 * Classe che gestisce interamente ogni aspetto del player che sta giocando
 *
 * Created by angelo on 20/02/17.
 */
public class Manager {

    /*Singleton della classe Manager*/
    private static Manager instance;
    /*Arena attiva*/
    private Arena arena;
    /*Server di comunicazione dell'host corrente*/
    private ServerCommunication communication;
    /*Player*/
    private Player player;
    /*Stato di gioco*/
    private GameState gameState;
    /*Indica quale player sta giocando*/
    private int idPlaying;
    /*Indica l'id del vincitore*/
    private int winner;
    /*Timer per il richiamo di un task*/
    private Timer timer;
    /*Indica l'azione del giocatore che ha il turno*/
    private String statusString;


    public static Manager getInstance() {
        if(instance == null)
            instance = new Manager();
        return instance;
    }

    public String getStatusString() { if(statusString==null) return " "; else return this.statusString; }

    public void setStatusString(String s) { this.statusString = s; }

    public Player getMyPlayer() {
        return this.player;
    }

    public void setMyPlayer(Player p) {
        this.player = p;
    }

    public Host getMyHost() {
        return this.player.getHost();
    }

    public Arena getArena() {
        return this.arena;
    }

    public void setArena(Arena r) {
        this.arena = r;
    }

    public ServerCommunication getCommunication() {
        return this.communication;
    }

    public void setCommunication(ServerCommunication c) {
        this.communication = c;
    }

    public GameState getGameState() { return this.gameState; }

    public void setGameState(GameState g) { this.gameState = g; }

    public boolean isPlaying() { return (this.idPlaying==player.getId() && winner < 0); }

    public int getIdPlaying() { return idPlaying; }

    public void setWinner(int w) { winner=w; }

    public int getWinner() { return winner; }

    public void setIdPlaying(int b) { this.idPlaying = b; gameState.setForTurn(isPlaying()); }

    public synchronized void setTimer (Timer t) { this.timer = t; }

    public synchronized Timer getTimer() {return this.timer; }

    public String getIpFromUuid(String u) {
        for(int i = 0; i<this.arena.getPlayers().size(); i++) {
            if(this.arena.getPlayers().get(i).getHost().getUuid().equals(u))
                return this.arena.getPlayers().get(i).getHost().getIp();
        }
        return null;
    }

    public void setIdFromUuid() {
        for(int i = 0; i<this.arena.getPlayers().size(); i++) {
            if(this.arena.getPlayers().get(i).getHost().getUuid().equals(this.player.getHost().getUuid()))
                this.player.setId(this.arena.getPlayers().get(i).getId());
        }
    }
}
