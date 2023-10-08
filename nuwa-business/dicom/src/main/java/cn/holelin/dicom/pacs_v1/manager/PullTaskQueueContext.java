package cn.holelin.dicom.pacs_v1.manager;


import cn.holelin.dicom.pacs_v1.entity.PullTaskRecord;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author HoleLin
 */
public class PullTaskQueueContext {


    private static final ConcurrentHashMap<String, ArrayBlockingQueue<PullTaskRecord>> PULL_TASK_MAP = new ConcurrentHashMap<>();

    /**
     * 根据用户ID获取对应的拉图队列
     *
     * @param userId 用户ID
     * @return 用户ID获取对应的拉图队列
     */
    public static ArrayBlockingQueue<PullTaskRecord> getUserPullTaskQueue(String userId) {
        return PULL_TASK_MAP.get(userId);
    }


    public static void put(String userId, ArrayBlockingQueue<PullTaskRecord> queue) {
        PULL_TASK_MAP.put(userId, queue);
    }

}
