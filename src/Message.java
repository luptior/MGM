public class Message {
    private String sender;
    private String receiver;
    private int gain;

    public Message(String sender, String receiver, int gain){
        this.sender = sender;
        this.receiver = receiver;
        this.gain = gain;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public int getGain() {
        return gain;
    }
}
