package UnoRMI;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by angelo on 20/02/17.
 */
public class Room implements Serializable {

    private static final long serialVersionUID = 1L;
    /*lista di player*/
    private ArrayList<Player> players;
    /*numero di player*/
    private int numStartingPlayers;

    public Room() {
        this.players = new ArrayList<Player>();
    }

    public Room(int n) {
        this.numStartingPlayers = n;
        this.players = new ArrayList<Player>();
    }

    public int getNumStartingPlayers() {
        return this.numStartingPlayers;
    }

    public void setNumStartingPlayers(int n) {
        this.numStartingPlayers = n;
    }

    public int getCurrentPlayers() {
        return this.players.size();
    }

    public int getMissingPlayers() {
        return this.numStartingPlayers - this.getCurrentPlayers();
    }

    public boolean isFull() {
        if(this.getCurrentPlayers() == this.numStartingPlayers)
            return true;
        return false;
    }

    public ArrayList<Player> getPlayers() {
        return this.players;
    }

    public void addPlayer(Player p) {
        this.players.add(p);
    }

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

    public Player getPlayerFromId(int id) {
        for(int i=0;i<players.size();i++)
            if(players.get(i).getId()==id) return players.get(i);
        return null;
    }

}
