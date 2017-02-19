package UnoUI;

/**
 * Created by TheNexus on 19/02/17.
 */

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;


public class DesktopLauncher {

    public static int resX = 1280;
    public static int resY = 720;

    public static void main (String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width=resX;
        config.height=resY;
        config.resizable=false;
        config.title = "DistributedUno";
        config.fullscreen = false;
        new LwjglApplication(new UnoUIContext(), config);
    }
}