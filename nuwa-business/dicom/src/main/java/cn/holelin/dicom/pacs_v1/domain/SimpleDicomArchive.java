package cn.holelin.dicom.pacs_v1.domain;

import lombok.Data;

import java.util.List;

/**
 * @author HoleLin
 */
@Data
public class SimpleDicomArchive {
    List<String> SOPInstanceUID;
}
