package cn.holelin.dicom.pacs_v1.utils;

import java.util.HashMap;
import java.util.Map;

public class ThreadLocalUtils {
    private static final ThreadLocal<Map<String, Object>> MAP = new ThreadLocal<>();

    public static void set(String key, Object value) {
        Map<String, Object> map = MAP.get();
        if (map == null) {
            map = new HashMap<>();
            MAP.set(map);
        }
        map.put(key, value);
    }

    public static Object get(String key) {
        Map<String, Object> map = MAP.get();
        return map == null ? null : map.get(key);
    }

    public static void remove() {
        MAP.remove();
    }
}
