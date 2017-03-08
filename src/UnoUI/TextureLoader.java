package UnoUI;

/**
 * Created by TheNexus on 21/02/17.
 */

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import UnoGame.Card;

// Classe atta alla gestione statica delle risorse Texture e font, che sono allocate una sola volta
// per poi essere riutilizzate
public class TextureLoader {

// Oggetti Texture e variabili di appoggio
    private static TextureRegion topCardTex;
    private static Texture deckTex;
    private static int tx;
    private static int ty;

    private static Texture canPlayTex;
    private static Texture cannotPlayTex;
    private static Texture bgTex;
    private static Texture ccTex;
    private static boolean hasChanged = false;

// Oggetto font
    private static BitmapFont pt22font;

// CardBox per la memorizzazione e renderizzazione della mano del giocatore
    private static CardBox cardBox;
    public static final int cardBoxW = 1024;
    public static final int cardBoxH = 210;

// Costanti che permettono la localizzazione delle TextureRegion relative alle singole carte nella Texture del
// mazzo complessivo
    private static final int blockX = 10;
    private static final int reverseX = 11;
    private static final int plustwoX = 12;
    private static final int wildX = 13;
    private static final int colchangeY = 0;
    private static final int plusfourY = 2;
    private static final int numRows = 4;
    private static final int numCols = 14;


// Il flag hasChanged determina se la mano e/o la carta in cima al mazzo siano cambiate, cosicche' il thread di rendering
// debba ri-aggiornarle
    public synchronized static void setChanged() { hasChanged=true; }

    public synchronized static boolean hasChanged()
    {
        if(hasChanged==true)
        {
            hasChanged=false;
            return true;
        }
        else
            return false;
    }

// Recupera e memorizza la TextureRegion relativa alla particolare carta di input
    public static TextureRegion loadCardTexture(Card c)
    {
// Carica la Texture del deck, se non ancora in memoria
        if(deckTex==null)
        {
            deckTex = new Texture(Gdx.files.internal("res/unoDeckTexture.png"));
            tx = deckTex.getWidth()/numCols;
            ty = deckTex.getHeight()/numRows;
            deckTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
// Recupera la specifica TextureRegion dalla Texture a seconda della tipologia e numero della carta
        TextureRegion r = null;
        if(c.type==Card.PLUSFOURTYPE)
            r = new TextureRegion(deckTex,tx*wildX,ty*plusfourY,tx,ty);
        else if(c.type==Card.CHANGECOLTYPE)
            r = new TextureRegion(deckTex,tx*wildX,ty*colchangeY,tx,ty);
        else if(c.type==Card.BLOCKTYPE)
            r = new TextureRegion(deckTex,tx*blockX,ty*c.color,tx,ty);
        else if(c.type==Card.PLUSTWOTYPE)
            r = new TextureRegion(deckTex,tx*plustwoX,ty*c.color,tx,ty);
        else if(c.type==Card.CHANGEDIRTYPE)
            r = new TextureRegion(deckTex,tx*reverseX,ty*c.color,tx,ty);
        else if(c.type==Card.NUMTYPE)
            r = new TextureRegion(deckTex,tx*c.number,ty*c.color,tx,ty);
        return r;
    }

// Imposta la texture relativa alla carta in cima al mazzo, in base alla carta di input
    public static void setTopCardTexture(Card c)
    {
        topCardTex = loadCardTexture(c);
    }

// Restituisce l'oggetto TextureRegion corrente associato alla carta in cima al mazzo
    public static TextureRegion getTopCardTexture()
    {
        if(topCardTex==null)
        {
            loadCoveredCardTexture();
            topCardTex = new TextureRegion(ccTex);
        }
        return topCardTex;
    }

// Metodi per il recupero di altre risorse grafiche statiche: Texture di vario genere, ed il BitmapFont utilizzato

    public static Texture loadCoveredCardTexture()
    {
        if(ccTex==null)
        {
            ccTex = new Texture(Gdx.files.internal("res/coveredCardTexture.png"));
            ccTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
        return ccTex;
    }

    public static Texture loadBGTexture()
    {
        if(bgTex==null)
        {
            bgTex = new Texture(Gdx.files.internal("res/bgTexture.png"));
            bgTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
        return bgTex;
    }

    public static Texture loadCanPlayTexture()
    {
        if(canPlayTex==null)
        {
            canPlayTex = new Texture(Gdx.files.internal("res/canPlayTexture.png"));
            canPlayTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
        return canPlayTex;
    }

    public static Texture loadCannotPlayTexture()
    {
        if(cannotPlayTex==null)
        {
            cannotPlayTex = new Texture(Gdx.files.internal("res/cannotPlayTexture.png"));
            cannotPlayTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
        return cannotPlayTex;
    }

    public static BitmapFont get22ptFont() {
        if(pt22font==null) {
            Texture texture = new Texture(Gdx.files.internal("res/arial22.png"));
            texture.setFilter(Texture.TextureFilter.Linear,Texture.TextureFilter.Linear);
            pt22font = new BitmapFont(Gdx.files.internal("res/arial22.fnt"), new TextureRegion(texture), false);
        }
        return pt22font;
    }

// Restituisce l'oggetto CardBox associato alla mano del giocatore, anch'esso gestito in modo statico
    public static CardBox getCardBox() {
        if(cardBox==null) {
            cardBox = new CardBox(0, 0, cardBoxW, cardBoxH);
        }
        return cardBox;
    }

// Rilascia tutte le risorse grafiche presenti in memoria
    public static void dispose()
    {
        if(deckTex!=null) {
            deckTex.dispose();
            deckTex=null;
        }
        if(bgTex!=null) {
            bgTex.dispose();
            bgTex=null;
        }
        if(pt22font!=null) {
            pt22font.dispose();
            pt22font=null;
        }
        if(cardBox!=null) {
            cardBox.dispose();
            cardBox=null;
        }
        if(canPlayTex!=null) {
            canPlayTex.dispose();
            canPlayTex=null;
        }
        if(cannotPlayTex!=null) {
            cannotPlayTex.dispose();
            cannotPlayTex=null;
        }
    }


}
