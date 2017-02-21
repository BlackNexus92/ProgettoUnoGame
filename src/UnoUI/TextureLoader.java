package UnoUI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import UnoGame.Card;

/**
 * Created by TheNexus on 21/02/17.
 */
public class TextureLoader {

    private static TextureRegion topCardTex;
    private static Texture deckTex;
    private static int tx;
    private static int ty;

    private static Texture bgTex;
    private static Texture ccTex;

    private static BitmapFont pt22font;

    private static final int blockX = 10;
    private static final int reverseX = 11;
    private static final int plustwoX = 12;
    private static final int wildX = 13;
    private static final int colchangeY = 0;
    private static final int plusfourY = 2;

    private static final int numRows = 4;
    private static final int numCols = 14;

    public static TextureRegion loadCardTexture(Card c)
    {
        if(deckTex==null)
        {
            deckTex = new Texture(Gdx.files.internal("res/unoDeckTexture.png"));
            tx = deckTex.getWidth()/numCols;
            ty = deckTex.getHeight()/numRows;
            deckTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
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

    public static void setTopCardTexture(Card c)
    {
        topCardTex = loadCardTexture(c);
    }

    public static TextureRegion getTopCardTexture()
    {
        if(topCardTex==null)
        {
            loadCoveredCardTexture();
            topCardTex = new TextureRegion(ccTex);
        }
        return topCardTex;
    }

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

    public static BitmapFont get22ptFont() {
        if(pt22font==null) {
            Texture texture = new Texture(Gdx.files.internal("res/arial22.png"));
            texture.setFilter(Texture.TextureFilter.Linear,Texture.TextureFilter.Linear);
            pt22font = new BitmapFont(Gdx.files.internal("res/arial22.fnt"), new TextureRegion(texture), false);
            //pt50font.getData().setScale(0.5f);
        }
        return pt22font;
    }

    public static void dispose()
    {
        deckTex.dispose();
        bgTex.dispose();
        pt22font.dispose();

        deckTex=null;
        bgTex=null;
        pt22font=null;
    }


}
