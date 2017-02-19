package UnoUI;

/**
 * Created by TheNexus on 19/02/17.
 */

import com.badlogic.gdx.*;

public class UnoUIContext extends Game {

    public int counter = 0;

    public void create() {
        this.setScreen(new UnoUIMain(this));
    }

}