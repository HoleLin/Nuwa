package cn.holelin.oss;

import cn.holelin.oss.utils.OssUtil;
import io.minio.MinioClient;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.springframework.util.StopWatch;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Fork(value = 2,jvmArgs = {"-Xms8G","-Xmx8G"})
public class OssBenchmark {

    @Benchmark
    public void upload() {
        String path = "C:\\Data\\DICOM\\test.zip";
        OssUtil ossUtil = new OssUtil(buildMinioClient());

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        ossUtil.upload("test", UUID.randomUUID().toString(), path, true);
        stopWatch.stop();
    }

    private MinioClient buildMinioClient() {
        return MinioClient.builder()
                .endpoint("http://localhost:9002")
                .credentials("minioadmin", "minioadmin")
                .build();
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder().include(OssBenchmark.class.getSimpleName())
                .result("oss-result.json")
                .resultFormat(ResultFormatType.JSON)
                .build();
        new Runner(opt).run();
    }

}
