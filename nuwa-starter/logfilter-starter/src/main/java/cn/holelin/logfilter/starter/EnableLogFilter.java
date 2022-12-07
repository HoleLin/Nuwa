package cn.holelin.logfilter.starter;

import cn.holelin.logfilter.starter.config.EnableLogFilterImportSelector;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

/**
 * @Description:
 * @Author: HoleLin
 * @CreateDate: 2022/10/13 18:39
 * @UpdateUser: HoleLin
 * @UpdateDate: 2022/10/13 18:39
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import(EnableLogFilterImportSelector.class)
public @interface EnableLogFilter {

}
