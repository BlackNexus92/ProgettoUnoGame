package UnoUI;

/**
 * Created by TheNexus on 22/02/17.
 */

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.text.ParseException;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.lang.Math;

public class ConfigPanel implements ChangeListener, ActionListener {

    /** GRANDEZZE FINESTRA DI IMPOSTAZIONI **/
    private final int w = 500;
    private final int h = 155;
    Integer[] playerNums = { 1, 2, 3, 4, 5, 6, 7, 8};
    /** ELEMENTI GRAFICI **/
    private JFrame jFrame;
    private JPanel panel;
    private JLabel usernameLabel;
    private JTextField usernameField;
    private JLabel serverIpLabel;
    private JTextField ipField;
    private JCheckBox serverCheckBox;
    private JLabel nplayersLabel;
    private JButton connectButton;
    private JComboBox nPlayersSelect;
    /** VARIABILI GLOBALI UTILI PER COMINCIARE LA PARTITA **/
    private String serverIP;
    private String username;
    private int nPlayers;

    public ConfigPanel() throws ParseException {
        // Instanziazione degli elementi grafici della finestra
        jFrame = new JFrame("DistributedUno: Configurazione");
        SpringLayout layout = new SpringLayout();
        panel = new JPanel(layout);
        usernameLabel = new JLabel("Nickname:");
        usernameField = new JTextField("Player"+((int)Math.floor(100*Math.random())),10);
        serverIpLabel = new JLabel("IP Host:      ");
        ipField = new JTextField("0.0.0.0", 10);
        ipField.setPreferredSize(new Dimension(500,20));
        serverCheckBox = new JCheckBox("Host della Partita");
        nplayersLabel = new JLabel(" - Players:");
        nPlayersSelect = new JComboBox(playerNums);

        connectButton = new JButton("Connessione");
        connectButton.setVerticalTextPosition(AbstractButton.CENTER);
        connectButton.setHorizontalTextPosition(AbstractButton.LEADING);

        // impostazione degli elementi grafici
        jFrame.setSize(w,h);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panel.setBackground(Color.WHITE);
        nplayersLabel.setVisible(false);
        nPlayersSelect.setVisible(false);

        // aggiunta dei listener alla checkbox ed al button di connessione
        serverCheckBox.addChangeListener(this);
        connectButton.addActionListener(this);

        // posizionamento degli elementi grafici
        layout.putConstraint(SpringLayout.WEST, usernameLabel, 10, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, usernameLabel, 22, SpringLayout.NORTH, panel);
        layout.putConstraint(SpringLayout.WEST, usernameField, 10, SpringLayout.EAST, serverIpLabel);
        layout.putConstraint(SpringLayout.NORTH, usernameField, 20, SpringLayout.NORTH, panel);
        layout.putConstraint(SpringLayout.WEST, serverIpLabel, 10, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, serverIpLabel, 32, SpringLayout.NORTH, usernameLabel);
        layout.putConstraint(SpringLayout.WEST, ipField, 10, SpringLayout.EAST, serverIpLabel);
        layout.putConstraint(SpringLayout.NORTH, ipField, 30, SpringLayout.NORTH, usernameField);
        layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, ipField, 0, SpringLayout.HORIZONTAL_CENTER, panel);
        layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, usernameField, 0, SpringLayout.HORIZONTAL_CENTER, panel);
        layout.putConstraint(SpringLayout.NORTH, serverCheckBox, 10, SpringLayout.SOUTH, ipField);
        layout.putConstraint(SpringLayout.WEST, serverCheckBox, 0, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, nplayersLabel, 14, SpringLayout.SOUTH, ipField);
        layout.putConstraint(SpringLayout.NORTH, nPlayersSelect, 11, SpringLayout.SOUTH, ipField);
        layout.putConstraint(SpringLayout.WEST, nplayersLabel, -1, SpringLayout.EAST, serverCheckBox);
        layout.putConstraint(SpringLayout.WEST, nPlayersSelect, 5, SpringLayout.EAST, nplayersLabel);
        layout.putConstraint(SpringLayout.SOUTH, connectButton, -25, SpringLayout.SOUTH, panel);
        layout.putConstraint(SpringLayout.EAST, connectButton, -10, SpringLayout.EAST, panel);

        // aggiunta degli elementi grafici al panel ed al frame principale
        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(serverIpLabel);
        panel.add(ipField);
        panel.add(serverCheckBox);
        panel.add(nplayersLabel);
        panel.add(nPlayersSelect);
        panel.add(connectButton);
        jFrame.add(panel);

        jFrame.setResizable(false);
        jFrame.setVisible(true);
    }


// listener della checkbox server
    @Override
    public void stateChanged(ChangeEvent e) {

        if (serverCheckBox.isSelected()) {
            nplayersLabel.setVisible(true);
            nPlayersSelect.setVisible(true);
            ipField.setEnabled(false);
        } else {
            nplayersLabel.setVisible(false);
            nPlayersSelect.setVisible(false);
            ipField.setEnabled(true);
        }
    }

    // listener del button di connessione
    @Override
    public void actionPerformed(ActionEvent e) {

        if (errorCheck()) {
            connectButton.setEnabled(false);

            try {
                setUsername(usernameField.getText());
                if (serverCheckBox.isSelected()) {
                    setServerIP("SERVER");
                    nPlayers = Integer.parseInt(nPlayersSelect.getSelectedItem().toString());
                    setNPlayers(nPlayers);
                } else {
                    setServerIP(ipField.getText());
                }
                DesktopLauncher.connect(this);

            } catch (RemoteException e1) {
               e1.printStackTrace();
            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            } catch (SocketException e1) {
                e1.printStackTrace();
            } catch (AlreadyBoundException e1) {
                e1.printStackTrace();
            } catch (NotBoundException e1) {
                e1.printStackTrace();
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            } catch (ServerNotActiveException e1) {
                e1.printStackTrace();
            }
        }
    }

    private boolean errorCheck() {
        boolean errorFree = true;
        String error = "";
        String username = usernameField.getText();
        String ip = ipField.getText();

        if (username.isEmpty()) {
            error += "Inserire un username!\n";
            errorFree = false;
        }
        if (ip.isEmpty()) {
            error += "Inserire l'IP dell'Host!\n";
            errorFree = false;
        }
        else {
            String[] st = ip.split("\\.");
            boolean ipIncorrect = false;
            if (st.length < 4) {
                ipIncorrect = true;
            }
            else {
                for (int i = 0; i < st.length; i++) {
                    try {
                        int value = Integer.parseInt(st[i]);
                        if (value < 0 || value > 255) {
                            ipIncorrect = true;
                        }
                    } catch (NumberFormatException e1) {
                        ipIncorrect = true;
                    }
                }
            }
            if (ipIncorrect) {
                error += "L'indirizzo IP non è valido!\n";
                errorFree = false;
            }
        }

        if (!errorFree) {
            JOptionPane.showMessageDialog(new JFrame(), error, "ATTENZIONE", JOptionPane.ERROR_MESSAGE);
        }

        return errorFree;
    }

    // GETTERS e SETTERS
    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }

    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }

    public String getServerIP() {
        return this.serverIP;
    }

    public void setNPlayers(int nPlayers) {
        this.nPlayers = nPlayers;
    }

    public int getNPlayers() {
        return this.nPlayers;
    }

    public JFrame getJFrame() { return this.jFrame; }
}

