package esir.progm.untitledsharkgames.Communication;
import android.content.Context;

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
    Context context;
    ServerSocket serverSocket;
    static final int socketServerPORT = 8080;

    public Server(Multijoueur multi) {
        Thread socketServerThread = new Thread(new SocketServerThread(multi));
        socketServerThread.start();
    }

    public int getPort() {
        return socketServerPORT;
    }
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
        private Multijoueur multi;
        private MultiHeberger mh;
        private MultiClient mc;
        private boolean init;

        public SocketServerThread(Multijoueur multi) {
            this.multi = multi;
            this.init = false;
        }

        @Override
        public void run() {
            try {
                // create ServerSocket using specified port
                serverSocket = new ServerSocket(socketServerPORT);
                while (true) {
                    // block the call until connection is created and return
                    // Socket object
                    Socket socket = serverSocket.accept();
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    InputStream inputStream = socket.getInputStream();
                    StringBuilder message = new StringBuilder();
                    /*
                     * notice: inputStream.read() will block if no data return
                     */
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        byteArrayOutputStream.write(buffer, 0, bytesRead);
                        message.append(byteArrayOutputStream.toString("UTF-8"));
                    }
                    if (message.charAt(0)=='/') {
                        message.append("/" + socket.getInetAddress().getHostAddress());
                    }
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

    public String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface.getNetworkInterfaces();
            int nbOfInterface = 0;
            while (enumNetworkInterfaces.hasMoreElements()) {
                nbOfInterface++;
                NetworkInterface networkInterface = enumNetworkInterfaces.nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface.getInetAddresses();
                int nbIP = 0;
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();
                    if (inetAddress.isSiteLocalAddress()) {
                        ip += inetAddress.getHostAddress();
                        nbIP++;
                    }
                }
                System.out.println("interface : "+nbOfInterface+" => "+nbIP+" @ présentes");
            }
        } catch (SocketException e) {
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }
        return ip;
    }
}