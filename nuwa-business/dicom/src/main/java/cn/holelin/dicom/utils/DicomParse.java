package cn.holelin.dicom.utils;

import cn.holelin.dicom.entity.DicomTag;
import cn.hutool.core.collection.CollUtil;
import lombok.extern.slf4j.Slf4j;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.io.DicomInputStream;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @Description: Dicom Tag 解析类
 * @Author: HoleLin
 * @CreateDate: 2022/3/23 3:33 PM
 * @UpdateUser: HoleLin
 * @UpdateDate: 2022/3/23 3:33 PM
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
@Slf4j
public class DicomParse {
    public static final int DICOM_TAG_NO_LENGTH = -1;
    public static final int DICOM_TAG_NO_STOP = -1;

    private DicomParse() {
    }

    /**
     * 获取dicom文件的元数据
     *
     * @param dicomFile dicom文件
     * @return 元数据
     * @throws java.io.IOException
     */
    public static Attributes parseMetaData(File dicomFile) throws IOException {
        try (final DicomInputStream dicomInputStream = new DicomInputStream(dicomFile)) {
            return dicomInputStream.readFileMetaInformation();
        }
    }

    /**
     * 获取dicom文件的属性集
     *
     * @param dicomFile dicom文件
     * @return 属性集
     * @throws java.io.IOException
     */
    public static Attributes parseAttributes(File dicomFile) throws IOException {
        try (final DicomInputStream dicomInputStream = new DicomInputStream(dicomFile)) {
            dicomInputStream.setIncludeBulkData(DicomInputStream.IncludeBulkData.NO);
            return dicomInputStream.readDataset(DICOM_TAG_NO_LENGTH, it -> it.tag() == Tag.PixelData);
        }
    }

    /**
     * 获取dicom文件的数据集
     *
     * @param dicomFile dicom文件
     * @return 数据集
     * @throws java.io.IOException
     */
    public static Attributes parseDataSet(File dicomFile) throws IOException {
        try (final DicomInputStream dicomInputStream = new DicomInputStream(dicomFile)) {
            dicomInputStream.setIncludeBulkData(DicomInputStream.IncludeBulkData.URI);
            return dicomInputStream.readDataset(DICOM_TAG_NO_LENGTH, it -> it.tag() == DICOM_TAG_NO_STOP);
        }
    }

    public static void printTagInfo(Attributes attributes, List<DicomTag> dicomTags) {
        if (CollUtil.isNotEmpty(dicomTags)) {
            dicomTags.forEach(it -> log.info("{} = {}", it.getTagName(), attributes.getString(Math.toIntExact(it.getTagValue()))));
        }
    }
}
