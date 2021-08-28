object Main {
    @kotlin.Throws(InterruptedException::class)
    @kotlin.jvm.JvmStatic
    fun main(args: Array<String>) {
        val p = DCOPProblem()
        val agentsPorts: HashMap<String, Int> = HashMap<String, Int>()
        for (a in p.agents) {
            agentsPorts.put(a, 5000 + p.agents.indexOf(a))
        }


        // build agents
        val agentDict: HashMap<String, mgmAgent> = HashMap<String, mgmAgent>()
        for (a in p.agents) {
            val neighbors: java.util.ArrayList<String> = java.util.ArrayList<String>()
            val index: Int = p.agents.indexOf(a)
            for (i in 0 until p.edges.length) {
                if (p.edges.get(index).get(i) === 1) {
                    neighbors.add(p.agents.get(i))
                }
            }
            val new_agent = mgmAgent(
                a,
                p.agents.indexOf(a),
                p.domains.get(a),
                "max",
                10,
                neighbors,
                5000 + p.agents.indexOf(a),
                agentsPorts
            )
            agentDict.put(a, new_agent)
        }

        // start agents
        for (a in p.agents) {
            val t: java.lang.Thread = java.lang.Thread(agentDict.get(a))
            t.start()
        }

//
    }
}