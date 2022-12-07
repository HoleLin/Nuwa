package cn.holelin.dicom.utils.validator;

import cn.holelin.dicom.domain.DicomImagePretreatment;
import cn.hutool.core.collection.CollUtil;
import org.dcm4che3.data.Attributes;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @Description:
 * @Author: HoleLin
 * @CreateDate: 2022/5/7 8:00 PM
 * @UpdateUser: HoleLin
 * @UpdateDate: 2022/5/7 8:00 PM
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */

public class DbValidator implements Validator {
    /**
     * 需要校验的Tag以及字典项列表
     */
    Map<Long, Set<String>> map;

    public DbValidator(Map<Long, Set<String>> map) {
        this.map = map;
    }

    @Override
    public Boolean validated(DicomImagePretreatment dicomFrame) {
        final Attributes attributes = dicomFrame.getAttributes();
        if (Objects.isNull(attributes)) {
            return false;
        }
        if (CollUtil.isNotEmpty(map)) {
            // 是否通过合法性校验
            boolean passFlag = true;
            for (Map.Entry<Long, Set<String>> entry : map.entrySet()) {
                final Long tagValue = entry.getKey();
                final Set<String> dictValueSet = entry.getValue();
                if (!dictValueSet.contains(attributes.getString(Math.toIntExact(tagValue)))) {
                    passFlag = false;
                    break;
                }
            }
            return passFlag;
        } else {
            // 没有需要校验的Tag,直接返回true
            return true;
        }
    }
}
