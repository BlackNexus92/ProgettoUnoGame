package UnoRMI;

import java.io.Serializable;

/**
 * Classe che rappresenta il messaggio scambiato durante il gioco.
 * A seconda del tipo di messaggio, sono inizializzate determinate variabili
 *
 * Created by angelo on 20/02/17.
 */
public class Message implements Serializable {

    public static final int MOVE = 0;
    public static final int PASS = 1;
    public static final int SHUFFLEPASS = 2;
    public static final int SHUFFLEMOVE = 3;
    public static final int ARENA = 4;
    public static final int PLAYER = 5;

    private static final long serialVersionUID = 1L;
    /*mittente del messaggio*/
    private String uuid;
    /*corpo del messaggio*/
    private Object payload;
    /*id del player*/
    private int idPlayer;
    /*id player che ha il turno*/
    private int idNextPlayer;
    /*numero di sequenza del messaggio*/
    private int seqNumber;
    /*numero di carte del player che ha inviato il messaggio*/
    private int playerCards;
    /*indica il tipo del messaggio*/
    public int type;

    public Message(String u, Object o) {
        this.uuid = u;
        this.payload = o;
        seqNumber = -1;
        idNextPlayer = -1;
        playerCards = -1;
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

    public int getIdPlayer() { return idPlayer; }

    public void setIdPlayer(int idPlayer) {
        this.idPlayer = idPlayer;
    }

    public int getIdNextPlayer() { return idNextPlayer; }

    public void setIdNextPlayer(int d) { idNextPlayer=d; }

    public int getSeqNumber() { return seqNumber; }

    public void setSeqNumber(int s) { seqNumber=s; }

    public int getPlayerCards() { return playerCards; }

    public void setPlayerCards(int c) { playerCards = c; }
}
