package esir.progm.untitledsharkgames.Communication;

import android.os.AsyncTask;
import android.widget.TextView;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client extends AsyncTask<Void, Void, String> {
    private String dstAddress;
    private final int PORT = 8080;
    private String message;

    public Client(String addr, String message) {
        dstAddress = addr;
        this.message = message;
    }

    @Override
    protected String doInBackground(Void... arg0) {
        Socket socket = null;
        try {
            System.out.println("try to join : "+dstAddress+":"+PORT);
            socket = new Socket(dstAddress, PORT);
            // écriture du message
            OutputStream outputStream = socket.getOutputStream();
            PrintStream printStream = new PrintStream(outputStream);
            printStream.print(this.message);
            printStream.close();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
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