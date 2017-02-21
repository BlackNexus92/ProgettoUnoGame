package UnoRMI;

import java.net.*;
import java.util.Enumeration;
import java.util.UUID;

/**
 * Created by angelo on 20/02/17.
 */
public class NetworkUtility {

    private static NetworkUtility instance;

    public static NetworkUtility getInstance() {
        if(instance == null)
            return new NetworkUtility();
        return instance;
    }

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

    public String getRandomUUID() {
        return UUID.randomUUID().toString();
    }



}
