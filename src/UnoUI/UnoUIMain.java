package UnoUI;

/**
 * Created by TheNexus on 19/02/17.
 */

import com.badlogic.gdx.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class UnoUIMain implements Screen {

    private SpriteBatch batch;
    private ShapeRenderer shaperenderer;
    private OrthographicCamera camera;

    private Game unoContext;

    public UnoUIMain(Game g) {
        unoContext = g;
        camera = new OrthographicCamera();
        camera.setToOrtho(false);
        camera.update();
        batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined.scale(1,1,1));
        shaperenderer = new ShapeRenderer();
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    public void dispose() {
        shaperenderer.dispose();
        batch.dispose();
    }

    public void hide() {}

    public void show() {}

    public void resize(int x,int y) {}

    @Override
    public void render(float delta) {
        processInput();
        Gdx.gl.glClearColor(0.6f,0.2f,0.0f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        shaperenderer.begin(ShapeRenderer.ShapeType.Filled);
        shaperenderer.setColor(0.0f,0.0f,0.0f,1.0f);
        shaperenderer.rect(0,0,Gdx.graphics.getWidth()/2,Gdx.graphics.getHeight()/2);
        shaperenderer.end();
    }

    private void processInput() {}

}