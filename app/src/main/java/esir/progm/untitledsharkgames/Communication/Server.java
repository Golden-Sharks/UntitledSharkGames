package esir.progm.untitledsharkgames.Communication;
/**
 * Code source : https://androidsrc.net/android-client-server-using-sockets-server-implementation/?utm_content=cmp-true
 * Je l'ai adapté au projet afin qu'il corresponde mieux au besoin
 */


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

import esir.progm.untitledsharkgames.multiplayer.Multijoueur;
import esir.progm.untitledsharkgames.multiplayer.MultiClient;
import esir.progm.untitledsharkgames.multiplayer.MultiHeberger;

public class Server {
    private ServerSocket serverSocket;                  // Socket utilisé par la classe
    private static final int socketServerPORT = 8080;   // Port de communication par défaut

    public Server(Multijoueur multi) {
        Thread socketServerThread = new Thread(new SocketServerThread(multi));
        socketServerThread.start();
    }

    /**
     * Détruit proprement le serveur
     */
    public void onDestroy() {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private class SocketServerThread extends Thread {
        // Classe d'origine du serveur
        private Multijoueur multi;

        public SocketServerThread(Multijoueur multi) {
            this.multi = multi;
        }

        @Override
        public void run() {
            try {
                // Créé le serveur en utilisant le port par défaut
                serverSocket = new ServerSocket(socketServerPORT);
                // Boucle infinie pour capter les messages
                while (true) {
                    Socket socket = serverSocket.accept(); // Accepte les communications
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    /*      Lecture des messages envoyés sur le socket      */
                    InputStream inputStream = socket.getInputStream();
                    StringBuilder message = new StringBuilder();
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        byteArrayOutputStream.write(buffer, 0, bytesRead);
                        message.append(byteArrayOutputStream.toString("UTF-8"));
                    }
                    /*      Si le message commence par un "/" : étoffe ce dernier avec l'adresse IP     */
                    if (message.length()>0 && message.charAt(0)=='/') {
                        message.append("/" + socket.getInetAddress().getHostAddress());
                    }
                    // Envoit du message à la classe du serveur
                    String finalMessage = message.toString();
                    multi.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            multi.setMsg(finalMessage);
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("AAAAA : server is stopped (it shouldn't)");
            }
        }
    }

    /**
     * Retourne l'adresse IP du socket
     */
    public String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces.nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface.getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();
                    if (inetAddress.isSiteLocalAddress()) {
                        ip += inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return ip;
    }
}