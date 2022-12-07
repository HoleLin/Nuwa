package cn.holelin.dicom.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Description:
 * @Author: HoleLin
 * @CreateDate: 2022/5/7 8:02 PM
 * @UpdateUser: HoleLin
 * @UpdateDate: 2022/5/7 8:02 PM
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
@Data
public class DicomTagDict {

    private Integer id;
    private Integer tagId;
    private String dictValue;
    private LocalDateTime createdTime;
}
