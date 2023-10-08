package cn.holelin.dicom.pacs_v1.utils;


import cn.holelin.dicom.pacs_v1.entity.PullTaskRecord;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author HoleLin
 */
public class GlobalPullTaskContext {

    private static final LinkedBlockingQueue<PullTaskRecord> QUEUE = new LinkedBlockingQueue<>();


    public static boolean offer(PullTaskRecord task) {
        return QUEUE.offer(task);
    }

    public static PullTaskRecord poll() {
        return QUEUE.poll();
    }

}
