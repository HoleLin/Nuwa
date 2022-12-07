package cn.holelin.dicom.utils;

import cn.holelin.dicom.domain.DicomImagePretreatment;
import cn.hutool.core.lang.UUID;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @Description:
 * @Author: HoleLin
 * @CreateDate: 2022/5/16 1:28 PM
 * @UpdateUser: HoleLin
 * @UpdateDate: 2022/5/16 1:28 PM
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
public class DownloadUtil {

    public static void main(String[] args) throws IOException {
//        deleteFile();
        handleFile();
    }

    private static void handleFile() throws FileNotFoundException {
        final ArrayList<String> urls = new ArrayList<>();
//        initUrls(urls);
        File dir = org.springframework.util.ResourceUtils.getFile("classpath:dicom");
        String orderId = UUID.fastUUID().toString();
        DicomCache dicomCache = new DicomCache();
        List<DicomImagePretreatment> dicomImages = new ArrayList<DicomImagePretreatment>(urls.size());
        dicomCache.setDicomImages(dicomImages);
        dicomCache.setBusinessId(orderId);
        OkHttpClient okHttpClient = new OkHttpClient();
        urls.forEach(url -> {
                    Observable.create((ObservableOnSubscribe<byte[]>) emitter -> {
                                final Request request = new Request.Builder().url(url)
                                        .get().build();
                                final Call call = okHttpClient.newCall(request);
                                call.enqueue(new Callback() {
                                    @Override
                                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                        emitter.onError(e);
                                    }

                                    @Override
                                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                        emitter.onNext(Objects.requireNonNull(response.body()).bytes());
                                        emitter.onComplete();
                                    }
                                });
                            })
                            .observeOn(Schedulers.io())
                            .map(body -> {
                                final File tempFile = File.createTempFile("RF_", ".temp", dir);
                                final FileOutputStream outputStream = new FileOutputStream(tempFile);
                                outputStream.write(body);
                                outputStream.close();
                                return tempFile;
                            }).subscribe(dicomFile -> {
                                DicomImagePretreatment dicomImagePretreatment = new DicomImagePretreatment();
                                dicomImagePretreatment.setFile(dicomFile);
                                dicomImagePretreatment.setMetaData(DicomParse.parseMetaData(dicomFile));
                                dicomImagePretreatment.setAttributes(DicomParse.parseAttributes(dicomFile));
                                dicomImagePretreatment.setDataSet(DicomParse.parseDataSet(dicomFile));
                                dicomImagePretreatment.setSourceFileName(dicomFile.getName());
                                dicomImages.add(dicomImagePretreatment);
                            });
                }
        );


    }

    private static void deleteFile() throws IOException {
        File dir = org.springframework.util.ResourceUtils.getFile("classpath:dicom");
        if (dir.exists()) {
            final File[] files = dir.listFiles();
            assert files != null;
            if (files.length != 0) {
                Arrays.stream(files).forEach(file -> {
                    try {
                        Files.deleteIfExists(file.toPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }

}
