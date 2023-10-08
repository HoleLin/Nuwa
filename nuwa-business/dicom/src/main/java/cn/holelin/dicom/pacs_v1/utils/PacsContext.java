package cn.holelin.dicom.pacs_v1.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayDeque;


/**
 * @author HoleLin
 */
@Slf4j
public class PacsContext {

    private PacsContext() {
    }

    /**
     * 执行C-MOVE动作队列
     */
    private static final ArrayDeque<String> C_MOVE_QUEUE = new ArrayDeque<>(50);
    public static boolean offer(String key) {
        return C_MOVE_QUEUE.offer(key);
    }

    public static String peek() {
        return C_MOVE_QUEUE.peek();
    }

    public static void remove() {
        C_MOVE_QUEUE.remove();
    }

}
