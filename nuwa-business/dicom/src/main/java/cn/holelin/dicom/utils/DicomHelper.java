package cn.holelin.dicom.utils;

import cn.holelin.dicom.domain.DicomImage;
import cn.holelin.dicom.domain.DicomImagePretreatment;
import cn.holelin.dicom.domain.DicomSeries;
import cn.holelin.dicom.domain.DicomStudy;
import cn.holelin.dicom.entity.DicomTag;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.crypto.digest.DigestUtil;
import org.dcm4che3.data.Attributes;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * @Description: Dicom辅助类用于序列拆分, 最优序列筛选
 * @Author: HoleLin
 * @CreateDate: 2022/5/19 3:44 PM
 * @UpdateUser: HoleLin
 * @UpdateDate: 2022/5/19 3:44 PM
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
public class DicomHelper {

    private static final Integer MIN_FRAME_NUMBER = 50;
    private static final String SET = "set";
    private static final String SHARED_PATH = "/Users/holelin/Projects/MySelf/Java-Notes/dicom/src/main/resources/dicom/";

    /**
     * 将
     *
     * @param dicomImagePretreatments
     */
    public static void bestSeriesFiltrate(List<DicomImagePretreatment> dicomImagePretreatments, List<DicomTag> dicomTags) throws IOException {
        if (CollUtil.isNotEmpty(dicomTags) && CollUtil.isNotEmpty(dicomImagePretreatments)) {
            List<DicomImage> dicomImages = new ArrayList<>(dicomImagePretreatments.size());
            dicomImagePretreatments.forEach(dicomImagePretreatment -> {
                final Attributes attributes = dicomImagePretreatment.getAttributes();
                final DicomImage dicomImage = buildDicomImage(dicomTags, attributes);
                assert dicomImage != null;
                dicomImage.setSourceFileName(dicomImagePretreatment.getSourceFileName());
                dicomImage.setSourceFile(dicomImagePretreatment.getFile());
                dicomImages.add(dicomImage);
            });

            // 提取Study信息
            if (CollUtil.isNotEmpty(dicomImages)) {
                // 根据序列属性分组序列
                final Map<String, List<DicomImage>> study =
                        dicomImages.stream().collect(Collectors.groupingBy(DicomImage::getSeriesInstanceUID));
                DicomStudy dicomStudy = new DicomStudy();
                final DicomImage simpleDicomImage = dicomImages.get(0);
                dicomStudy.setStudyID(simpleDicomImage.getStudyID());
                dicomStudy.setStudyInstanceUID(simpleDicomImage.getStudyInstanceUID());
                dicomStudy.setSeries(study);
                splitFileDir(dicomStudy);

                // 重新组装文件 将文件按序列进行拆分归类
                List<DicomSeries> multipleSeries = new ArrayList<>();
                study.entrySet()
                        .stream().filter(entry -> MIN_FRAME_NUMBER <= entry.getValue().size())
                        .forEach(entry -> {
                            final DicomSeries dicomSeries = parseSeries(entry.getValue());
                            multipleSeries.add(dicomSeries);
                        });

                if (CollUtil.isNotEmpty(multipleSeries)) {
                    // 收集多序列用于筛选最优序列
                    final DicomSeries dicomSeries = doBestSeriesFiltrate(multipleSeries);
                    if (Objects.isNull(dicomSeries)) {
                        // 未选出最优序列
                    } else {
                        // 送检
                    }
                }
            }

        }
    }

