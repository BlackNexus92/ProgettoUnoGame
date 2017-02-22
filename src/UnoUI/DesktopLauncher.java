package UnoUI;

/**
 * Created by TheNexus on 19/02/17.
 */

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import UnoRMI.Manager;
import UnoGame.GameState;

import java.text.ParseException;


public class DesktopLauncher {

    public static int resX = 1024;
    public static int resY = 768;

    public static void main (String[] arg) {

        ConfigPanel sp;
        try { sp = new ConfigPanel(); }
        catch (ParseException e) {e.printStackTrace();}
 //       Manager.getInstance().setStartPanel(sp);
        bootGUI();
    }

    public static void bootGUI()
    {
// DA ELIMINARE
        Manager m = Manager.getInstance();
        GameState g = new GameState();
        g.setHand(g.getDeck().drawCards(7));
        g.setCanPlay(true);
        m.setGameState(g);
// *************
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width=resX;
        config.height=resY;
        config.resizable=false;
        config.title = "DistributedUno";
        config.fullscreen = false;
        new LwjglApplication(new UnoUIContext(), config);
    }

}