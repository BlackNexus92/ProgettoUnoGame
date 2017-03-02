package UnoGame;

import UnoRMI.Manager;
import UnoRMI.Message;
import UnoUI.TextureLoader;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.util.ArrayList;
import java.io.Serializable;
import UnoRMI.Player;

/**
 * Created by TheNexus on 20/02/17.
 */

// Classe che implementa lo stato di gioco vero e proprio relativo al singolo giocatore: include deck di gioco,
// arraylist di carte nella mano, id del turno corrente, ed altre informazioni
public class GameState implements Serializable {

    private static final long serialVersionUID = 1L;

// Deck attivo (aggiornato ad ogni mossa dei vari giocatori)
    private Deck deck;
// Mano del giocatore
    private ArrayList<Card> hand;
// Definisce le carte da pescare nel proprio turno
    private int cardsToDraw = 0;
// Numero di sequenza del turno di gioco
    private int seqNumber = 0;

// Identifica se il giocatore può scartare una carta nel proprio turno dopo aver pescato
// (ad es., in caso di blocchi, +2, o +4, l'attributo viene impostato a false)
    private boolean canPlay = false;
// Identifica se nel mio turno ho rimescolato il mazzo
    private boolean shuffled = false;

    public GameState()
    {
        deck = new Deck();
        hand = new ArrayList<Card>();
        hand.clear();
    }

// Predispone le variabili di gioco al turno successivo del giocatore
    public void setForTurn(boolean t) { canPlay=t; if(t) shuffled=false; }

// Metodi set e get generici
    public void setDeck(Deck d) { deck = d; }
    public Deck getDeck() { return deck; }

    public int getSeqNumber() { return seqNumber; }
    public void setSeqNumber(int s) { seqNumber=s; }

