package br.com.devs.monkey;

import com.google.gson.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        String json1 = "{}";
        String json2 = "{}";
        Object differences = findDifferences(json1, json2);
        System.out.println(differences);
    }
    public static String findDifferences(String json1, String json2) {
        Gson gson = new Gson();
        JsonElement element1 = JsonParser.parseString(json1);
        JsonElement element2 = JsonParser.parseString(json2);

        Map<String, Object> differences = new HashMap<>();
        compareJsonElements(element1, element2, differences, gson, "");

        Gson gsonPretty = new GsonBuilder().setPrettyPrinting().create();
        return gsonPretty.toJson(differences);
    }

    private static void compareJsonElements(JsonElement element1, JsonElement element2, Map<String, Object> differences, Gson gson, String basePath) {
        JsonObject obj1 = element1.getAsJsonObject();
        JsonObject obj2 = element2.getAsJsonObject();

        for (Map.Entry<String, JsonElement> entry : obj1.entrySet()) {
            String key = entry.getKey();
            JsonElement val1 = entry.getValue();
            JsonElement val2 = obj2.has(key) ? obj2.get(key) : null;

            String path = basePath.isEmpty() ? key : basePath + "." + key;

            if (val2 == null) {
                differences.put(path, Map.of("from", val1, "to", "undefined"));
            } else if (val1.isJsonObject() && val2.isJsonObject()) {
                compareJsonElements(val1, val2, differences, gson, path);
            } else if (val1.isJsonArray() && val2.isJsonArray()) {
                compareJsonArrays(val1.getAsJsonArray(), val2.getAsJsonArray(), differences, gson, path);
            } else if (!val1.equals(val2)) {
                differences.put(path, Map.of("from", val1, "to", val2));
            }
        }
    }

    private static void compareJsonArrays(JsonArray arr1, JsonArray arr2, Map<String, Object> differences, Gson gson, String basePath) {
        // Implementação simplificada para a comparação baseada em codBanco, idBanco, isSelect ou select.
        for (int i = 0; i < arr1.size(); i++) {
            JsonObject obj1 = arr1.get(i).getAsJsonObject();
            JsonElement idBanco1 = obj1.has("idBanco") ? obj1.get("idBanco") : JsonParser.parseString("null");
            JsonElement codBanco1 = obj1.has("codBanco") ? obj1.get("codBanco") : JsonParser.parseString("null");
            JsonElement isSelect1 = obj1.has("isSelect") ? obj1.get("isSelect") : JsonParser.parseString("null");
            JsonElement select1 = obj1.has("select") ? obj1.get("select") : JsonParser.parseString("null");

            boolean matched = false;
            for (int j = 0; !matched && j < arr2.size(); j++) {
                JsonObject obj2 = arr2.get(j).getAsJsonObject();
                if (equals(idBanco1, obj2.get("idBanco")) || equals(codBanco1, obj2.get("codBanco")) ||
                        (isSelect1.toString().equals("true") && equals(select1, obj2.get("select")))) {
                    compareJsonElements(obj1, obj2, differences, gson, basePath + "[" + i + "]");
                    matched = true;
                }
            }
            if (!matched) {
                differences.put(basePath + "[" + i + "]", Map.of("from", obj1, "to", "undefined"));
            }
        }
    }

    private static boolean equals(JsonElement elem1, JsonElement elem2) {
        return elem1 != null && elem2 != null && elem1.equals(elem2);
    }

}