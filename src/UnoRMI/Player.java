package UnoRMI;

import java.io.Serializable;

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
    private char id;

    public Player() { }

    public Player(String username, Host host) {
        this.username = username;
        this.host = host;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Host getHost() {
        return host;
    }

    public void setHost(Host host) {
        this.host = host;
    }

    public char getId() { return id; }

    public void setId(char id) {
        this.id = id;
    }
}
