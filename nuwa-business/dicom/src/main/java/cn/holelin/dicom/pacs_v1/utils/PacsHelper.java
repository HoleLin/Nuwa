package cn.holelin.dicom.pacs_v1.utils;


import cn.holelin.dicom.pacs_v1.enums.InformationModelEnum;
import cn.holelin.dicom.pacs_v1.request.PacsDimseConditionBase;
import cn.holelin.dicom.pacs_v1.request.PacsSearchRequest;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.VR;
import org.dcm4che3.tool.common.CLIUtils;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * PACS 辅助类
 *
 * @author HoleLin
 */
public class PacsHelper {


    /**
     * 根据请求构建PACS查询条件
     *
     * @param request 请求
     * @return PACS查询条件
     */
    public static Attributes buildFindAttributes(PacsSearchRequest request) {
        Attributes attributes = buildBaseConditionAttributes(request);
        final String startDate = request.getStartDate();
        final String endDate = request.getEndDate();

        if (Objects.nonNull(startDate) && Objects.nonNull(endDate)) {
            final String days = startDate + "-" + endDate;
            CLIUtils.addAttributes(attributes, CLIUtils.toTags(new String[]{"StudyDate"}), days);
        } else {
            attributes.setDate(Tag.StudyDate, VR.DT, null);
        }
        final String startTime = request.getStartTime();
        final String endTime = request.getEndTime();
        if (Objects.nonNull(startTime) && Objects.nonNull(endTime)) {
            final String times = startTime + "-" + endTime;
            CLIUtils.addAttributes(attributes, CLIUtils.toTags(new String[]{"StudyTime"}), times);
        } else {
            attributes.setDate(Tag.StudyTime, VR.DT, null);
        }

        attributes.setInt(Tag.NumberOfSeriesRelatedInstances, VR.IS, null);
        attributes.setString(Tag.StudyDescription, VR.CS, "");
        attributes.setString(Tag.SeriesDescription, VR.CS, "");
        attributes.setString(Tag.PatientAge, VR.AS, "");
        attributes.setString(Tag.PatientSex, VR.CS, "");
        attributes.setDouble(Tag.SliceThickness, VR.DS, null);
        return attributes;
    }

    /**
     * 构建基础的查询条件
     * 设置QueryRetrieveLevel为 SERIES
     * 设置StudyInstanceUID/SeriesInstanceUID/PatientID/PatientName/AccessionNumber/BodyPartExamined/Modality
     *
     * @param dimseConditionBase 查询条件
     * @return DICOM TAG  Attributes
     */
    public static Attributes buildBaseConditionAttributes(PacsDimseConditionBase dimseConditionBase) {
        final Attributes attributes = new Attributes();

        String modality = dimseConditionBase.getModality();
        String bodyPartExamined = dimseConditionBase.getBodyPartExamined();
        attributes.setString(Tag.QueryRetrieveLevel, VR.CS,
                InformationModelEnum.FIND.getLevel());
        final String studyInstanceUid = dimseConditionBase.getStudyInstanceUid();
        if (StringUtils.hasText(studyInstanceUid)) {
            attributes.setString(Tag.StudyInstanceUID, VR.UI, studyInstanceUid);
        } else {
            attributes.setString(Tag.StudyInstanceUID, VR.UI, "");
        }
        final String seriesInstanceUid = dimseConditionBase.getSeriesInstanceUid();
        if (StringUtils.hasText(seriesInstanceUid)) {
            attributes.setString(Tag.SeriesInstanceUID, VR.UI, seriesInstanceUid);
        } else {
            attributes.setString(Tag.SeriesInstanceUID, VR.UI, "");
        }

        final String patientId = dimseConditionBase.getPatientId();
        if (StringUtils.hasText(patientId)) {
            attributes.setString(Tag.PatientID, VR.LO, patientId);
        } else {
            attributes.setString(Tag.PatientID, VR.LO, "");
        }
        final String patientName = dimseConditionBase.getPatientName();
        if (StringUtils.hasText(patientName)) {
            attributes.setString(Tag.PatientName, VR.PN, patientName);
        } else {
            attributes.setString(Tag.PatientName, VR.PN, "");
        }
        final String accessionNumber = dimseConditionBase.getAccessionNumber();
        if (StringUtils.hasText(accessionNumber)) {
            attributes.setString(Tag.AccessionNumber, VR.SH, accessionNumber);
        } else {
            attributes.setString(Tag.AccessionNumber, VR.SH, "");
        }

        if (StringUtils.hasText(bodyPartExamined)) {
            attributes.setString(Tag.BodyPartExamined, VR.CS, accessionNumber);
        } else {
            attributes.setString(Tag.BodyPartExamined, VR.CS, "");
        }
        if (StringUtils.hasText(modality)) {
            attributes.setString(Tag.Modality, VR.CS, modality);
        } else {
            attributes.setString(Tag.Modality, VR.CS, "");
        }
        return attributes;
    }
}
