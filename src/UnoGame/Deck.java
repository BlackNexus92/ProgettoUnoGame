package UnoGame;

import UnoUI.TextureLoader;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by TheNexus on 20/02/17.
 */

public class Deck implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int DECKSIZE = 108;
    public static final int HANDSIZE = 7;

    private ArrayList<Card> activeDeck;
    private ArrayList<Card> discardedDeck;
    private Card topCard;

    public Deck()
    {
        activeDeck = new ArrayList<Card>();
        discardedDeck = new ArrayList<Card>();
        topCard = new Card();
        buildDeck();
    }

    public void buildDeck()
    {
        discardedDeck.clear();
        activeDeck.clear();
        Card buf;
// Ciclo sui quattro colori
        for(int j=0;j<4;j++)
        {
// Aggiungo la carta 0, una per ogni colore
            buf = new Card(j,0,Card.NUMTYPE);
            activeDeck.add(buf);
// Aggiungo le carte numero e le carte speciali, due per ogni colore
            for(int k=0;k<2;k++)
            {
// Carte numero colorate
                for (int i = 1; i < 10; i++) {
                    buf = new Card(j, i, Card.NUMTYPE);
                    activeDeck.add(buf);
                }
// Carte speciali colorate
                buf = new Card(j,0,Card.BLOCKTYPE);
                activeDeck.add(buf);
                buf = new Card(j,0,Card.CHANGEDIRTYPE);
                activeDeck.add(buf);
                buf = new Card(j,0,Card.PLUSTWOTYPE);
                activeDeck.add(buf);
            }
// Aggiungo le carte wild non colorate, quattro per ogni tipo
            buf = new Card(Card.COLORLESS,0,Card.CHANGECOLTYPE);
            activeDeck.add(buf);
            buf = new Card(Card.COLORLESS,0,Card.PLUSFOURTYPE);
            activeDeck.add(buf);
        }
        shuffleDeck(activeDeck);
        findTopCard(activeDeck);
    }

// Scambia il mazzo attivo con quello scartato
    public void swapDecks()
    {
        ArrayList<Card> temp = activeDeck;
        activeDeck = discardedDeck;
        discardedDeck = temp;
        shuffleDeck(activeDeck);
    }

    private void findTopCard(ArrayList<Card> deck)
    {
        Card top;
        int i=0;
        if(deck.size()==0) {topCard=null; return;}
        do {
            top = deck.get(i);
            i++;
        }
        while(top.type!=Card.NUMTYPE && i<deck.size());
        if(top.type==Card.NUMTYPE) {
            topCard = top;
            deck.remove(i - 1);
        }
        else
            topCard = null;
    }

// Mescola il mazzo di carte
    private void shuffleDeck(ArrayList<Card> deck)
    {
        int a,b,s=deck.size()/2;
        Random gen = new Random();
        Card temp;
        for(int i=0;i<s;i++)
        {
            a = gen.nextInt(s);
            b = s + gen.nextInt(s);
            temp = deck.get(a);
            deck.set(a,deck.get(b));
            deck.set(b,temp);
        }
    }

// Estrae una o più carte dal mazzo attivo
    public ArrayList<Card> drawCards(int n)
    {
        ArrayList<Card> cards = new ArrayList<Card>(0);
        List<Card> deckSublist;
        cards.clear();
        if(n<=0 || activeDeck.size()==0) return cards;
        if(activeDeck.size()<n)
            deckSublist = activeDeck.subList(0, activeDeck.size());
        else
            deckSublist = activeDeck.subList(0, n);
        cards.addAll(deckSublist);
        deckSublist.clear();
        return cards;
    }

    public int size() { return activeDeck.size(); }

    public Card getTopCard() { return topCard; }

    public void setTopCard(Card c)
    {
        if(topCard.isCardCompatible(c))
        {
            discardedDeck.add(topCard);
            topCard=c;
            TextureLoader.setChanged();
        }
    }
}
