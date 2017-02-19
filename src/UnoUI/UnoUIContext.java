package UnoUI;

/**
 * Created by TheNexus on 19/02/17.
 */

import com.badlogic.gdx.*;

public class UnoUIContext extends Game {
    
    public void create() {
        this.setScreen(new UnoUIMain(this));
    }

}