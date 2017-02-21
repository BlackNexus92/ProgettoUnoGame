package UnoRMI;

import UnoGame.GameState;

/**
 * Created by angelo on 20/02/17.
 */
public class Manager {

    /*Singleton della classe Manager*/
    private static Manager instance;
    /*Room attiva*/
    private Room room;
    /*Server di comunicazione dell'host corrente*/
    private ServerCommunication communication;
    /*Player*/
    private Player player;
    /**/
    private GameState gameState;
    /*Indica se il player sta giocando*/
    private boolean isPlaying;

    //todo aggiungere variabili?

    public static Manager getInstance() {
        if(instance == null)
            instance = new Manager();
        return instance;
    }

    public Player getMyPlayer() {
        return this.player;
    }

    public void setMyPlayer(Player p) {
        this.player = p;
    }

    public Host getMyHost() {
        return this.player.getHost();
    }

    public Room getRoom() {
        return this.room;
    }

    public void setRoom(Room r) {
        this.room = r;
    }

    public ServerCommunication getCommunication() {
        return this.communication;
    }

    public void setCommunication(ServerCommunication c) {
        this.communication = c;
    }

    public GameState getGameState() { return this.gameState; }

    public void setGameState(GameState g) { this.gameState = g; }

    public boolean getIsPlaying() { return this.isPlaying; }

    public void setIsPlaying(boolean b) { this.isPlaying = b; }

    public String getIpFromUuid(String u) {
        for(int i=0; i<this.room.getPlayers().size(); i++) {
            if(this.room.getPlayers().get(i).getHost().getUuid().equals(u))
                return this.room.getPlayers().get(i).getHost().getIp();
        }
        return null;
    }

    public void setIdFromUuid() {
        for(int i=0; i<this.room.getPlayers().size(); i++) {
            if(this.room.getPlayers().get(i).getHost().getUuid().equals(this.player.getHost().getUuid()))
                this.player.setId(this.room.getPlayers().get(i).getId());
        }
    }
}
