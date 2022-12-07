package cn.holelin.dicom.pacs.enums;

import org.dcm4che3.data.UID;

/**
 * Presentation Contexts中的Abstract Syntax
 * @Description:
 * @Author: HoleLin
 * @CreateDate: 2022/8/15 15:00
 * @UpdateUser: HoleLin
 * @UpdateDate: 2022/8/15 15:00
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
public enum InformationModelEnum {
    /**
     * FIND操作
     */
    FIND_STUDY_ROOT(UID.StudyRootQueryRetrieveInformationModelFind, "STUDY"),
    /**
     * MOVE操作
     */
    MOVE_PATIENT_ROOT(UID.PatientRootQueryRetrieveInformationModelMove, "STUDY"),
    MOVE_STUDY_ROOT(UID.StudyRootQueryRetrieveInformationModelMove, "STUDY"),
    /**
     * ECHO
     */
    VERIFICATION(UID.Verification, "STUDY");

    public final String cuid;
    public final String level;


    InformationModelEnum(String cuid, String level) {
        this.cuid = cuid;
        this.level = level;
    }
}
