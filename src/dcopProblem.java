import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import org.yaml.snakeyaml.Yaml;

/**
 * Class describing a sample DCOP problem.
 *
 * agents, a list of agents names in String
 * domains, <K,V> for agent ids and List of domain
 * edges, 2D matrix, if adjacent 1, otherwise 0
 * utility, [val1, val2, val3] = 10.1
 *
 * TODO:
 *  1, Combinatorics
 *  2, Reads Sceanario Files
 */

public class dcopProblem {

//    private static final Logger logger = LoggerFactory.getLogger(dcopProblem.class);

    public ArrayList<String> agents ;
    public HashMap<String, ArrayList<String>> domains = new HashMap<>();
    public int[][] edges;
    public HashMap<ArrayList, Double> utility = new HashMap<>();
    

    public dcopProblem(){
        // default to have a sample question
        agents = new ArrayList<String>(Arrays.asList("a1", "a2", "a3"));
        ArrayList<String> domain = new ArrayList<>();
        domain.add("R");
        domain.add("G");

        for (String a : agents){
            domains.put(a, domain);
        }

        edges = new int[agents.size()][agents.size()];
        for (int i = 0; i<agents.size(); i++){
            for (int j = 0; j<agents.size(); j++){
                if (i != j){
                    edges[i][j] = 1;
                }else{
                    edges[i][j] = 0;
                }

                if( i == 0 && j ==2 || i == 2 && j ==0) {
                    edges[i][j] = 0;
                }
            }
        }

        /**
         * A simple sample rule of utility(min the best)
         * if v1 = v2, utility += 10 otherwise 0
         * if v3 = v2, utility += 10 otherwise 0
         * if v1 = G, utility += 0.1 otherwise -= 0.1
         * if v2 = R, utility += 0.1 otherwise -= 0.1
         * if v3 = R, utility += 0.1 otherwise -= 0.1
         */
        ArrayList<String> entry;
        entry = new ArrayList<>(Arrays.asList("R","R","R"));
        utility.put(entry, 20.1);
        entry = new ArrayList<>(Arrays.asList("R","R","G"));
        utility.put(entry, 9.9);
        entry = new ArrayList<>(Arrays.asList("R","G","G"));
        utility.put(entry, 9.7);
        entry = new ArrayList<>(Arrays.asList("G","G","G"));
        utility.put(entry, 19.9);
        entry = new ArrayList<>(Arrays.asList("G","G","R"));
        utility.put(entry, 10.1);
        entry = new ArrayList<>(Arrays.asList("G","R","R"));
        utility.put(entry, 10.3);
        entry = new ArrayList<>(Arrays.asList("R","G","R"));
        utility.put(entry, -0.1);
        entry = new ArrayList<>(Arrays.asList("G","R","G"));
        utility.put(entry, 0.1);

//        logger.info("A dcop problem formed");
//        logger.info(toString());
    }

    private ArrayList<List<String>> solutionSpace(){
        // Return cartesian product of domains
        ArrayList<List<String>> solutions = new ArrayList<List<String>>();

        // TODO: do the actual work here
        // might need some library which can do the combinatorial in Java

        return solutions;
    }

    public static void readSample(String sampleFile) {
        // TODO: actually parse sample from a file
    }


    /**
     * Utility Part
     */

    public String toString(){
        String problemString = "";
        problemString += agents.toString()+"\n";
        problemString += domains.toString()+"\n";
        for (int[] edge : edges){
            problemString += Arrays.toString(edge)+"\n";
        }
        problemString += utility.toString();

        return problemString;
    }


}
