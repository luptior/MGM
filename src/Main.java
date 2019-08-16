import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws InterruptedException{

        dcopProblem p = new dcopProblem();

        HashMap<String, Integer> agentsPorts = new HashMap<>();
        for (String a : p.agents) {
            agentsPorts.put(a, 5000+p.agents.indexOf(a));
        }


        // build agents
        HashMap<String ,mgmAgent> agentDict = new HashMap<>();


        for (String a : p.agents) {
            ArrayList<String> neighbors = new ArrayList<>();
            int index = p.agents.indexOf(a);
            for (int i=0; i < p.edges.length; i++){
                if(p.edges[index][i] == 1){
                    neighbors.add(p.agents.get(i));
                }
            }

            mgmAgent new_agent = new mgmAgent(a,
                                            p.agents.indexOf(a),
                                            p.domains.get(a),
                                            "max",
                                            10,
                                            neighbors,
                                            5000+p.agents.indexOf(a),
                                            agentsPorts);
            agentDict.put(a, new_agent);
        }

        // start agents
        for(String a :p.agents){
            Thread t = new Thread(agentDict.get(a));
            t.start();
        }

//


    }

}
