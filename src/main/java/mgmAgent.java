import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;

import java.net.*;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class mgmAgent implements Runnable {

    private final String name;
    private final int ID;
    private final ArrayList<String> dcopDomain;
    private String currentValue;
    private String newValue;
    private float currentUtility;
    private float gain;
    private String status; // should be 3 status starting/gain/value_update
    private String optMode; // current default to be seeking maximum
    public final int cycle_limit;
    public int cycle_count;

    public ArrayList<String> recv;

    // neighbor related fields
    private final ArrayList<String> neighbors;
    private final HashMap<String, Integer> neighborsPorts;
    private HashMap<String, String> neighborsValues;
    private HashMap<String, String> neighborsNewValues;
    private HashMap<String, Float> neighborsGains;
    private HashMap<String, HashMap<String, HashMap<String, Integer>>> agentUtilityMap;

    // communication related fields
    private InetAddress ip;
    private final int PORT;


    public mgmAgent(String name,
                    int id,
                    ArrayList<String> domain,
                    String mode,
                    int cycle_limit,
                    ArrayList<String> neighbors,
                    int port,
                    HashMap<String, Integer> neighborsPorts) {
        this.name = name;
        this.ID = id;
        this.status = "value"; // gain/value
        this.optMode = mode; // can be max or min
        this.cycle_limit = cycle_limit;
        this.cycle_count = 0;

        //Assign the first element of the dcop domain to be the current assignment
        this.dcopDomain = domain;
        this.currentValue = this.dcopDomain.get(0);
        this.currentUtility = 0;
        this.neighbors = neighbors;

        this.neighborsValues = new HashMap<>();
        this.neighborsNewValues = new HashMap<>();

        this.neighborsGains = new HashMap<>();

        this.PORT = port;
        this.neighborsPorts = neighborsPorts;

    }

    @Override
    public void run() {
        // the main run process for each agent

        ClientHandler ch = new ClientHandler();

        // where agent spawn the thread to handle each income request
        Thread ch_thread = new Thread(ch);
        ch_thread.start();

        // wait a second for the thread to start before msgs are sent
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // triger the whole process with first message
        if (cycle_count == 0) {
            // delay the sending to make sure all thread has been initialized
            sendValueMessage();
        }

    }

    class MessageHandler implements Runnable {
        // Very simple calss for taking in the value and store it in the received

        final DataInputStream dis;
        final DataOutputStream dos;
        final Socket s;
        private volatile String recvd;


        // Constructor
        public MessageHandler(Socket s, DataInputStream dis, DataOutputStream dos) {
            this.s = s;
            this.dis = dis;
            this.dos = dos;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    // receive 1
                    this.recvd = dis.readUTF();
                    // System.out.println("    Msg received by " + name +"  "+ recvd);
                    this.s.close();
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                // closing resources
                this.dis.close();
                this.dos.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public String getRecvd() {
            return recvd;
        }

    }


    // ClientHandler class
    class ClientHandler implements Runnable {

        public void run() {
            ServerSocket ss;
            String received;
            recv = new ArrayList<>();

            System.out.println("Agent " + name + " starts running");

            try {
                // server is listening on port 5056
                ss = new ServerSocket(PORT);
                System.out.println("Agent " + name + " gets port " + PORT);

                // running infinite loop for getting
                // client request
                while (cycle_count < cycle_limit) {
                    Socket s = null;
                    HashMap<String, String> pMsg = new HashMap<>();

                    try {
                        // socket object to receive incoming client requests\


                        s = ss.accept();


                        // obtaining input and out streams
                        DataInputStream dis = new DataInputStream(s.getInputStream());
                        DataOutputStream dos = new DataOutputStream(s.getOutputStream());

                        // create a new thread object
                        MessageHandler ch = new MessageHandler(s, dis, dos);
                        Thread t = new Thread(ch);

                        // Invoking the start() method
                        t.start();

                        t.join();
                        pMsg = parseMsg(ch.getRecvd());

                        // System.out.println(status + pMsg);

                        if (pMsg.get("type").equals("value")) {
                            neighborsNewValues.put(pMsg.get("origin"), pMsg.get("value"));
                            if (cycle_count == 0) {
                                neighborsValues.put(pMsg.get("origin"), pMsg.get("value"));
                                float bestSoFar = evaluateExtensional(name, currentValue);
                                bestSoFar += evaluateRelations(currentValue);
                                currentUtility = bestSoFar;
                            }
                            if (status.equals("value")) {
                                handleValueMessage();
                            }
                        } else if (pMsg.get("type").equals("gain")) {
                            neighborsGains.put(pMsg.get("origin"), Float.parseFloat(pMsg.get("value")));
                            if (status.equals("gain")) {
                                handleGainMesssage();
                            }
                        }


                    } catch (Exception e) {
                        try {
                            s.close();
                            e.printStackTrace();
                        } catch (NullPointerException se) {
                            se.printStackTrace();
                        }
                    }
                }
                System.out.println("AGENT: " + name + " value: " + currentValue + " utility: " + currentUtility);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public int getID() {
        return this.ID;
    }

    public ArrayList<String> getDcopDomain() {
        return dcopDomain;
    }

    public ArrayList<String> getNeighbors() {
        return neighbors;
    }

    public float getCurrentUtility() {
        return this.currentUtility;
    }

    public String getCurrentValue() {
        return this.currentValue;
    }

    public void sendValueMessage() {
        // send the current value to all its beighbors

        for (String n : neighbors) {
            String toSend = name + "/" + n + "/value/" + this.currentValue;
            sendMsg(neighborsPorts.get(n), toSend);
        }
        // System.out.println("In cycle:"+ cycle_count + ", "+ name + " value " + currentValue+ " sent to "+ neighbors);


        // TODO:
        // pubsub: can publish the message to the channel of this agent

    }


    public void sendGainMessage() {
        // send the current gain to all agents in neighbor list

        for (String n : neighbors) {
            String toSend = name + "/" + n + "/gain/" + this.gain;
            sendMsg(neighborsPorts.get(n), toSend);
        }
        //System.out.println("In cycle:"+ cycle_count + ", "+ name + " gain:" +this.gain+" sent to "+ neighbors);

        // pubsub: can publish the message to the channel of this agent

    }

//    public void getGainMessage(){
//        for (int i = 0; i < neighbors.size(); i++) {
//            mgmMessage msg = fetchFromChannel(neighbors.get(i));
//            neighborsGains.replace(neighbors.get(i), msg.getGain());
//        }
//    }

    public void handleValueMessage() {
        // when all values updates have been received
        // given the new context received in this.neighborsNewValues
        // gain and newValue has been set by findOptAssignment()
        if (neighborsNewValues.size() == neighbors.size()) {
            this.status = "gain";
            this.neighborsValues = (HashMap<String, String>) neighborsNewValues.clone();
            //System.out.println("Status change: Agent " + name + " starts handling gain");
            findOptValue();
            sendGainMessage();
        }

    }

    public void handleGainMesssage() {
        // when all gain updates have been received
        // given the new context received in this.neighborsNewValues
        // gain and newValue has been set by findOptAssignment()

        // System.out.println("Agent: " + name + " " + neighborsGains);
        if (neighborsGains.size() == neighbors.size()) {

            HashMap<String, Float> allGains;
            allGains = (HashMap<String, Float>) neighborsGains.clone();
            allGains.put(this.name, this.gain);

            if (this.optMode.equals("max")) {

                String maxNeighbour = Collections.max(allGains.entrySet(), Map.Entry.comparingByValue()).getKey();
                if (maxNeighbour.equals(this.name)) {
                    // if the max gain is current agent
                    this.currentValue = this.newValue;
                    this.currentUtility += this.gain;
                    // sendValueMessage();
                }

            } else if (this.optMode.equals("min")) {

                String minNeighbour = Collections.min(allGains.entrySet(), Map.Entry.comparingByValue()).getKey();
                if (minNeighbour.equals(this.name)) {
                    this.currentValue = this.newValue;
                    this.currentUtility += this.gain;
                }

            } else {
                System.out.println("Optimization Mode is not supporteds");
            }

            System.out.println("In cycle: " + (cycle_count) + " agent: " + name + " neighbors: " + neighborsValues
                    + " current ultility: " + (this.currentUtility - this.gain) + " gain: " + this.gain);

            this.neighborsGains = new HashMap<>();
            // change status
            this.status = "value";
            //System.out.println("Status change: Agent " + name + " starts handling value");
            this.cycle_count += 1;
            sendValueMessage();


        }
    }

    private static float evaluateValue(String agent1, String val1, String agent2, String val2) {
        // compare the utility given that this agent choose val1 and agent2 choose val2

        // return agentUtilityMap.get(val1).get(agent2).get(val2);

        // currently hardcoded

        int val = 0;


        if ((agent1.equals("a1") && agent2.equals("a2") || agent1.equals("a2") && agent2.equals("a1"))
                && val1.equals(val2)) {
            val += 10;
        }
        if ((agent1.equals("a3") && agent2.equals("a2") || agent1.equals("a2") && agent2.equals("a3"))
                && val1.equals(val2)) {
            val += 10;
        }

        return val;

    }

    private float evaluateRelations(String value) {
        float val = 0;

        for (String n : neighbors) {
            val += evaluateValue(this.name, value, n, neighborsValues.get(n));
        }

        return val;

    }

    private static float evaluateExtensional(String name, String val1) {
        float val = 0;

        if (name.equals("a1")) {
            if (val1.equals("R")) {
                val -= 0.1;
            } else {
                val += 0.1;
            }
        } else if (name.equals("a2")) {
            if (val1.equals("R")) {
                val += 0.1;
            } else {
                val -= 0.1;
            }
        } else if (name.equals("a3")) {
            if (val1.equals("R")) {
                val += 0.1;
            } else {
                val -= 0.1;
            }
        }

        return val;
    }

    private void findOptValue() {
        // find the best value in dcop domain based on current context
        // returns the best assignment and store in this.gain
        float bestSoFar = this.currentUtility;

        for (String val : dcopDomain) {
            float newUtility = evaluateExtensional(this.name, val);
            newUtility += evaluateRelations(val);
            System.out.println("agent: " + name + " value:" + val + " u:" + newUtility);
            //System.out.println("New Utility is" + newUtility + "current" + this.currentUtility);
            if (newUtility > bestSoFar && this.optMode.equals("max") ||
                    newUtility < bestSoFar && this.optMode.equals("min")) {
                this.newValue = val;
                bestSoFar = newUtility;
            } else {
                this.newValue = this.currentValue;
            }
        }

        this.gain = bestSoFar - this.currentUtility;

        System.out.println("Agent " + name + " current ultility: " + (this.currentUtility) + " gain: " + this.gain + " neighbors " + neighborsValues);

    }

    private static HashMap<String, String> parseMsg(String msg) {
        // Msg : "orginAgentID-type-value"
        HashMap<String, String> reformatted_msg = new HashMap<>();
        String[] msg_parts = msg.split("/");
        reformatted_msg.put("origin", msg_parts[0]);
        reformatted_msg.put("dest", msg_parts[1]);
        reformatted_msg.put("type", msg_parts[2]);
        reformatted_msg.put("value", msg_parts[3]);
        return reformatted_msg;
    }

    public void sendMsg(int port, String msg) {
        try {
            // getting localhost ip
            InetAddress ip = InetAddress.getByName("localhost");

            // establish the connection with server port 5056
            Socket s = new Socket(ip, port);

            // obtaining input and out streams
            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());

            //send 1, the msg gonna be read
            dos.writeUTF(msg);

            //System.out.println("Msg \"" + msg +"\" sent from " + PORT + " to " + port);

            s.close();

            dis.close();
            dos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
