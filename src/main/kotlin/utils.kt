object utils {
    fun dictDecopose(dict: String): HashMap<String, String> {
        val msg_parts: Array<String> = dict.split(",").toTypedArray()
        val re_dict: HashMap<String, String> = HashMap<String, String>()
        var key: String
        var value: String
        for (m in msg_parts) {
//            string.replaceAll("^\"|\"$", "");
            key = m.split(":").toTypedArray().get(0)
                .replace(" ", "")
                .replace("{", "")
                .replace("}", "")
                .replace("'", "")
            value = m.split(":").toTypedArray().get(1)
                .replace(" ", "")
                .replace("{", "")
                .replace("}", "")
                .replace("'", "")
            re_dict.put(key, value)
        }
        return re_dict
    }
}