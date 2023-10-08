package cn.holelin.dicom.pacs_v1.enums;

import org.dcm4che3.data.UID;

/**
 *
 */
public enum InformationModelEnum {

    ECHO(UID.Verification,""),
    /**
     * FIND操作
     */
    FIND(UID.StudyRootQueryRetrieveInformationModelFind, "SERIES"),
    /**
     * MOVE操作
     */
    MOVE(UID.StudyRootQueryRetrieveInformationModelMove, "SERIES");
    final String cuid;
    final String level;

    InformationModelEnum(String cuid, String level) {
        this.cuid = cuid;
        this.level = level;
    }
    public String getCuid() {
        return cuid;
    }

    public String getLevel() {
        return level;
    }
}