    public void setHand(ArrayList<Card> h) { hand = h; }
    public ArrayList<Card> getHand() { return hand; }

// Inizializza la mano del giocatore, considerando il suo id nella room ed il numero totale di giocatori
    public void initializeHand(int id,int nPlayers)
    {
        if(id<0 || id>=Deck.MAXPLAYERS || id>=nPlayers || nPlayers>Deck.MAXPLAYERS) return;
        hand.clear();
        int i;
// Sono scartate tutte le carte relative alle mani degli altri giocatori (con id minore o maggiore del proprio),
// e sono pescate solo le carte associate al proprio id.
        for(i=0;i<id;i++) deck.drawCards(Deck.HANDSIZE);
        hand.addAll(deck.drawCards(Deck.HANDSIZE));
        for(i=id+1;i<nPlayers;i++) deck.drawCards(Deck.HANDSIZE);
    }

// Metodo che scatena l'effetto, se presente, della carta in cima al mazzo, all'inizio del proprio turno.
// Da richiamare una sola volta all'inizio del turno del giocatore
    public void triggerTopCard()
    {
        if(!canPlay) return;
        Card topCard = deck.getTopCard();
        cardsToDraw = 0;
// Se la carta è di tipo speciale (blocco, +2, +4 o cambio direzione) ed il suo effetto deve ancora essere scatenato,
// attivo questa
        if(topCard.type!=Card.NUMTYPE && topCard.active)
        {
// Elaboro il numero di carte da pescare a seconda del tipo della topcard, ed imposto canPlay a false se questa
// comporta anche effetti di blocco
            if(topCard.type==Card.BLOCKTYPE) { canPlay = false; cardsToDraw = 0;}
            if(topCard.type==Card.PLUSTWOTYPE) { canPlay = false; cardsToDraw = 2; }
            if(topCard.type==Card.PLUSFOURTYPE) { canPlay = false; cardsToDraw = 4; }
            if(topCard.type==Card.CHANGEDIRTYPE && Manager.getInstance().getArena().getCurrentPlayers()==2) { canPlay = false; cardsToDraw = 0; }
        }
// Se invece posso giocare, in quanto l'effetto della topcard era già stato scatenato, elaboro se sia necessario
// pescare una carta, nel caso in cui non ne abbia di valide nella mano
        if(canPlay && !topCard.existsLegalMove(hand))
            cardsToDraw = 1;

// Disattivo la topcard, in quanto ne ho subito l'effetto
        topCard.active = false;

// Pesco le carte designate, a seconda dell'effetto della topcard e delle condizioni della mano
        ArrayList<Card> drawnCards = deck.drawCards(cardsToDraw);

// Se ho pescato un numero di carte inferiore a quello designato, vuol dire che il mazzo è esaurito:
// in tal caso procedo rimescolandolo, e pescando le carte restanti
        if(drawnCards.size()<cardsToDraw)
        {
            deck.swapDecks();
            drawnCards.addAll(deck.drawCards(cardsToDraw-drawnCards.size()));
            shuffled=true;
        }

// Aggiungo le carte pescate alla mano, e segnalo la necessità di aggiornare gli oggetti grafici associati alla mano
        hand.addAll(drawnCards);
        TextureLoader.setChanged();
        drawnCards.clear();
    }

// Metodo che, nel turno del giocatore, gli permette di giocare una carta o passare il turno, aggiornando
// anche lo stato di gioco e segnalando la mossa agli altri giocatori
    public void applyCard(Card c)
    {
        if(c==null) return;
// Inizializzo l'oggetto messaggio da inviare agli altri giocatori
        Message m = new Message(Manager.getInstance().getMyHost().getUuid(), null);
        m.setIdPlayer(Manager.getInstance().getMyPlayer().getId());

// Se ho effettivamente la possibilità di giocare, e la carta selezionata è compatibile con la topcard, gioco questa
        if(canPlay && deck.getTopCard().isCardCompatible(c))
        {
// Se ho selezionato una carta che in realtà non è presente nella mia mano, ritorno
            if(!removeCardFromHand(c)) return;
// Aggiorno lo stato del gioco e del deck
            c.active = true;
            deck.setTopCard(c);
            if(c.type==Card.CHANGEDIRTYPE) deck.setReverse(!deck.getReverse());

// Imposto i parametri del messaggio
            m.setSeqNumber(++seqNumber);
            m.setPayload(deck);
            m.setPlayerCards(hand.size());

// Imposto l'id del giocatore che avrà il turno dopo di me, a seconda del verso del giro di gioco
            if(Manager.getInstance().getGameState().getDeck().getReverse())
                m.setIdNextPlayer(Manager.getInstance().getArena().getPrevious(Manager.getInstance().getMyPlayer()).getId());
            else
                m.setIdNextPlayer(Manager.getInstance().getArena().getNext(Manager.getInstance().getMyPlayer()).getId());
            Manager.getInstance().setIdPlaying(m.getIdNextPlayer());

// Se la mia mano ha dimensione zero, ho vinto: in tal caso, nessuno potrà giocare dopo di me
            if(hand.size()==0) {
                Manager.getInstance().setWinner(Manager.getInstance().getMyPlayer().getId());
                Manager.getInstance().setStatusString("Hai vinto!");
                m.setIdNextPlayer(-1);
            }
            else {
                Player p = Manager.getInstance().getArena().getPlayerFromId(m.getIdNextPlayer());
                if(p!=null) Manager.getInstance().setStatusString(p.getUsername() + " gioca il suo turno...");
            }

            if(shuffled)
                m.type = Message.SHUFFLEMOVE;
            else
                m.type = Message.MOVE;

            shuffled=false;
            canPlay = false;

// Invio effettivo del messaggio
            try {
                Manager.getInstance().getCommunication().getNextHostInterface().send(m);
            } catch (RemoteException e) {
                System.out.println("# REMOTE EXCEPTION # in ServerCommunication.send ");
            } catch (NotBoundException e) {
                System.out.println("# NOT BOUND EXCEPTION # in ServerCommunication.send ");
            } catch (ServerNotActiveException e) {
                System.out.println("# SERVER NOT ACTIVE EXCEPTION # in ServerCommunication.send ");
            }

// Se invece non posso giocare una carta a causa di un effetto di blocco, o perché non possiedo nessuna carta valida,
// segnalo semplicemente il numero di carte pescate. Questo secondo caso viene gestito allo stesso modo di una mossa
// reale in modo da forzare il giocatore a cliccare su una qualsiasi carta, anche se non valida, per passare il turno.
        }
        else if(!canPlay || !deck.getTopCard().existsLegalMove(hand))
        {

            m.setSeqNumber(++seqNumber);
            m.setPayload(deck);
            m.setPlayerCards(hand.size());

            if(Manager.getInstance().getGameState().getDeck().getReverse())
                m.setIdNextPlayer(Manager.getInstance().getArena().getPrevious(Manager.getInstance().getMyPlayer()).getId());
            else
                m.setIdNextPlayer(Manager.getInstance().getArena().getNext(Manager.getInstance().getMyPlayer()).getId());
            Manager.getInstance().setIdPlaying(m.getIdNextPlayer());

            Player p = Manager.getInstance().getArena().getPlayerFromId(m.getIdNextPlayer());
            if(p!=null) Manager.getInstance().setStatusString(p.getUsername() + " gioca il suo turno...");

            if(shuffled)
                m.type = Message.SHUFFLEPASS;
            else
                m.type = Message.PASS;

            shuffled = false;
            canPlay = false;

// Invio effettivo del messaggio
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

// Metodo che rimuove una carta dalla mano del giocatore, se essa è presente. Ritorna true o false a seconda
// dell'esito dell'operazione.
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
