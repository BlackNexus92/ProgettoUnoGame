package UnoUI;

/**
 * Created by TheNexus on 20/02/17.
 */

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

// Classe che si occupa di renderizzare la parte dell'interfaccia relativa alla mano di carte del giocatore
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
        pane = new ScrollPane(table);
        pane.setSize(w,h);
        pane.setX(x);
        pane.setY(y);
        stage.addActor(pane);
    }

// Imposta dimensione e posizione del Box
    public void setSize(float w,float h) { stage.getViewport().setScreenSize((int)w,(int)h); }
    public void setXY(float x,float y) { pane.setX(x); pane.setY(y); }

// Aggiorna il Box di carte: per ciascuna carta nella mano, realizza un oggetto Image con la texture corrispondente e
// l'oggetto Card associato, per poi memorizzarlo nella Table all'interno del Box
    public void refreshPane(ArrayList<Card> hand)
    {
// Elimino tutti gli oggetti presenti nel CardBox, prima di aggiornarlo
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
// Dopo aver aggiunto tutti gli oggetti sulla stessa riga, chiudo la stessa aggiungendone una nuova
        table.row();
    }

// Data una coppia XY di coordinate schermo, restituisce l'oggetto Card (se presente) cliccato nel Box
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

// Restituisce l'oggetto Stage associato al Box
    public Stage getStage() {
        return stage;
    }

// Renderizza ed anima il CardBox
    public void drawAndAct(float delta) {
        stage.act(delta);
        stage.draw();
    }

// Libera la memoria associata al CardBox
    public void dispose() {
        stage.dispose();
        table.clear();
        pane.clearChildren();
        table=null;
        pane=null;
    }
}