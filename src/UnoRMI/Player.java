package UnoRMI;

import java.io.Serializable;

/**
 * Created by angelo on 20/02/17.
 */
public class Player implements Serializable {

    private static final long serialVersionUID = 1L;
    /*username del player*/
    private String username;

    private Host host;
    private int id;

}
