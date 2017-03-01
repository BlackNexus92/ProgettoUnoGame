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

// Classe atta alla visualizzazione del pannello di configurazione iniziale del gioco
public class ConfigPanel implements ChangeListener, ActionListener {

// Width ed height della finestra
    private final int w = 500;
    private final int h = 170;
// Array di valori per i numeri possibili di giocatori
    Integer[] playerNums = { 1, 2, 3, 4, 5, 6, 7, 8};

// Elementi grafici Java Swing per la visualizzazione del pannello
    private JFrame jFrame;
    private JPanel panel;
    private JLabel usernameLabel;
    private JTextField usernameField;
    private JLabel hostIpLabel;
    private JTextField ipField;
    private JCheckBox hostCheckBox;
    private JLabel nplayersLabel;
    private JButton connectButton;
    private JComboBox nPlayersSelect;

// Variabili estratte dai campi dell'interfaccia, per la configurazione effettiva
    private String serverIP;
    private String username;
    private int nPlayers;


    public ConfigPanel() throws ParseException {
// Allocazione degli elementi grafici
        jFrame = new JFrame("DistributedUno: Configurazione");
        SpringLayout layout = new SpringLayout();
        panel = new JPanel(layout);
        usernameLabel = new JLabel("Nickname:");
        usernameField = new JTextField("Player"+((int)Math.floor(100*Math.random())),10);
        hostIpLabel = new JLabel("IP Host:      ");
        ipField = new JTextField("0.0.0.0", 10);
        ipField.setPreferredSize(new Dimension(500,20));
        hostCheckBox = new JCheckBox("Host della Partita");
        nplayersLabel = new JLabel(" - Players:");
        nPlayersSelect = new JComboBox(playerNums);
        connectButton = new JButton("Connessione");
        connectButton.setVerticalTextPosition(AbstractButton.CENTER);
        connectButton.setHorizontalTextPosition(AbstractButton.LEADING);

// Imposto le caratteristiche del JFrame e la relativa visibilità
        jFrame.setSize(w,h);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panel.setBackground(Color.WHITE);
        nplayersLabel.setVisible(false);
        nPlayersSelect.setVisible(false);

// Aggiunta di listener per l'interfaccia di configurazione
        hostCheckBox.addChangeListener(this);
        connectButton.addActionListener(this);

// Aggiunta di constraint per il posizionamento degli elementi a schermo
        layout.putConstraint(SpringLayout.WEST, usernameLabel, 10, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, usernameLabel, 22, SpringLayout.NORTH, panel);
        layout.putConstraint(SpringLayout.WEST, usernameField, 10, SpringLayout.EAST, hostIpLabel);
        layout.putConstraint(SpringLayout.NORTH, usernameField, 20, SpringLayout.NORTH, panel);
        layout.putConstraint(SpringLayout.WEST, hostIpLabel, 10, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, hostIpLabel, 32, SpringLayout.NORTH, usernameLabel);
        layout.putConstraint(SpringLayout.WEST, ipField, 10, SpringLayout.EAST, hostIpLabel);
        layout.putConstraint(SpringLayout.NORTH, ipField, 30, SpringLayout.NORTH, usernameField);
        layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, ipField, 0, SpringLayout.HORIZONTAL_CENTER, panel);
        layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, usernameField, 0, SpringLayout.HORIZONTAL_CENTER, panel);
        layout.putConstraint(SpringLayout.NORTH, hostCheckBox, 10, SpringLayout.SOUTH, ipField);
        layout.putConstraint(SpringLayout.WEST, hostCheckBox, 0, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, nplayersLabel, 14, SpringLayout.SOUTH, ipField);
        layout.putConstraint(SpringLayout.NORTH, nPlayersSelect, 11, SpringLayout.SOUTH, ipField);
        layout.putConstraint(SpringLayout.WEST, nplayersLabel, -1, SpringLayout.EAST, hostCheckBox);
        layout.putConstraint(SpringLayout.WEST, nPlayersSelect, 5, SpringLayout.EAST, nplayersLabel);


        layout.putConstraint(SpringLayout.NORTH, connectButton, 10, SpringLayout.SOUTH, ipField);
        layout.putConstraint(SpringLayout.EAST, connectButton, -10, SpringLayout.EAST, panel);

// Aggiungo gli elementi componenti dell'interfaccia al panel incluso nel JFrame
        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(hostIpLabel);
        panel.add(ipField);
        panel.add(hostCheckBox);
        panel.add(nplayersLabel);
        panel.add(nPlayersSelect);
        panel.add(connectButton);
        jFrame.add(panel);

        jFrame.setResizable(false);
        jFrame.setVisible(true);
    }


// Metodo di callback per il listener delle modifiche al checkbox Host
    @Override
    public void stateChanged(ChangeEvent e) {

        if (hostCheckBox.isSelected()) {
            nplayersLabel.setVisible(true);
            nPlayersSelect.setVisible(true);
            ipField.setEnabled(false);
        } else {
            nplayersLabel.setVisible(false);
            nPlayersSelect.setVisible(false);
            ipField.setEnabled(true);
        }
    }

// Metodo di callback per il listener delle azioni effettuate sul tasto di connessione
    @Override
    public void actionPerformed(ActionEvent e) {

// Se non vi sono errori di formattazione, proseguo impostando i parametri di gioco ed avviando la procedura di
// connessione, tramite il metodo connect
        if (errorCheck()) {
            connectButton.setEnabled(false);
            try {
                setUsername(usernameField.getText());
                if (hostCheckBox.isSelected()) {
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

// Metodo atto al controllo di errori di formattazione nei parametri inseriti dall'utente
    private boolean errorCheck() {
        boolean errorFree = true;
        String error = "";
        String username = usernameField.getText();
        String ip = ipField.getText();

// Errore nel caso di username vuoto
        if (username.isEmpty()) {
            error += "Inserire un username!\n";
            errorFree = false;
        }
// Errore nel caso di IP Host vuoto
        if (ip.isEmpty()) {
            error += "Inserire l'IP dell'Host!\n";
            errorFree = false;
        }
// Controllo errore nel caso di IP mal formattato
        else {
            String[] st = ip.split("\\.");
            boolean ipIncorrect = false;
            if (st.length < 4) {
                ipIncorrect = true;
            }
// Controllo errore nel caso di IP con valori non validi
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

// Metodi set e get generici

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

