import org.yaml.snakeyaml.Yaml

/**
 * Class describing a sample DCOP problem.
 *
 *
 * agents, a list of agents names in String
 * domains, <K></K>,V> for agent ids and List of domain
 * edges, 2D matrix, if adjacent 1, otherwise 0
 * utility, [val1, val2, val3] = 10.1
 *
 *
 * TODO:
 * 1, Combinatorics
 * 2, Reads Sceanario Files
 */
class DCOPProblem {
    //    private static final Logger logger = LoggerFactory.getLogger(DCOPProblem.class);
    var agents: java.util.ArrayList<String>
    var domains: HashMap<String, java.util.ArrayList<String>> = HashMap<String, java.util.ArrayList<String>>()
    var edges: Array<IntArray>
    var utility: HashMap<java.util.ArrayList<*>, Double> = HashMap<java.util.ArrayList<*>, Double>()
    private fun solutionSpace(): java.util.ArrayList<List<String>> {
        // Return cartesian product of domains

        // TODO: do the actual work here
        // might need some library which can do the combinatorial in Java
        return java.util.ArrayList<List<String>>()
    }

    /**
     * Utility Part
     */
    override fun toString(): String {
        var problemString = ""
        problemString += agents.toString() + "\n"
        problemString += domains.toString() + "\n"
        for (edge in edges) {
            problemString += Arrays.toString(edge) + "\n"
        }
        problemString += utility.toString()
        return problemString
    }

    companion object {
        fun readSample(sampleFile: String?) {
            // TODO: actually parse sample from a file
        }
    }

    init {
        // default to have a sample question
        agents = java.util.ArrayList<String>(Arrays.asList<String>("a1", "a2", "a3"))
        val domain: java.util.ArrayList<String> = java.util.ArrayList<String>()
        domain.add("R")
        domain.add("G")
        for (a in agents) {
            domains.put(a, domain)
        }
        edges = Array(agents.size) { IntArray(agents.size) }
        for (i in agents.indices) {
            for (j in agents.indices) {
                if (i != j) {
                    edges[i][j] = 1
                } else {
                    edges[i][j] = 0
                }
                if (i == 0 && j == 2 || i == 2 && j == 0) {
                    edges[i][j] = 0
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
        var entry: java.util.ArrayList<String?>
        entry = java.util.ArrayList<String>(Arrays.asList<String>("R", "R", "R"))
        utility.put(entry, 20.1)
        entry = java.util.ArrayList<String>(Arrays.asList<String>("R", "R", "G"))
        utility.put(entry, 9.9)
        entry = java.util.ArrayList<String>(Arrays.asList<String>("R", "G", "G"))
        utility.put(entry, 9.7)
        entry = java.util.ArrayList<String>(Arrays.asList<String>("G", "G", "G"))
        utility.put(entry, 19.9)
        entry = java.util.ArrayList<String>(Arrays.asList<String>("G", "G", "R"))
        utility.put(entry, 10.1)
        entry = java.util.ArrayList<String>(Arrays.asList<String>("G", "R", "R"))
        utility.put(entry, 10.3)
        entry = java.util.ArrayList<String>(Arrays.asList<String>("R", "G", "R"))
        utility.put(entry, -0.1)
        entry = java.util.ArrayList<String>(Arrays.asList<String>("G", "R", "G"))
        utility.put(entry, 0.1)

//        logger.info("A dcop problem formed");
//        logger.info(toString());
    }
}