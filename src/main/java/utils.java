import java.util.HashMap;

public class utils {

    public static HashMap<String, String> dictDecopose(String dict) {

        String[] msg_parts = dict.split(",");

        HashMap<String, String> re_dict = new HashMap<>();

        String key;
        String value;


        for (String m : msg_parts) {
//            string.replaceAll("^\"|\"$", "");
            key = m.split(":")[0]
                    .replace(" ", "")
                    .replace("{", "")
                    .replace("}", "")
                    .replace("'", "");
            value = m.split(":")[1]
                    .replace(" ", "")
                    .replace("{", "")
                    .replace("}", "")
                    .replace("'", "");
            re_dict.put(key, value);
        }

        return re_dict;
    }

}
