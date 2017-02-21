package UnoRMI;

import java.io.Serializable;

/**
 * Created by angelo on 20/02/17.
 */
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;
    /*mittente del messaggio*/
    private String uuid;
    /*corpo del messaggio*/
    private Object payload;
    /*id del player*/
    private char idPlayer;

    public Message(String u, Object o) {
        this.uuid = u;
        this.payload = o;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }

    public char getIdPlayer() { return idPlayer; }

    public void setIdPlayer(char idPlayer) {
        this.idPlayer = idPlayer;
    }
}
