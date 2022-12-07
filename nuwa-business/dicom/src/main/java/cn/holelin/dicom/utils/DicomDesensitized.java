package cn.holelin.dicom.utils;

import cn.holelin.dicom.domain.DicomImagePretreatment;
import cn.holelin.dicom.entity.DicomTag;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.text.CharSequenceUtil;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.VR;
import org.dcm4che3.io.DicomOutputStream;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;

/**
 * @Description: dicom文件敏感字段脱敏工具类
 * @Author: HoleLin
 * @CreateDate: 2022/5/7 1:23 PM
 * @UpdateUser: HoleLin
 * @UpdateDate: 2022/5/7 1:23 PM
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
public class DicomDesensitized {
    private static final String FILE_EXTENSION_TEMP = ".temp";
    private static final String DICOM_TAG_DEFAULT_CHARSET = "ISO_IR 192";
    private static final Integer DICOM_TAG_MIN_VALUE = 3;


    private DicomDesensitized() {

    }

    /**
     * 默认脱敏方法 采用
     *
     * @param dicomFrame dicom对象
     * @param dicomTags 需要脱敏的Tag列表
     */
    public static void defaultDesensitization(DicomImagePretreatment dicomFrame,
                                              List<DicomTag> dicomTags) throws IOException {
        // 若dicomFrame对象为空或者没有需要脱敏的Tag则直接跳过
        if (Objects.isNull(dicomFrame) || CollectionUtil.isEmpty(dicomTags)) {
            return;
        }
        final File dicomFile = dicomFrame.getFile();
        final String originFileName = dicomFile.getCanonicalPath();
        String tempFilename = originFileName + FILE_EXTENSION_TEMP;
        final File tempFile = new File(tempFilename);
        final Attributes dataSet;
        final Attributes metaData;

        // 若没有获取DataSet时,获取DataSet
        if (Objects.isNull(dicomFrame.getDataSet())) {
            dataSet = DicomParse.parseDataSet(dicomFile);
        } else {
            dataSet = dicomFrame.getDataSet();
        }
        // 若没有获取MetaData时,获取MetaData
        if (Objects.isNull(dicomFrame.getMetaData())) {
            metaData = DicomParse.parseMetaData(dicomFile);
        } else {
            metaData = dicomFrame.getMetaData();
        }
        // 脱敏
        dicomTags.forEach(it -> {
            final Long tagValue = it.getTagValue();
            final String originValue = dataSet.getString(Math.toIntExact(tagValue));
            if (Objects.nonNull(originValue)) {
                String targetValue = "";
                // 只有tag的值大于等于3采用中间隐藏的脱敏方式,否则采用全部隐藏
                if (originValue.length() >= DICOM_TAG_MIN_VALUE) {
                    targetValue = CharSequenceUtil.hide(originValue, 1, originValue.length() - 1);
                } else {
                    targetValue = CharSequenceUtil.hide(originValue, 0, originValue.length());
                }
                dataSet.setString(Math.toIntExact(tagValue), VR.AE, targetValue);
            }
        });
        try (final DicomOutputStream dicomOutputStream = new DicomOutputStream(tempFile)) {
            // 确保所有影像标签字符集为 UTF-8
            dataSet.setSpecificCharacterSet(DICOM_TAG_DEFAULT_CHARSET);
            dicomOutputStream.writeDataset(metaData, dataSet);
            // 将脱敏后的文件覆盖原有文件
            final File newFile = new File(originFileName);
            if (Files.deleteIfExists(dicomFile.toPath()) && tempFile.renameTo(newFile)) {
                dicomFrame.clean();
                dicomFrame.setAttributes(DicomParse.parseAttributes(newFile));
            }
        } catch (IOException e) {
            Files.deleteIfExists(tempFile.toPath());
        }
    }

}
