package UnoRMI;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Classe che tiene traccia dei player attivi nell'arena di gioco
 *
 * Created by angelo on 20/02/17.
 */
public class Arena implements Serializable {

    private static final long serialVersionUID = 1L;
    /*lista di player*/
    private ArrayList<Player> players;
    /*numero di player di partenza*/
    private int numStartingPlayers;

    public Arena() {
        this.players = new ArrayList<Player>();
    }

    public Arena(int n) {
        this.numStartingPlayers = n;
        this.players = new ArrayList<Player>();
    }

    // restituisce il numero di player di partenza
    public int getNumStartingPlayers() {
        return this.numStartingPlayers;
    }

    public void setNumStartingPlayers(int n) {
        this.numStartingPlayers = n;
    }

    // restituisce il numero di player attualmente in gioco
    public int getCurrentPlayers() {
        return this.players.size();
    }

    public int getMissingPlayers() {
        return this.numStartingPlayers - this.getCurrentPlayers();
    }

    // controlla se tutti i partecipanti all'arena di gioco si sono connessi
    public boolean isFull() {
        if(this.getCurrentPlayers() == this.numStartingPlayers)
            return true;
        return false;
    }

    // restituisce la lista di player attivi nell'arena di gioco
    public ArrayList<Player> getPlayers() {
        return this.players;
    }

    // aggiunge un player all'arena di gioco
    public void addPlayer(Player p) {
        this.players.add(p);
    }

    // rimuove il player passato come parametro dall'arena di gioco
    public boolean removePlayer(Player p) {
        for(int i=0; i<this.players.size(); i++)
        {
            Player temp = this.players.get(i);
            if(temp.getHost().getIp().equals(p.getHost().getIp()) &&
                    temp.getHost().getPort() == p.getHost().getPort() &&
                    temp.getHost().getUuid().equals(p.getHost().getUuid()))
            {
                this.players.remove(i);
                return true;
            }
        }
        return false;
    }

    // restituisce il player successivo, nella configurazione ad anello della rete, a quello passato come parametro
    public Player getNext(Player p) {
        for(int i=0; i<this.players.size(); i++)
        {
            Player temp = this.players.get(i);
            if(temp.getHost().getIp().equals(p.getHost().getIp()) &&
                    temp.getHost().getPort() == p.getHost().getPort() &&
                    temp.getHost().getUuid().equals(p.getHost().getUuid()))
            {
                if(i == this.players.size()-1)
                    return this.players.get(0);

                return this.players.get(i+1);
            }
        }
        return null;
    }

    // restituisce il player precedente, nella configurazione ad anello della rete, a quello passato come parametro
    public Player getPrevious(Player p) {
        for(int i=0; i<this.players.size(); i++)
        {
            Player temp = this.players.get(i);
            if(temp.getHost().getIp().equals(p.getHost().getIp()) &&
                    temp.getHost().getPort() == p.getHost().getPort() &&
                    temp.getHost().getUuid().equals(p.getHost().getUuid()))
            {
                if(i == 0)
                    return this.players.get(this.players.size()-1);

                return this.players.get(i-1);
            }
        }
        return null;
    }

    // restituisce il player che ha un certo id, se è presente
    public Player getPlayerFromId(int id) {
        for(int i=0;i<players.size();i++)
            if(players.get(i).getId()==id) return players.get(i);
        return null;
    }

}
