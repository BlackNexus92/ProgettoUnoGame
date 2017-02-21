package UnoGame;

import java.util.ArrayList;

/**
 * Created by TheNexus on 20/02/17.
 */

public class GameState {

    private Deck deck;
    private ArrayList<Card> hand;
    private boolean canPlay = false;
    private int cardsToDraw = 0;

    public GameState()
    {
        deck = new Deck();
        hand = new ArrayList<Card>();
        hand.clear();
    }

    public boolean canPlay() { return canPlay; }
    public void setCanPlay(boolean c) { canPlay=c; }

    public void setDeck(Deck d) { deck = d; }
    public Deck getDeck() { return deck; }

    public void setHand(ArrayList<Card> h) { hand = h; }
    public ArrayList<Card> getHand() { return hand; }

    public void triggerTopCard()
    {
        if(!canPlay) return;
        Card topCard = deck.getTopCard();
        cardsToDraw = 0;
        if(topCard.type!=Card.NUMTYPE && topCard.active)
        {
            if(topCard.type==Card.BLOCKTYPE) { canPlay = false; cardsToDraw = 0;}
            if(topCard.type==Card.PLUSTWOTYPE) { canPlay = false; cardsToDraw = 2; }
            if(topCard.type==Card.PLUSFOURTYPE) { canPlay = false; cardsToDraw = 4; }
        }
        if(!topCard.active && !topCard.existsLegalMove(hand))
        {
            cardsToDraw = 1;
        }

        topCard.active = false;

        ArrayList<Card> drawnCards = deck.drawCards(cardsToDraw);

        if(drawnCards.size()<cardsToDraw)
        {
            deck.swapDecks();
            drawnCards.addAll(deck.drawCards(cardsToDraw-drawnCards.size()));
// BROADCAST MESSAGGIO MAZZI SCAMBIATI
        }

        hand.addAll(drawnCards);
        drawnCards.clear();

// BROADCAST MESSAGGIO CARTA DISATTIVATA E CARTE PESCATE O TURNO CONCLUSO
    }

    public void applyCard(Card c)
    {
        if(canPlay && deck.getTopCard().isCardCompatible(c))
        {
            if(!removeCardFromHand(c)) return;
            c.active = true;
            deck.setTopCard(c);
            canPlay = false;
/*
            if(hand.size()==0)
// BROADCAST MESSAGGIO CARTA GIOCATA E VINCITORE
            else
// BROADCAST MESSAGGIO CARTA GIOCATA
*/
        }
        else if(canPlay && !deck.getTopCard().existsLegalMove(hand))
        {
            canPlay = false;

// BROADCAST MESSAGGIO TURNO PASSATO

        }
    }

    private boolean removeCardFromHand(Card c)
    {
        for(int i=0;i<hand.size();i++)
            if(c==hand.get(i))
            {
                hand.remove(i);
                return true;
            }
        return false;
    }


}
