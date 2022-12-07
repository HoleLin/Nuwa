package cn.holelin.logfilter.starter.config;

import cn.holelin.logfilter.starter.EnableLogFilter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;

/**
 * @Description:
 * @Author: HoleLin
 * @CreateDate: 2022/10/14 10:32
 * @UpdateUser: HoleLin
 * @UpdateDate: 2022/10/14 10:32
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
public class EnableLogFilterImportSelector implements DeferredImportSelector, BeanClassLoaderAware, EnvironmentAware {

    private static final Logger logger = LoggerFactory.getLogger(EnableLogFilterImportSelector.class);
    private Class annotationClass = EnableLogFilter.class;
    private ClassLoader beanClassLoader;

    private Environment environment;
    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public String[] selectImports(AnnotationMetadata metadata) {
        if (!isEnabled()) {
            return new String[0];
        }
        final AnnotationAttributes attributes = AnnotationAttributes.fromMap(
                metadata.getAnnotationAttributes(this.annotationClass.getName(), true));
        Assert.notNull(attributes, "attributes can not be null...");
        // 从spring.factories中获取所有通过EnableLogFilter注解引入的自动配置类,并进行去重操作
        final ArrayList<String> factories = new ArrayList<>(
                new LinkedHashSet<>(SpringFactoriesLoader.loadFactoryNames(this.annotationClass, this.beanClassLoader)));
        if (factories.isEmpty()&&!hasDefaultFactory()){
            throw new IllegalStateException("Annotation @" + getSimpleName()
                    + " found, but there are no implementations. Did you forget to include a starter?");
        }
        if (factories.size() > 1) {

            logger.warn("More than one implementation " + "of @" + getSimpleName()
                    + " (now relying on @Conditionals to pick one): " + factories);
        }

        return factories.toArray(new String[factories.size()]);
    }

    private boolean isEnabled() {
        return true;
    }

    protected boolean hasDefaultFactory() {
        return false;
    }
    protected String getSimpleName() {
        return this.annotationClass.getSimpleName();
    }

    protected Class<EnableLogFilter> getAnnotationClass() {
        return this.annotationClass;
    }

    protected Environment getEnvironment() {
        return this.environment;
    }

}
