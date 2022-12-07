package cn.holelin.dicom.mapper;


import cn.holelin.dicom.entity.DicomTag;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Description:
 * @Author: HoleLin
 * @CreateDate: 2022/3/23 4:49 PM
 * @UpdateUser: HoleLin
 * @UpdateDate: 2022/3/23 4:49 PM
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
@Mapper
public interface DicomTagMapper {
    /**
     * 查询必须存在的Tag列表
     * @return
     */
    List<DicomTag> queryMust();

    /**
     * 查询需要进行脱敏的Tag列表
     * @return
     */
    List<DicomTag> queryNeedDesensitized();

    /**
     * 查询需要检查Tag值的合法性的Tag列表
     * @return
     */
    List<DicomTag> queryNeedCheck();

    /**
     *
     * @return
     */
    List<DicomTag> queryNeedStore();
}
