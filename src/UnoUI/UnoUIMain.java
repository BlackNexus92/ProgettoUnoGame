package UnoUI;

/**
 * Created by TheNexus on 19/02/17.
 */

import com.badlogic.gdx.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.Sprite;
import UnoRMI.Manager;
import UnoGame.GameState;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import UnoGame.Card;

import java.util.ArrayList;

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
        gamestate = manager.getGameState();
        TextureLoader.setTopCardTexture(gamestate.getDeck().getTopCard());

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
        topCardSprite.setRegion(TextureLoader.getTopCardTexture());
        processInput();
        Gdx.gl.glClearColor(0.6f, 0.2f, 0.0f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        bgSprite.draw(batch);
        glyphLayout.setText(pt22font, "Mazzo: " + gamestate.getDeck().size() + " carte");
        pt22font.draw(batch, glyphLayout, 10, 230);
        glyphLayout.setText(pt22font, "Mano: " + gamestate.getHand().size() + " carte");
        pt22font.draw(batch, glyphLayout, 1014 - glyphLayout.width, 230);
        drawPlayerRing(batch);
        topCardSprite.draw(batch);
        batch.end();
        cardBox.drawAndAct(delta);
        Gdx.gl.glEnable(Gdx.gl20.GL_BLEND);
        shaperenderer.begin(ShapeRenderer.ShapeType.Filled);
        drawColorSignal(shaperenderer);
        if (choosingColor)
            drawColorChooser(shaperenderer);
        drawTurnHighlight(shaperenderer);
        shaperenderer.end();
        Gdx.gl.glDisable(Gdx.gl20.GL_BLEND);
    }

    private void processInput() {
        delta += 1000 * Gdx.graphics.getDeltaTime();
        int x = Gdx.input.getX();
        int y = Gdx.graphics.getHeight() - Gdx.input.getY();

// DA CAMBIARE CON INDICATORE DI TURNO
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && gamestate.canPlay() && delta - lastClick > 250) {
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
            cardBox.refreshPane(gamestate.getHand());
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

    private void drawPlayerRing(SpriteBatch batch)
    {
        int nPlayers = 6;
        float xc,yc;
        for(int i=0;i<nPlayers;i++)
        {
            xc = 75 + 874*i/(nPlayers-1);
            yc = 650;
            cardSprite.setPosition(xc-cardSprite.getWidth()/2,yc-cardSprite.getHeight()/2);
            cardSprite.draw(batch);
            glyphLayout.setText(pt22font,"Giocatore "+i);
            pt22font.draw(batch,glyphLayout,xc-glyphLayout.width/2,yc-80);
            glyphLayout.setText(pt22font,20+" Carte");
            pt22font.draw(batch,glyphLayout,xc-glyphLayout.width/2,yc-100);
            if(true)
            {
                cardSprite.setPosition(xc-cardSprite.getWidth()/2 + 7,yc-cardSprite.getHeight()/2 + 7);
                cardSprite.draw(batch);
            }
        }
    }

    private void drawTurnHighlight(ShapeRenderer sr)
    {
        int nPlayers = 6;
        float xc,yc;
        for(int i=0;i<nPlayers;i++)
        {
            if(i==3)
            {
                xc = 75 + 874*i/(nPlayers-1);
                yc = 650;
                sr.setColor(0.5f,0.5f,0.5f,0.6f);
                sr.rect(xc-5-cardSprite.getWidth()/2,yc-cardSprite.getHeight()/2,cardSprite.getWidth()+15,cardSprite.getHeight()+10);
            }
        }
    }
}