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
import com.badlogic.gdx.graphics.Texture;
import UnoGame.Card;
import UnoGame.Deck;

import java.util.ArrayList;

public class UnoUIMain implements Screen {

    private SpriteBatch batch;
    private ShapeRenderer shaperenderer;
    private OrthographicCamera camera;

    private Sprite bgSprite;

    private BitmapFont pt22font;
    private String strbuf;

    private Game unoContext;

    private Deck deck;
    private CardBox cardBox;

    private static final float cardBoxW = 1024;
    private static final float cardBoxH = 210;

    public UnoUIMain(Game g) {
        unoContext = g;
        camera = new OrthographicCamera();
        camera.setToOrtho(false);
        camera.update();
        batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined.scale(1,1,1));
        shaperenderer = new ShapeRenderer();

        bgSprite = new Sprite(TextureLoader.loadBGTexture());
        bgSprite.setSize(DesktopLauncher.resX,DesktopLauncher.resY);
        bgSprite.setPosition(0,0);

        pt22font = TextureLoader.get22ptFont();

        deck = new Deck();

        cardBox = new CardBox(0,0,cardBoxW,cardBoxH);
        cardBox.refreshPane(deck.drawCards(20));
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    public void dispose() {
        shaperenderer.dispose();
        batch.dispose();
        TextureLoader.dispose();
    }

    public void hide() {}

    public void show() {}

    public void resize(int x,int y) {
        float sx = (float)x/DesktopLauncher.resX;
        float sy = (float)y/DesktopLauncher.resY;
        camera = new OrthographicCamera();
        camera.setToOrtho(false);
        camera.update();
        batch.setProjectionMatrix(camera.combined.scale(sx,sy,1));
        cardBox.setSize(sx*cardBoxW,sy*cardBoxH);
    }

    @Override
    public void render(float delta) {
        processInput();
        Gdx.gl.glClearColor(0.6f,0.2f,0.0f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        bgSprite.draw(batch);
        strbuf = "Mazzo: "+deck.getSize()+" carte";
        pt22font.draw(batch,strbuf,10,230);
        strbuf = "Mano: "+deck.getSize()+" carte";
        pt22font.draw(batch,strbuf,1024-160,230);
        batch.end();
        cardBox.drawAndAct(delta);
    }

    private void processInput()
    {
        int x = Gdx.input.getX();
        int y = Gdx.graphics.getHeight() - Gdx.input.getY();
        if(Gdx.input.isButtonPressed(Input.Buttons.LEFT))
        {
            cardBox.selectedCard(x,y);
        }
    }

}