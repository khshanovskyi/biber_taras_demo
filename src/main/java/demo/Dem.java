package demo;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Dem {

    public static void main(String[] args) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("storeId", 33L);
        map.put("userName", "Kolya");
        map.put("order", 1);
        map.put("ww", null);
        map.put("arr", new Object[]{});

        System.out.println(extractAndConvertToString(map, "userName"));
        System.out.println(extractAndConvertToLong(map, "storeId"));
        System.out.println(extractAndConvertToInt(map, "order"));
        System.out.println(extractAndConvertToInt(map, "userName"));
        System.out.println(extractAndConvertToLong(map, "ww"));
        System.out.println(extractAndConvertToLong(map, "arr"));

    }

    public static String extractAndConvertToString(Map<String, Object> stringObjectMap, String paramName){
        return extractIfPresent(stringObjectMap, paramName).orElse(null);
    }

    public static Long extractAndConvertToLong(Map<String, Object> stringObjectMap, String paramName) {
        return extractIfPresent(stringObjectMap, paramName)
                .filter(NumberUtils::isParsable)
                .map(NumberUtils::createLong)
                .orElse(null);
    }

    public static Integer extractAndConvertToInt(Map<String, Object> stringObjectMap, String paramName) {
        return extractIfPresent(stringObjectMap, paramName)
                .filter(NumberUtils::isParsable)
                .map(NumberUtils::createInteger)
                .orElse(null);
    }

    private static Optional<String> extractIfPresent(Map<String, Object> stringObjectMap, String paramName) {
        return Optional.ofNullable(stringObjectMap)
                .filter(entry -> entry.containsKey(paramName))
                .map(entry -> entry.get(paramName))
                .map(ToStringBuilder::reflectionToString);
    }
}
