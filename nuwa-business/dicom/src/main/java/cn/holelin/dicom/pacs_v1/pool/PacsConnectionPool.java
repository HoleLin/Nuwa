package cn.holelin.dicom.pacs_v1.pool;

import cn.hutool.core.util.ObjectUtil;
import org.apache.commons.pool2.KeyedPooledObjectFactory;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;
import org.dcm4che3.net.Association;

import java.time.Duration;

/**
 * PACS连接池
 *
 * @author HoleLin
 */
public class PacsConnectionPool extends GenericKeyedObjectPool<String, Association> {
    private static final Object LOCK = new Object();
    private static PacsConnectionPool pool = null;

    private PacsConnectionPool(KeyedPooledObjectFactory factory, GenericKeyedObjectPoolConfig config) {
        super(factory, config);
    }


    public static PacsConnectionPool getInstance() {
        return getInstance(false);
    }

    public static PacsConnectionPool getInstance(Boolean needExtendedNegotiation) {
        if (ObjectUtil.isNotNull(pool)) {
            return pool;
        }

        // need double-check
        synchronized (LOCK) {
            if (ObjectUtil.isNotNull(pool)) {
                return pool;
            }
            PacsConnectionPoolFactory factory = new PacsConnectionPoolFactory(needExtendedNegotiation);
            pool = new PacsConnectionPool(factory, buildConfig());
        }
        return pool;
    }

    private static GenericKeyedObjectPoolConfig<Association> buildConfig() {
        GenericKeyedObjectPoolConfig<Association> poolConfig = new GenericKeyedObjectPoolConfig<>();
        poolConfig.setMinIdlePerKey(2);
        poolConfig.setMaxIdlePerKey(3);
        poolConfig.setMaxTotalPerKey(10);
        poolConfig.setMaxWait(Duration.ofMinutes(2));
        poolConfig.setTestWhileIdle(true);
        poolConfig.setTimeBetweenEvictionRuns(Duration.ofMinutes(10));
        return poolConfig;
    }

}