    /**
     * 从文件存储的角度拆分多序列
     *
     * @param dicomStudy 待重新规划文件路径的多序列
     */
    private static void splitFileDir(DicomStudy dicomStudy) throws IOException {
        final String studyId = dicomStudy.getStudyID();
        final String studyInstanceUid = dicomStudy.getStudyInstanceUID();
        final String studyDirName = DigestUtil.sha1Hex(studyInstanceUid + studyId);
        final Path studyDir = Paths.get(SHARED_PATH + studyDirName);
        if (!studyDir.toFile().exists()) {
            Files.createDirectory(studyDir);
        }
        final Map<String, List<DicomImage>> series = dicomStudy.getSeries();
        series.forEach((seriesInstanceUid, dicomImages) -> {
            String seriesPath = SHARED_PATH + studyDirName + File.separator + seriesInstanceUid + File.separator;
            try {
                final Path seriesDir = Paths.get(seriesPath);
                if (!seriesDir.toFile().exists()) {
                    Files.createDirectory(seriesDir);
                }
                dicomImages.forEach(dicomImage -> {
                    final File sourceFile = dicomImage.getSourceFile();
                    try {
                        final String newFileName = dicomImage.getSourceFileName();
                        final Path oldPath = Paths.get(seriesPath + newFileName);
                        if (!oldPath.toFile().exists()) {
                            final Path newFilePath = Files.createFile(oldPath);
                            // TODO 此处可能上传OSS
                            Files.copy(sourceFile.toPath(), newFilePath, StandardCopyOption.REPLACE_EXISTING);
                            // 复制后将新的文件重新赋值给dicomImage
                            final File afterHandleFile = newFilePath.toFile();
                            dicomImage.setAfterHandleFile(afterHandleFile);
                            dicomImage.setAfterHandleFileName(afterHandleFile.getName());
                        } else {
                            final File oldFile = oldPath.toFile();
                            dicomImage.setAfterHandleFile(oldFile);
                            dicomImage.setAfterHandleFileName(oldFile.getName());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
    }

    /**
     * 通过反射来构建DicomImage对象并对其进行属性赋值
     *
     * @param dicomTags  需要赋值的tag信息
     * @param attributes dicom影像文件属性
     * @return
     */
    private static DicomImage buildDicomImage(List<DicomTag> dicomTags, Attributes attributes) {
        final Class<DicomImage> dicomImageClass = DicomImage.class;
        try {
            final Constructor<DicomImage> defaultConstructor = dicomImageClass.getConstructor();
            final DicomImage image = defaultConstructor.newInstance();
            dicomTags.forEach(it -> {
                try {
                    final int tag = Math.toIntExact(it.getTagValue());
                    switch (it.getVr()) {
                        case "DS":
                            final Method doubleMethod = dicomImageClass.getMethod(SET + it.getTagName(), Double.class);
                            doubleMethod.invoke(image, attributes.getDouble(tag, -1));
                            break;
                        case "DA":
                            final Method localDateMethod = dicomImageClass.getMethod(SET + it.getTagName(), LocalDate.class);
                            final Date date = attributes.getDate(tag);
                            if (Objects.nonNull(date)) {
                                localDateMethod.invoke(image, date.toInstant()
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDate());
                            }
                            break;
                        case "IS":
                            final Method integerMethod = dicomImageClass.getMethod(SET + it.getTagName(), Integer.class);
                            integerMethod.invoke(image, attributes.getInt(tag, -1));
                            break;
                        default:
                            final Method stringMethod = dicomImageClass.getMethod(SET + it.getTagName(), String.class);
                            stringMethod.invoke(image, attributes.getString(tag));
                            break;
                    }
                } catch (IllegalAccessException |
                        NoSuchMethodException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            });
            return image;
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException |
                InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 最优序列筛选
     *
     * @param multipleSeries
     * @return
     */
    private static DicomSeries doBestSeriesFiltrate(List<DicomSeries> multipleSeries) {
        multipleSeries.stream()
                .filter(DicomSeries::getIsWhole)
                .filter(DicomSeries::getIsSerial)
                .forEach(series -> {
                    weighting(series);
                });
        // 若可以根据权重进行排序,则选出权重最大的序列,若选不出来则返回null
        return null;
    }

    /**
     * 序列加权操作
     *
     * @param series 待处理的序列
     */
    private static void weighting(DicomSeries series) {
        // TODO 待补充
    }

    /**
     * 提取序列中总帧数/是否完整/是否连续等信息,用于最优序列筛选
     *
     * @param series 待处理的序列
     * @return 序列对象
     */
    private static DicomSeries parseSeries(List<DicomImage> series) {
        DicomSeries dicomSeries = new DicomSeries();
        if (CollUtil.isNotEmpty(series)) {
            dicomSeries.setNumberOfInstances(series.size());
            List<Integer> instanceNumberValues = new ArrayList<>(series.size());
            final DicomImage image = series.get(0);
            BeanUtil.copyProperties(image, dicomSeries);
            series.forEach(dicomImage -> {
                final Integer instanceNumber = dicomImage.getInstanceNumber();
                if (instanceNumber > dicomSeries.getMaxInstanceNumber()) {
                    dicomSeries.setMaxInstanceNumber(instanceNumber);
                }
                if (instanceNumber < (dicomSeries).getMinInstanceNumber()) {
                    dicomSeries.setMinInstanceNumber(instanceNumber);
                }
                dicomSeries.setLastReceivedTime(LocalDateTime.now());
                instanceNumberValues.add(instanceNumber);
            });
            // 判断序列是否完整
            boolean isWhole = dicomSeries.getMaxInstanceNumber() - dicomSeries.getMinInstanceNumber() + 1
                    == dicomSeries.getNumberOfInstances();
            dicomSeries.setIsWhole(isWhole);
            // 判断序列是否连续
            Collections.sort(instanceNumberValues);
            for (int i = 0; i < instanceNumberValues.size() - 1; i++) {
                int j = i + 1;
                if (instanceNumberValues.get(j) - instanceNumberValues.get(i) != 1) {
                    dicomSeries.setIsSerial(false);
                    break;
                }
            }
        }
        return dicomSeries;
    }

    public static void main(String[] args) {
        final Class<DicomImage> dicomImageClass = DicomImage.class;
        try {
            final Constructor<DicomImage> defaultConstructor = dicomImageClass.getConstructor();
            final DicomImage image = defaultConstructor.newInstance();
            final Field declaredField = dicomImageClass.getDeclaredField("seriesInstanceUID");
            declaredField.setAccessible(true);
            declaredField.set(image, "123");
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException |
                InvocationTargetException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
