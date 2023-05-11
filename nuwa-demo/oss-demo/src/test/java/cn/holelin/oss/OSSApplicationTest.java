package cn.holelin.oss;

import cn.holelin.oss.utils.OssUtil;
import io.minio.MinioClient;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;

import java.nio.file.Paths;
import java.util.List;

/**
 * Unit test for simple App.
 */
@Slf4j
public class OSSApplicationTest
        extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public OSSApplicationTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(OSSApplicationTest.class);
    }

    /**
     * Rigourous Test :-)
     */
    public void testUpload() {
        String path = "C:\\Data\\DICOM\\test.zip";
        OssUtil ossUtil = new OssUtil(buildMinioClient());

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        ossUtil.upload("test", "test", path, true);
        stopWatch.stop();
        log.info("耗时:{}ms", stopWatch.getLastTaskTimeMillis());
    }

    public void testGetLink() {
        OssUtil ossUtil = new OssUtil(buildMinioClient());
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String test = ossUtil.getLinkWithOneDay("test", "06784f00-1c44-4c81-927b-822908ce02d9");
        stopWatch.stop();
        log.info("耗时:{}ms", stopWatch.getLastTaskTimeMillis());
        log.info("link:{}", test);
    }

    public void testListObjects() {
        OssUtil ossUtil = new OssUtil(buildMinioClient());
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        List<String> list = ossUtil.list("test", "dir/1_3_12_2_1107_5_1_4_73757");
        stopWatch.stop();
        log.info("耗时:{}ms", stopWatch.getLastTaskTimeMillis());
        log.info("link:{}", list);

    }

    public void testListWithShare() {
        OssUtil ossUtil = new OssUtil(buildMinioClient());
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        List<String> list = ossUtil.listWithShare("test", "dir/1_3_12_2_1107_5_1_4_73757");
        stopWatch.stop();
        log.info("耗时:{}ms", stopWatch.getLastTaskTimeMillis());
        log.info("link:{},{}", list,list.size());

    }


    private MinioClient buildMinioClient() {
        return MinioClient.builder()
                .endpoint("http://192.168.11.60:9002")
                .credentials("minioadmin", "minioadmin")
                .build();
    }
}
