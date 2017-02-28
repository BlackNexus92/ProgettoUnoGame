package UnoGame;

import UnoUI.TextureLoader;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by TheNexus on 20/02/17.
 */

// Classe atta a memorizzare il deck di gioco, ossia il mazzo di carte (sia pescabili, che già scartate), e la carta
// "top" attualmente attiva, e tutte le dinamiche associate
public class Deck implements Serializable {

    private static final long serialVersionUID = 1L;

// Costanti relative ai vincoli del gioco
    public static final int MAXPLAYERS = 15;
    public static final int DECKSIZE = 108;
    public static final int HANDSIZE = 7;

// Il mazzo di carte pescabili
    private ArrayList<Card> activeDeck;
// Il mazzo delle carte scartate
    private ArrayList<Card> discardedDeck;
// Determina il verso attuale del giro - cambia se viene giocata una carta reverse
    private boolean reverse = false;
// Oggetto carta attualmente attivo, e sulla quale vanno effettuate mosse
    private Card topCard;

    public Deck()
    {
        activeDeck = new ArrayList<Card>();
        discardedDeck = new ArrayList<Card>();
        topCard = new Card();
        buildDeck();
    }

// Metodo che costruisce il deck iniziale di 108 carte secondo le regole ufficiali. Il deck viene elaborato, mescolato,
// e viene poi selezionata una carta attiva da porre in cima al mazzo.
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
// Mescolo il deck ed identifico la carta da porre in cima al mazzo
        shuffleDeck(activeDeck);
        findTopCard(activeDeck);
    }

// Scambia il mazzo attivo con quello scartato, da richiamare quando il mazzo delle carte pescabili viene esaurito
    public void swapDecks()
    {
        ArrayList<Card> temp = activeDeck;
        activeDeck = discardedDeck;
        discardedDeck = temp;
        shuffleDeck(activeDeck);
    }

// Metodo che identifica ed assegna la carta "top" dal mazzo di input; ignora tutte le carte speciali, e seleziona
// la prima carta numero trovata
    private void findTopCard(ArrayList<Card> deck)
    {
        Card top;
        int i=0;
        if(deck.size()==0) {topCard=null; return;}
// Ciclo finché non individuo una carta colore
        do {
            top = deck.get(i);
            i++;
        }
        while(top.type!=Card.NUMTYPE && i<deck.size());
// Assegno la carta trovata come topCard, e la elimino dal mazzo
        if(top.type==Card.NUMTYPE) {
            topCard = top;
            deck.remove(i - 1);
        }
        else
            topCard = null;
    }

// Mescola il mazzo di carte, effettuando n/2 scambi casuali
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
// Se il mazzo è vuoto, o il numero di carte non è valido, ritorno
        if(n<=0 || activeDeck.size()==0) return cards;
// Seleziono la sublist di lunghezza massima (rispetto ad n) consentita dal deck
        if(activeDeck.size()<n)
            deckSublist = activeDeck.subList(0, activeDeck.size());
        else
            deckSublist = activeDeck.subList(0, n);
// Aggiungo gli elementi della sublist all'arraylist da ritornare, elimino le carte corrispondenti dal deck,
// e ritorno
        cards.addAll(deckSublist);
        deckSublist.clear();
        return cards;
    }

// Metodi set e get generici
    public int size() { return activeDeck.size(); }

    public boolean getReverse() { return reverse; }
    public void setReverse(boolean r) { reverse = r; }

    public Card getTopCard() { return topCard; }

// Imposta la nuova carta top. Viene verificato che quella nuova sia compatibile con la precedente secondo le regole
// di gioco; dopo l'assegnazione, la carta precedente viene aggiunta al mazzo delle carte scartate.
    public void setTopCard(Card c)
    {
        if(topCard.isCardCompatible(c))
        {
            discardedDeck.add(topCard);
            topCard=c;
// Segnala al TextureLoader che la texture relativa alla carta top deve essere aggiornata
            TextureLoader.setChanged();
        }
    }
}
