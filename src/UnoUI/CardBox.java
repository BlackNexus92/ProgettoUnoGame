package UnoUI;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import java.util.ArrayList;
import UnoGame.Card;
import com.badlogic.gdx.Gdx;

/**
 * Created by TheNexus on 20/02/17.
 */

public class CardBox {

    private Stage stage;
    private ScrollPane pane;
    private Table table;

    public CardBox() {
        this(0,0,1024,210);
    }

    public CardBox(float x,float y,float w,float h)
    {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        table = new Table();
        table.padLeft(10);
        table.padRight(10);
        table.setFillParent(true);
        pane = new ScrollPane(table);
        pane.setSize(w,h);
        pane.setX(x);
        pane.setY(y);
        stage.addActor(pane);
    }

    public void setSize(float w,float h) { /*stage.getViewport().setScreenSize((int)w,(int)h);*/ }

    public void refreshPane(ArrayList<Card> hand)
    {
        table.clearChildren();
        Image img;
        for(int i=0;i<hand.size();i++)
        {
            Sprite s = new Sprite(TextureLoader.loadCardTexture(hand.get(i)));
            s.setSize(133,200);
            s.setPosition(0,0);
            img = new Image(new SpriteDrawable(s));
            img.setUserObject(hand.get(i));
            table.add(img);
        }
        table.row();
    }

    public Card selectedCard(int x,int y)
    {
        Actor a = stage.hit(x,y,true);
        Card c = null;
        if(a!=null) {
            c = (Card) a.getUserObject();
 //           if(c!=null)
 //               System.out.println("Num " + c.number + " type " + c.type + " col " + c.color);
        }
        return c;
    }

    public Stage getStage() {
        return stage;
    }

    public void drawAndAct(float delta) {
        stage.act(delta);
        stage.draw();
    }

    public void dispose() {
        stage.dispose();
        table.clear();
        pane.clearChildren();
        table=null;
        pane=null;
    }
}