package UnoGame;

import UnoRMI.Manager;
import UnoRMI.Message;
import UnoUI.TextureLoader;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.util.ArrayList;
import java.io.Serializable;

/**
 * Created by TheNexus on 20/02/17.
 */

public class GameState implements Serializable {

    private static final long serialVersionUID = 1L;

    private Deck deck;
    private ArrayList<Card> hand;
    private int cardsToDraw = 0;
    private int seqNumber = 0;

    private boolean canPlay = false;
    private boolean shuffled = false;

    public GameState()
    {
        deck = new Deck();
        hand = new ArrayList<Card>();
        hand.clear();
    }

    public void setForTurn(boolean t) { canPlay=t; if(t) shuffled=false; }

    public void setDeck(Deck d) { deck = d; }
    public Deck getDeck() { return deck; }

    public int getSeqNumber() { return seqNumber; }
    public void setSeqNumber(int s) { seqNumber=s; }

    public void setHand(ArrayList<Card> h) { hand = h; }
    public ArrayList<Card> getHand() { return hand; }

    public void initializeHand(int id,int nPlayers)
    {
        if(id<0 || id>=Deck.MAXPLAYERS || id>=nPlayers || nPlayers>Deck.MAXPLAYERS) return;
        hand.clear();
        int i;
        for(i=0;i<id;i++) deck.drawCards(Deck.HANDSIZE);
        hand.addAll(deck.drawCards(Deck.HANDSIZE));
        for(i=id+1;i<nPlayers;i++) deck.drawCards(Deck.HANDSIZE);
    }

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
            if(topCard.type==Card.CHANGEDIRTYPE && Manager.getInstance().getRoom().getCurrentPlayers()==2) { canPlay = false; cardsToDraw = 0; }
        }
        if(canPlay && !topCard.existsLegalMove(hand))
            cardsToDraw = 1;

        topCard.active = false;

        ArrayList<Card> drawnCards = deck.drawCards(cardsToDraw);

        if(drawnCards.size()<cardsToDraw)
        {
            deck.swapDecks();
            drawnCards.addAll(deck.drawCards(cardsToDraw-drawnCards.size()));
            shuffled=true;
        }

        hand.addAll(drawnCards);
        TextureLoader.setChanged();
        drawnCards.clear();
    }

    public void applyCard(Card c)
    {
        if(c==null) return;
        Message m = new Message(Manager.getInstance().getMyHost().getUuid(), null);
        m.setIdPlayer(Manager.getInstance().getMyPlayer().getId());

        if(canPlay && deck.getTopCard().isCardCompatible(c))
        {
            if(!removeCardFromHand(c)) return;
            c.active = true;
            deck.setTopCard(c);
            if(c.type==Card.CHANGEDIRTYPE) deck.setReverse(!deck.getReverse());
            if(hand.size()==0) Manager.getInstance().setWinner(Manager.getInstance().getMyPlayer().getId());

            m.setSeqNumber(++seqNumber);
            m.setPayload(deck);
            m.setPlayerCards(hand.size());

            if(Manager.getInstance().getGameState().getDeck().getReverse())
                m.setIdNextPlayer(Manager.getInstance().getRoom().getPrevious(Manager.getInstance().getMyPlayer()).getId());
            else
                m.setIdNextPlayer(Manager.getInstance().getRoom().getNext(Manager.getInstance().getMyPlayer()).getId());
            Manager.getInstance().setIdPlaying(m.getIdNextPlayer());

// BROADCAST MESSAGGIO CARTA GIOCATA E CARTE PESCATE
            if(shuffled)
                m.type = Message.SHUFFLEMOVE;
            else
                m.type = Message.MOVE;

            shuffled=false;
            canPlay = false;

            try {
                Manager.getInstance().getCommunication().getNextHostInterface().send(m);
            } catch (RemoteException e) {
                System.out.println("# REMOTE EXCEPTION # in ServerCommunication.send ");
            } catch (NotBoundException e) {
                System.out.println("# NOT BOUND EXCEPTION # in ServerCommunication.send ");
            } catch (ServerNotActiveException e) {
                System.out.println("# SERVER NOT ACTIVE EXCEPTION # in ServerCommunication.send ");
            }

        }
        else if(!canPlay || !deck.getTopCard().existsLegalMove(hand))
        {

            m.setSeqNumber(++seqNumber);
            m.setPayload(deck);
            m.setPlayerCards(hand.size());

            if(Manager.getInstance().getGameState().getDeck().getReverse())
                m.setIdNextPlayer(Manager.getInstance().getRoom().getPrevious(Manager.getInstance().getMyPlayer()).getId());
            else
                m.setIdNextPlayer(Manager.getInstance().getRoom().getNext(Manager.getInstance().getMyPlayer()).getId());
            Manager.getInstance().setIdPlaying(m.getIdNextPlayer());

// BROADCAST MESSAGGIO TURNO PASSATO E CARTE PESCATE
            if(shuffled)
                m.type = Message.SHUFFLEPASS;
            else
                m.type = Message.PASS;

            shuffled = false;
            canPlay = false;

            try {
                Manager.getInstance().getCommunication().getNextHostInterface().send(m);
            } catch (RemoteException e) {
                System.out.println("# REMOTE EXCEPTION # in ServerCommunication.send ");
            } catch (NotBoundException e) {
                System.out.println("# NOT BOUND EXCEPTION # in ServerCommunication.send ");
            } catch (ServerNotActiveException e) {
                System.out.println("# SERVER NOT ACTIVE EXCEPTION # in ServerCommunication.send ");
            }

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
