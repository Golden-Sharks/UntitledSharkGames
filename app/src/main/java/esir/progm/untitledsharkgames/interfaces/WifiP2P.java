package esir.progm.untitledsharkgames.interfaces;

public interface WifiP2P {
    public void connect();

    public void createGroup();

    public void disconnect();

    public void sendString(String message);
}
