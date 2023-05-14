package esir.progm.untitledsharkgames.Communication;
/**
 * Code source : https://androidsrc.net/android-client-server-using-sockets-client-implementation/
 */

import android.os.AsyncTask;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client extends AsyncTask<Void, Void, String> {
    private String dstAddress;      // Adresse à laquelle envoyer le message
    private final int PORT = 8080;  // Port par défaut : 8080
    private String message;         // Message à transmettre

    public Client(String addr, String message) {
        dstAddress = addr;
        this.message = message;
    }

    @Override
    protected String doInBackground(Void... arg0) {
        Socket socket = null;
        try {
            socket = new Socket(dstAddress, PORT);
            // écriture du message
            OutputStream outputStream = socket.getOutputStream();
            PrintStream printStream = new PrintStream(outputStream);
            printStream.print(this.message);
            printStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }
}