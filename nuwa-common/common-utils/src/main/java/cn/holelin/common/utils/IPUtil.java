package cn.holelin.common.utils;

import org.lionsoul.ip2region.xdb.Searcher;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author HoleLin
 */
public class IPUtil {
    public static void main(String[] args) throws IOException {
        // 1、创建 searcher 对象
        String dbPath = "C:\\Users\\YW\\IdeaProjects\\Nuwa\\nuwa-common\\common-utils\\src\\main\\resources\\ip\\ip2region.xdb";
        // 1、从 dbPath 加载整个 xdb 到内存。
        byte[] cBuff;
        try {
            cBuff = Searcher.loadContentFromFile(dbPath);
        } catch (Exception e) {
            System.out.printf("failed to load content from `%s`: %s\n", dbPath, e);
            return;
        }

        // 2、使用上述的 cBuff 创建一个完全基于内存的查询对象。
        Searcher searcher;
        try {
            searcher = Searcher.newWithBuffer(cBuff);
        } catch (Exception e) {
            System.out.printf("failed to create content cached searcher: %s\n", e);
            return;
        }

        // 3、查询
        String ip = null;
        try {
            ip = "222.64.78.214";
            long sTime = System.nanoTime();
            String region = searcher.search(ip);
            long cost = TimeUnit.NANOSECONDS.toMicros((long) (System.nanoTime() - sTime));
            System.out.printf("{region: %s, ioCount: %d, took: %d μs}\n", region, searcher.getIOCount(), cost);
        } catch (Exception e) {
            System.out.printf("failed to search(%s): %s\n", ip, e);
        }

        // 4、关闭资源 - 该 searcher 对象可以安全用于并发，等整个服务关闭的时候再关闭 searcher
        // searcher.close();

        // 3、关闭资源
        searcher.close();

    }
}
