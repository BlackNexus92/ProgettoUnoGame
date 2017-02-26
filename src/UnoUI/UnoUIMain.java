package UnoUI;

/**
 * Created by TheNexus on 19/02/17.
 */

import UnoGame.Deck;
import com.badlogic.gdx.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.Sprite;
import UnoRMI.Manager;
import UnoRMI.Room;
import UnoRMI.Player;
import UnoGame.GameState;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import UnoGame.Card;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

public class UnoUIMain implements Screen {

    private SpriteBatch batch;
    private ShapeRenderer shaperenderer;
    private OrthographicCamera camera;

    private Sprite bgSprite;
    private Sprite cardSprite;
    private Sprite topCardSprite;

    private BitmapFont pt22font;
    private GlyphLayout glyphLayout;

    private Game unoContext;

    private Manager manager;
    private GameState gamestate;
    private Room room;

    private CardBox cardBox;
    private int cardBoxW;
    private int cardBoxH;

    private Card chosenCard;
    private boolean choosingColor;

    private static final float topCardW = 133;
    private static final float topCardH = 200;

    private static final float cardW = 106;
    private static final float cardH = 160;

    private float delta;
    private float lastClick;

    public UnoUIMain(Game g) {
        unoContext = g;
        camera = new OrthographicCamera();
        camera.setToOrtho(false);
        camera.update();
        batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined.scale(1, 1, 1));
        shaperenderer = new ShapeRenderer();

        bgSprite = new Sprite(TextureLoader.loadBGTexture());
        bgSprite.setSize(DesktopLauncher.resX, DesktopLauncher.resY);
        bgSprite.setPosition(0, 0);

        cardSprite = new Sprite(TextureLoader.loadCoveredCardTexture());
        cardSprite.setSize(cardW, cardH);

        topCardSprite = new Sprite(TextureLoader.getTopCardTexture());
        topCardSprite.setSize(topCardW, topCardH);
        topCardSprite.setPosition(512 - topCardSprite.getWidth() / 2, 384 - topCardSprite.getHeight() / 2);

        pt22font = TextureLoader.get22ptFont();
        glyphLayout = new GlyphLayout();

        manager = Manager.getInstance();
        room = Manager.getInstance().getRoom();
        gamestate = manager.getGameState();
        TextureLoader.setTopCardTexture(gamestate.getDeck().getTopCard());
        topCardSprite.setRegion(TextureLoader.getTopCardTexture());

        cardBox = TextureLoader.getCardBox();
        cardBox.refreshPane(gamestate.getHand());
        cardBoxW = TextureLoader.cardBoxW;
        cardBoxH = TextureLoader.cardBoxH;

        delta = 0;
        lastClick = 0;
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void show() {}

    @Override
    public void dispose() {
        shaperenderer.dispose();
        batch.dispose();
        TextureLoader.dispose();
    }

    @Override
    public void resize(int x, int y) {
        float sx = (float) x / DesktopLauncher.resX;
        float sy = (float) y / DesktopLauncher.resY;
        camera = new OrthographicCamera();
        camera.setToOrtho(false);
        camera.update();
        batch.setProjectionMatrix(camera.combined.scale(sx, sy, 1));
    }

    @Override
    public void render(float delta) {
        gamestate = manager.getGameState();
        room = Manager.getInstance().getRoom();
        if(TextureLoader.hasChanged())
        {
            TextureLoader.setTopCardTexture(gamestate.getDeck().getTopCard());
            topCardSprite.setRegion(TextureLoader.getTopCardTexture());
            cardBox.refreshPane(gamestate.getHand());
        }
        processInput();
        Gdx.gl.glClearColor(0.6f, 0.2f, 0.0f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        bgSprite.draw(batch);
        glyphLayout.setText(pt22font, "Mazzo: " + gamestate.getDeck().size() + " carte");
        pt22font.draw(batch, glyphLayout, 10, 230);
        glyphLayout.setText(pt22font, "Mano: " + gamestate.getHand().size() + " carte");
        pt22font.draw(batch, glyphLayout, 1014 - glyphLayout.width, 230);
        glyphLayout.setText(pt22font,manager.getStatusString());
        pt22font.draw(batch,glyphLayout,512 - glyphLayout.width/2,230);
        setPlayerRing(batch,shaperenderer);
        topCardSprite.draw(batch);
        pt22font.setColor(1.0f,1.0f,1.0f,1.0f);
        batch.end();
        cardBox.drawAndAct(delta);
        Gdx.gl.glEnable(Gdx.gl20.GL_BLEND);
        shaperenderer.begin(ShapeRenderer.ShapeType.Filled);
        drawColorSignal(shaperenderer);
        if (choosingColor)
            drawColorChooser(shaperenderer);
        shaperenderer.end();
        Gdx.gl.glDisable(Gdx.gl20.GL_BLEND);
    }

    private void processInput() {
        delta += 1000 * Gdx.graphics.getDeltaTime();
        int x = Gdx.input.getX();
        int y = Gdx.graphics.getHeight() - Gdx.input.getY();

        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Manager.getInstance().isPlaying() && delta - lastClick > 250) {
            lastClick = delta;
            if (choosingColor) {
                if (y >= 0 && y < cardBoxH) {
                    if (x < 0) x = 0;
                    else if (x >= cardBoxW) x = cardBoxW - 1;
                    chosenCard.color = (x * 4) / cardBoxW;
                    choosingColor = false;
                }
            } else {
                chosenCard = cardBox.selectedCard(x, y);
                if (chosenCard != null && (chosenCard.type == Card.CHANGECOLTYPE || chosenCard.type == Card.PLUSFOURTYPE))
                    choosingColor = true;
                else if (chosenCard != null)
                    choosingColor = false;
            }
        }
        if (chosenCard != null && !choosingColor) {
            gamestate.applyCard(chosenCard);
            chosenCard = null;
            choosingColor = false;
        }
    }

