package UnoRMI;

import java.net.*;
import java.util.Enumeration;
import java.util.UUID;

/**
 * Classe per la gestione degli indirizzi IP
 *
 * Created by angelo on 20/02/17.
 */
public class AddressUtility {

    private static AddressUtility instance;

    public static AddressUtility getInstance() {
        if(instance == null)
            instance = new AddressUtility();
        return instance;
    }

    //Restituisce l'indirizzo IP dell'host
    public String getHostAddress() throws UnknownHostException, SocketException {

        for(Enumeration<NetworkInterface> ni = NetworkInterface.getNetworkInterfaces(); ni.hasMoreElements();) {
            NetworkInterface iface = ni.nextElement();
            if (iface.getName() != "lo") {
                for(Enumeration<InetAddress> addresses = iface.getInetAddresses(); addresses.hasMoreElements();){
                    InetAddress address = addresses.nextElement();

                    if(address instanceof Inet4Address){
                        String strHostIp = address.getHostAddress();
                        return strHostIp;
                    }
                }
            }
        }
        return null;
    }

    //Restituisce un identificativo per l'host
    public String getRandomUUID() {
        return UUID.randomUUID().toString();
    }



}
