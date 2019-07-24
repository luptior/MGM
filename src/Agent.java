import java.util.LinkedList;
import java.util.Queue;

public class Agent {

    final private String id;
    private String[] neighbors;
    private int currentValue;
    private Queue<Message> sendningQueue;

    public Agent(String id, String[] neighborsList){
        this.id=id;
        this.neighbors=neighborsList;
        sendningQueue = new LinkedList<>();
    }

    public String getId() {
        return id;
    }

    public String[] getNeighbors() {
        return neighbors;
    }

    public int getCurrentValue() {
        return currentValue;
    }

    public void sendAllValueMessage(String[] neighbors, int value){
        for (int i=0; i<neighbors.length; i++){
            Message msg = new Message(this.id, neighbors[i], value);
            sendningQueue.add(msg);
        }

        for (int i=0; i<neighbors.length; i++){

        }
    }



}