    private void drawColorSignal(ShapeRenderer sr)
    {
        int color = gamestate.getDeck().getTopCard().color;
        if(color == Card.RED)
            sr.setColor(1.0f,0.0f,0.0f,0.7f);
        else if(color == Card.YELLOW)
            sr.setColor(1.0f,1.0f,0.0f,0.7f);
        else if(color == Card.GREEN)
            sr.setColor(0.0f,1.0f,0.0f,0.7f);
        else if(color == Card.BLUE)
            sr.setColor(0.0f,0.0f,1.0f,0.7f);
        shaperenderer.rect(topCardSprite.getX()+topCardSprite.getWidth()+10,topCardSprite.getY(),30,30);
    }

    private void drawColorChooser(ShapeRenderer sr)
    {
        sr.setColor(1.0f,0.0f,0.0f,0.7f);
        sr.rect(0,0,cardBoxW/4,cardBoxH);
        sr.setColor(1.0f,1.0f,0.0f,0.7f);
        sr.rect(cardBoxW/4,0,cardBoxW/4,cardBoxH);
        sr.setColor(0.0f,1.0f,0.0f,0.7f);
        sr.rect(cardBoxW/2,0,cardBoxW/4,cardBoxH);
        sr.setColor(0.0f,0.0f,1.0f,0.7f);
        sr.rect(cardBoxW*3/4,0,cardBoxW/4,cardBoxH);
    }

    private void setPlayerRing(SpriteBatch batch,ShapeRenderer sr)
    {
        ArrayList<Player> players = room.getPlayers();
        int id = 1,i=0,playerID;
        Player p;
        playerID = Manager.getInstance().getMyPlayer().getId();
// Ciclo fino a trovare l'istanza del player nell'arraylist
        while(i<players.size()) {
            p = players.get(i++);
            if(p.getId()==playerID)
                break; }
// Disegno i players successivi al giocatore
        while(i<players.size()) {
            p = players.get(i);
            drawPlayerRing(batch,sr,p,id++,players.size()); }
// Disegno i restanti players successivi al giocatore, riavvolgendo l'iteratore
        i = 0;
        while(i<players.size()) {
            p = players.get(i);
            if(p.getId()==playerID)
                break;
            drawPlayerRing(batch,sr,p,id++,players.size()); }
    }

    private void drawPlayerRing(SpriteBatch batch,ShapeRenderer sr, Player p, int i, int nPlayers)
    {
        float xc,yc;
        xc = 75 + 874*i/(nPlayers);
        yc = 650;
        cardSprite.setPosition(xc-cardSprite.getWidth()/2,yc-cardSprite.getHeight()/2);
        cardSprite.draw(batch);
        setFontColorFromID(pt22font,p.getId());
        glyphLayout.setText(pt22font,p.getUsername());
        pt22font.draw(batch,glyphLayout,xc-glyphLayout.width/2,yc-80);
        glyphLayout.setText(pt22font,p.getnCards()+" Carte");
        pt22font.draw(batch,glyphLayout,xc-glyphLayout.width/2,yc-100);
        if(p.getnCards()>1)
        {
            cardSprite.setPosition(xc-cardSprite.getWidth()/2 + 7,yc-cardSprite.getHeight()/2 + 7);
            cardSprite.draw(batch);
        }
        if(manager.getIdPlaying()==p.getId())
        {
            batch.end();
            Gdx.gl.glEnable(Gdx.gl20.GL_BLEND);
            sr.begin(ShapeRenderer.ShapeType.Filled);
            drawTurnHighlight(sr,i,nPlayers);
            sr.end();
            Gdx.gl.glDisable(Gdx.gl20.GL_BLEND);
            batch.begin();
        }
    }

    private void setFontColorFromID(BitmapFont font,int id)
    {
        if(id==0)
            font.setColor(1.0f,0.0f,0.0f,1.0f);
        else if(id==1)
            font.setColor(0.0f,1.0f,0.0f,1.0f);
        else if(id==2)
            font.setColor(0.0f,0.0f,1.0f,1.0f);
        else if(id==3)
            font.setColor(0.0f,1.0f,1.0f,1.0f);
        else if(id==4)
            font.setColor(1.0f,0.0f,1.0f,1.0f);
        else if(id==5)
            font.setColor(1.0f,1.0f,0.0f,1.0f);
        else if(id==6)
            font.setColor(1.0f,1.0f,1.0f,1.0f);
        else if(id==7)
            font.setColor(0.0f,0.0f,0.0f,1.0f);
        else
            font.setColor(0.5f,0.5f,0.5f,1.0f);
    }

    private void drawTurnHighlight(ShapeRenderer sr,int id,int nPlayers)
    {
        float xc,yc;
        xc = 75 + 874*id/(nPlayers);
        yc = 650;
        sr.setColor(0.5f,0.5f,0.5f,0.6f);
        sr.rect(xc-5-cardSprite.getWidth()/2,yc-cardSprite.getHeight()/2,cardSprite.getWidth()+15,cardSprite.getHeight()+10);
    }
}