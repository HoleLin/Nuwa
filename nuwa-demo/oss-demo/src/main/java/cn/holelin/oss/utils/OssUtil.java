package cn.holelin.oss.utils;

import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONUtil;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description:
 * @Author: HoleLin
 * @CreateDate: 2022/7/1 16:22
 * @UpdateUser: HoleLin
 * @UpdateDate: 2022/7/1 16:22
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
@Slf4j
public class OssUtil {

    private ReentrantLock reentrantLock = new ReentrantLock();
    private MinioClient client;

    public OssUtil(MinioClient client) {
        this.client = client;
    }

    public void download(String bucketName, String objectName, HttpServletResponse response) {
        if (reentrantLock.tryLock()) {
            try {
                final StopWatch stopWatch = new StopWatch();
                stopWatch.start();

                final GetObjectResponse object = client.getObject(GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build());

                response.addHeader("Content-Disposition", "attachment;filename=" + objectName);
                response.setContentType("application/octet-stream");
                final ServletOutputStream outputStream = response.getOutputStream();
                final byte[] buf = new byte[1024];
                while (object.read(buf, 0, buf.length) >= 0) {
                    outputStream.write(buf);
                }
                outputStream.flush();
                outputStream.close();
                stopWatch.stop();
                System.out.println("共耗时:" + stopWatch.getLastTaskTimeMillis() + "ms");
            } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                     InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                     XmlParserException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public void upload(String bucketName, String objectName, String filePath) {
        upload(bucketName, objectName, filePath, false);
    }

    /**
     * @param bucketName     上传文件最终存储到那个bucket的名称
     * @param objectName     文件完整名称,例/doc/pdf/xxx.pdf
     * @param filePath       文件路径
     * @param isCreateBucket 若bucket不存在是否创建bucket
     */
    public void upload(String bucketName, String objectName, String filePath, boolean isCreateBucket) {
        // 先根据bucketName判断bucket是否存在
        final BucketExistsArgs bucketExistsArgs = BucketExistsArgs.builder().bucket(bucketName).build();
        try {
            boolean bucketExists = client.bucketExists(bucketExistsArgs);
            if (!bucketExists) {
                Assert.isTrue(isCreateBucket, "上传失败,OSS bucket不存在");
                client.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            final ObjectWriteResponse response = client.uploadObject(UploadObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .filename(filePath)
                    .build());
            final String responseStr = JSONUtil.parseObj(response).toStringPretty();
            stopWatch.stop();
            log.info("上传OSS总耗时:{}ms,返回值:{}", stopWatch.getLastTaskTimeMillis(), responseStr);
        } catch (ErrorResponseException | InsufficientDataException | InternalException |
                 InvalidKeyException | InvalidResponseException | IOException |
                 NoSuchAlgorithmException | ServerException | XmlParserException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取一天有效期的文件外链
     *
     * @param bucketName bucket名称
     * @param objectName 文件完整名称,例/doc/pdf/xxx.pdf
     * @return 外链
     */
    public String getLinkWithOneDay(String bucketName, String objectName) {
        String url = "";
        final BucketExistsArgs bucketExistsArgs = BucketExistsArgs.builder().bucket(bucketName).build();

        try {
            if (client.bucketExists(bucketExistsArgs)) {
                url = client.getPresignedObjectUrl(
                        GetPresignedObjectUrlArgs.builder()
                                .bucket(bucketName)
                                .object(objectName)
                                .expiry(1, TimeUnit.DAYS)
                                .method(Method.GET)
                                .build());
            }
        } catch (ErrorResponseException | InsufficientDataException | InternalException |
                 InvalidKeyException | InvalidResponseException | IOException |
                 NoSuchAlgorithmException | ServerException | XmlParserException e) {
            e.printStackTrace();
        }
        return url;
    }


    /**
     * 列出指定bucket的指定前缀下所有Objects
     * @param bucketName
     * @param dir
     * @return
     */
    public List<String> list(String bucketName, String dir) {
        ArrayList<String> result = new ArrayList<>();
        final BucketExistsArgs bucketExistsArgs = BucketExistsArgs.builder().bucket(bucketName).build();

        try {
            if (client.bucketExists(bucketExistsArgs)) {
                Iterable<Result<Item>> results = client.listObjects(ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .prefix(dir)
                        .build());
                for (Result<Item> itemResult : results) {
                    Item item = itemResult.get();
                    result.add(item.objectName());
                }
            }
        } catch (ErrorResponseException | InsufficientDataException | InternalException |
                 InvalidKeyException | InvalidResponseException | IOException |
                 NoSuchAlgorithmException | ServerException | XmlParserException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 列出指定bucket的指定前缀下所有Objects,并生成外链
     * @param bucketName
     * @param dir
     * @return
     */
    public List<String> listWithShare(String bucketName, String dir) {
        ArrayList<String> result = new ArrayList<>();
        final BucketExistsArgs bucketExistsArgs = BucketExistsArgs.builder().bucket(bucketName).build();

        try {
            if (client.bucketExists(bucketExistsArgs)) {
                Iterable<Result<Item>> results = client.listObjects(ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .prefix(dir)
                        .build());
                for (Result<Item> itemResult : results) {
                    Item item = itemResult.get();
                    String url = client.getPresignedObjectUrl(
                            GetPresignedObjectUrlArgs.builder()
                                    .bucket(bucketName)
                                    .object(item.objectName())
                                    .expiry(1, TimeUnit.DAYS)
                                    .method(Method.GET)
                                    .build());
                    result.add(url);
                }
            }
        } catch (ErrorResponseException | InsufficientDataException | InternalException |
                 InvalidKeyException | InvalidResponseException | IOException |
                 NoSuchAlgorithmException | ServerException | XmlParserException e) {
            e.printStackTrace();
        }
        return result;
    }
}
