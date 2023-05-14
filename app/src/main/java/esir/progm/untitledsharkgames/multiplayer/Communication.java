package esir.progm.untitledsharkgames.multiplayer;

import esir.progm.untitledsharkgames.Communication.Client;

/**
 * Classe permettant d'envoyer facilement un message via un serveur
 */
public class Communication {
    private static String IP = "";
    public static void setIpHost(String ip) {
        Communication.IP = ip;
    }
    public static void sendMessage(String message) {
        Client client = new Client(IP, message);
        client.execute();
    }
}
