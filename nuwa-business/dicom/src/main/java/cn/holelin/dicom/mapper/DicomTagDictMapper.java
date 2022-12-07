package cn.holelin.dicom.mapper;

import cn.holelin.dicom.entity.DicomTagDict;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
/**
 * @Description:
 * @Author: HoleLin
 * @CreateDate: 2022/5/19 11:29 PM
 * @UpdateUser: HoleLin
 * @UpdateDate: 2022/5/19 11:29 PM
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
@Mapper
public interface DicomTagDictMapper {
    /**
     * 通过Dicom Tag Id来查询tag对应值的字典项
     * @param tagIds tagId列表
     * @return 字典项列表
     */
    List<DicomTagDict> queryDictByTagIds(List<Integer> tagIds);
}
