package jna

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import com.google.gson.GsonBuilder

class systemProperties extends SystemProp {
    public static SystemProp systemProp;
    private static HashMap<?, ?> properties = new LinkedHashMap<>()
    private static Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create()
    private static String json;
    static {
        for (Map.Entry<?, ?> e : System.getProperties().entrySet()) {
            properties[e.getKey()] = e.getValue()
            //System.out.println(String.format("%s = %s", e.getKey().toString().replace(".", "_"), e.getValue()));
        }

        json = gson.toJson(properties)

        ObjectMapper objectMapper = new ObjectMapper();
        systemProp = objectMapper.readValue(json, SystemProp.class) as SystemProp;
        //System.out.println("prox = " + systemProp.getJavaAwtGraphicsenv());

        //gson.fromJson(this.toString(), SystemProp.class);
        //println(json)
    }

    @Override
    String toString() {
        Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create()
        return gson.toJson(properties)
    }

    static void main(String[] args) {
        //def j = new systemProperties()
        //println(j)
        print(systemProp)
    }
}