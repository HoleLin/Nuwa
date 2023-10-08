package cn.holelin.dicom.pacs_v1;

import cn.holelin.dicom.pacs_v1.config.DefaultPacsServerProperties;
import cn.holelin.dicom.pacs_v1.config.PacsProperties;
import cn.holelin.dicom.pacs_v1.domain.PacsServerConfig;
import cn.holelin.dicom.pacs_v1.manager.StoreDicomWithLockRunnable;
import cn.holelin.dicom.pacs_v1.support.PacsServerSupport;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.concurrent.ExecutorService;

import static cn.holelin.dicom.pacs_v1.consts.StringConstants.DEFAULT_PACS_SERVER_DEVICE_NAME;


/**
 * @author HoleLin
 */
@Component
@Order(value = 2)
public class PacsRunner implements ApplicationRunner {

    private final PacsProperties pacsProperties;

    private final ExecutorService pullTaskExecutorService;

    public PacsRunner(PacsProperties pacsProperties, ExecutorService pullTaskExecutorService) {
        this.pacsProperties = pacsProperties;
        this.pullTaskExecutorService = pullTaskExecutorService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        PacsServerSupport support = new PacsServerSupport(getDefaultPacsConfig());
        try {
            support.start();
            pullTaskExecutorService.execute(new StoreDicomWithLockRunnable(pacsProperties));
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }

    }

    private PacsServerConfig getDefaultPacsConfig() {
        PacsServerConfig config = new PacsServerConfig();
        config.setDeviceName(DEFAULT_PACS_SERVER_DEVICE_NAME);
        DefaultPacsServerProperties defaultConfig = pacsProperties.getDefaultConfig();
        config.setLocalHostName(defaultConfig.getHostname());
        config.setAeTitle(defaultConfig.getAet());
        config.setLocalPort(defaultConfig.getPort());
        config.setLocalStoragePath(defaultConfig.getLocalStoragePath());
        return config;
    }
}
