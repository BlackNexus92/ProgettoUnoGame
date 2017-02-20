package UnoRMI;

/**
 * Created by angelo on 20/02/17.
 */
public class Manager {

    /*singleton della classe Manager*/
    private static Manager instance;
    /*Room attiva*/
    private Room room;
    /*Server di comunicazione dell'host corrente*/
    private ServerCommunication communication;
    /*Player*/
    private Player player;

    //todo aggiungere variabili e metodi getIpFromUuid

    public static Manager getInstance() {
        if(instance == null)
            return new Manager();
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


}
