package cn.holelin.common.file.zip;

import java.util.HashSet;

/**
 * @Description:
 * @Author: HoleLin
 * @CreateDate: 2022/3/30 2:34 PM
 * @UpdateUser: HoleLin
 * @UpdateDate: 2022/3/30 2:34 PM
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
public abstract class AbstractCompressStrategy implements CompressStrategy {
    final static HashSet<String> EXCLUDE_SET = new HashSet<>(16);
    final static String EMPTY_STRING = "";

    static {
        EXCLUDE_SET.add(".DS_Store");
        EXCLUDE_SET.add("__MACOSX");
    }

}
