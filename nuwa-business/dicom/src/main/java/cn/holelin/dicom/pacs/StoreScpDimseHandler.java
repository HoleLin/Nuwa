package cn.holelin.dicom.pacs;

import lombok.extern.slf4j.Slf4j;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.VR;
import org.dcm4che3.io.DicomOutputStream;
import org.dcm4che3.net.Association;
import org.dcm4che3.net.Dimse;
import org.dcm4che3.net.PDVInputStream;
import org.dcm4che3.net.Status;
import org.dcm4che3.net.pdu.PresentationContext;
import org.dcm4che3.net.service.BasicCStoreSCP;
import org.dcm4che3.net.service.DicomServiceException;
import org.dcm4che3.util.SafeClose;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

/**
 * SCP 接收文件存储处理类
 *
 * @Description:
 * @Author: HoleLin
 * @CreateDate: 2022/12/5 15:17
 * @UpdateUser: HoleLin
 * @UpdateDate: 2022/12/5 15:17
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
@Slf4j
public class StoreScpDimseHandler extends BasicCStoreSCP {
    private final String storageDir;
    private static int[] responseDelays;

    public StoreScpDimseHandler(String workPath) {
        super("*");
        this.storageDir = workPath;
    }

    @Override
    protected void store(Association as, PresentationContext pc,
                         Attributes rq, PDVInputStream data, Attributes rsp)
            throws IOException {

        try {
            rsp.setInt(Tag.Status, VR.US, 0);
            if (Objects.isNull(storageDir)) {
                log.warn("workPath is null");
                return;
            }
            final File dir = new File(storageDir);
            if (!Files.exists(dir.toPath()) && !dir.mkdirs()) {
                return;
            }
            String cuid = rq.getString(Tag.AffectedSOPClassUID);
            String iuid = rq.getString(Tag.AffectedSOPInstanceUID);
            String tsuid = pc.getTransferSyntax();

            File file = new File(dir, iuid);
            try {
                log.info("{}: M-WRITE {}.", as, file);
                final Attributes information = as.createFileMetaInformation(iuid, cuid, tsuid);
                storeTo(information,
                        data, file);
                renameTo(file, new File(storageDir, iuid));
            } catch (Exception e) {
                if (file.delete()) {
                    log.info("{}: M-DELETE {}", as, file);
                } else {
                    log.warn("{}: M-DELETE {} failed!", as, file);
                }
                throw new DicomServiceException(Status.ProcessingFailure, e);
            }
        } finally {
            sleep(as, responseDelays);
        }
    }

    /**
     * 存储DICOM文件
     *
     * @param fmi  dicom属性信息
     * @param data 数据
     * @param file 最终保存的文件对象
     * @throws IOException
     */
    private void storeTo(Attributes fmi,
                         PDVInputStream data, File file) throws IOException {
        file.getParentFile().mkdirs();
        DicomOutputStream out = new DicomOutputStream(file);
        try {
            out.writeFileMetaInformation(fmi);
            data.copyTo(out);
        } finally {
            SafeClose.close(out);
        }
    }


    /**
     * 修改名称
     *
     * @param from 原始文件对象
     * @param dest 最终文件对象
     * @throws IOException
     */
    private void renameTo(File from, File dest) {
        try {
            dest.getParentFile().mkdirs();
            Files.move(from.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sleep(Association as, int[] delays) {
        int responseDelay = delays != null
                ? delays[(as.getNumberOfReceived(Dimse.C_STORE_RQ) - 1) % delays.length]
                : 0;
        if (responseDelay > 0) {
            try {
                Thread.sleep(responseDelay);
            } catch (InterruptedException ignore) {
            }
        }
    }
}