package UnoGame;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by TheNexus on 19/02/17.
 */

// Classe atta a codificare le carte del gioco di Uno, ed i relativi vincoli
public class Card implements Serializable {

    private static final long serialVersionUID = 1L;

// Costanti di tipo relative alle carte
    public static final int NUMTYPE = 0;
    public static final int BLOCKTYPE = 1;
    public static final int CHANGEDIRTYPE = 2;
    public static final int PLUSTWOTYPE = 3;
    public static final int PLUSFOURTYPE = 4;
    public static final int CHANGECOLTYPE = 5;

// Costanti di colore relative alle carte
    public static final int RED = 0;
    public static final int BLUE = 3;
    public static final int GREEN = 2;
    public static final int YELLOW = 1;
    public static final int COLORLESS = 4;

// Colore, numero e tipo della carta
    public int color;
    public int number;
    public int type;
// Determina se la carta e' attiva, ossia il suo effetto deve ancora scatenarsi
    public boolean active;

    public Card()
    {
        color = RED;
        number = 0;
        type = NUMTYPE;
        active = true;
    }

    public Card(int c,int n,int t)
    {
        color = c;
        number = n;
        type = t;
        active = true;
    }

// Determina se la carta b puo' essere sovrapposta a questa carta
    public boolean isCardCompatible(Card b)
    {
        if(this.color==b.color || b.type==Card.PLUSFOURTYPE || b.type==Card.CHANGECOLTYPE) return true;
        if(b.type!=Card.NUMTYPE && b.type==this.type) return true;
        if(this.type==NUMTYPE && b.type==NUMTYPE && (this.number==b.number)) return true;
        return false;
    }

// Determina se almeno una carta dalla mano di input e' compatibile con quella descritta
    public boolean existsLegalMove(ArrayList<Card> hand)
    {
        for(int i=0;i<hand.size();i++)
            if(this.isCardCompatible(hand.get(i))) return true;
        return false;
    }
}
