package UnoRMI;

import java.io.Serializable;
import UnoGame.Deck;

/**
 * Created by angelo on 20/02/17.
 */
public class Player implements Serializable {

    private static final long serialVersionUID = 1L;
    /*username del player*/
    private String username;
    /*parametri di rete*/
    private Host host;
    /*id univoco del player*/
    private int id;

    private int nCards;

    public Player() { }

    public Player(String username, Host host) {
        this.username = username;
        this.host = host;
        this.nCards = Deck.HANDSIZE;
    }

    public String getUsername() {
        return username;
    }

    public int getnCards() { return nCards; }
    public void setnCards(int n) { nCards = n; }

    public void setUsername(String username) {
        this.username = username;
    }

    public Host getHost() {
        return host;
    }

    public void setHost(Host host) {
        this.host = host;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }
}
