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

import esir.progm.untitledsharkgames.interfaces.Game;
import esir.progm.untitledsharkgames.multiplayer.MultiClient;
import esir.progm.untitledsharkgames.multiplayer.MultiHeberger;

public class Server {
    Context context;
    ServerSocket serverSocket;
    static final int socketServerPORT = 8080;

    public Server(Game dispMsg) {
        Thread socketServerThread = new Thread(new SocketServerThread(dispMsg));
        socketServerThread.start();
    }

    public Server(MultiHeberger mh) {
        Thread socketServerThread = new Thread(new SocketServerThread(mh));
        socketServerThread.start();
    }

    public Server(MultiClient mc) {
        Thread socketServerThread = new Thread(new SocketServerThread(mc));
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
        private Game dispMsg;
        private MultiHeberger mh;
        private MultiClient mc;
        private boolean init;

        public SocketServerThread(Game dispMsg) {
            this.dispMsg = dispMsg;
            this.init = false;
        }

        public SocketServerThread(MultiHeberger mh) {
            this.mh = mh;
            this.init = true;
        }

        public SocketServerThread(MultiClient mc) {
            this.mc = mc;
            this.init = true;
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
                    String message = "";
                    /*
                     * notice: inputStream.read() will block if no data return
                     */
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        byteArrayOutputStream.write(buffer, 0, bytesRead);
                        message += byteArrayOutputStream.toString("UTF-8");
                    }

                    if (init) { // On veut récupérer le pseudo et l'IP
                        if (mh!=null) {

                            String finalMessage = message;
                            this.mh.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    String ip = parseIP(socket.getInetAddress().toString());
                                    mh.addDevice(finalMessage, ip);
                                }
                            });
                        }
                        else {
                            this.mc.runOnUiThread(new Runnable(){
                                @Override
                                public void run() {
                                   mc.launchGames();
                                }
                            });
                        }
                    } else {
                        String msg = "Score de l'adversaire : "+message;
                        this.dispMsg.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dispMsg.setMsg(msg);
                            }
                        });
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("AAAAA : server is stopped (it shouldn't)");
            }
        }
    }

    private String parseIP(String ip) {
        StringBuilder cleanIP = new StringBuilder();
        int nbInNumberBlock = 0;
        int nbOfPoints = 0;
        for (int i=0 ; i<ip.length() ; i++){
            char c = ip.charAt(i);
            if (c=='.') {
                nbInNumberBlock = 0;
                nbOfPoints++;
                if (nbOfPoints>3) break;
                cleanIP.append(c);
            } else if(c<='9' && c>='0') {
                nbInNumberBlock++;
                if (nbInNumberBlock>3) break;
                cleanIP.append(c);
            }
        }
        return cleanIP.toString();
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