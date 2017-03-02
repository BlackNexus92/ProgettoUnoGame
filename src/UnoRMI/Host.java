package UnoRMI;

import java.io.Serializable;

/**
 * Classe che gestisce un host collegato alla rete durante il gioco
 *
 * Created by angelo on 20/02/17.
 */
public class Host implements Serializable {

    private static final long serialVersionUID = 2122441862583128094L;
    /*identificativo host*/
    private String uuid;
    /*indirizzo ip dell'host*/
    private String ip;
    /*numero di porta*/
    private int port;

    public Host() {
        this.uuid = AddressUtility.getInstance().getRandomUUID();
    }

    public Host(String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.uuid = AddressUtility.getInstance().getRandomUUID();
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
