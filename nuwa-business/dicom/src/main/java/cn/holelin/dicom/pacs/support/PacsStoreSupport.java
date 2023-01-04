package cn.holelin.dicom.pacs.support;

import cn.holelin.dicom.pacs.enums.InformationModelEnum;
import cn.holelin.dicom.pacs.param.PacsStoreParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.UID;
import org.dcm4che3.io.DicomInputStream;
import org.dcm4che3.net.Association;
import org.dcm4che3.net.DimseRSPHandler;
import org.dcm4che3.net.InputStreamDataWriter;
import org.dcm4che3.net.Status;
import org.dcm4che3.net.pdu.PresentationContext;
import org.dcm4che3.tool.common.DicomFiles;
import org.dcm4che3.util.SafeClose;
import org.dcm4che3.util.TagUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * PACS 推图实现类
 *
 * @Description:
 * @Author: HoleLin
 * @CreateDate: 2022/12/7 17:29
 * @UpdateUser: HoleLin
 * @UpdateDate: 2022/12/7 17:29
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
public class PacsStoreSupport extends AbstractPacsOperationTemplate {
    private String dicomDir;

    private List<ScuInfo> list = new ArrayList<>();

    public PacsStoreSupport(PacsStoreParam param) {
        super(param);
        this.dicomDir = param.getDicomDir();
    }

    @Override
    public void preConnect(InformationModelEnum model) {
        final Path path = Paths.get(dicomDir);
        doScan(path, (file, fmi, dsPos, ds) -> {
            if (!addFile(file, dsPos, fmi)) {
                return false;
            }
            return true;
        });
    }

    @Override
    public void execute(InformationModelEnum model, Attributes conditions) throws IOException, InterruptedException {
        for (ScuInfo info : list) {
            if (this.association.isReadyForDataTransfer()) {
                try {
                    send(new File(info.getFilePath()), info.getEndFmi(), info.getCuid(), info.getIuid(),
                            info.getTs());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    this.association.waitForOutstandingRSP();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
        list.clear();
    }

    private void doScan(Path path, DicomFiles.Callback callback) {
        if (Files.isDirectory(path)) {
            try {
                Files.list(path).forEach(it -> doScan(it, callback));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        } else {
            final File file = path.toFile();
            try (DicomInputStream in = new DicomInputStream(file)) {
                in.setIncludeBulkData(DicomInputStream.IncludeBulkData.NO);
                Attributes fmi = in.readFileMetaInformation();
                long dsPos = in.getPosition();
                Attributes ds = in.readDatasetUntilPixelData();
                if (fmi == null || !fmi.containsValue(Tag.TransferSyntaxUID)
                        || !fmi.containsValue(Tag.MediaStorageSOPClassUID)
                        || !fmi.containsValue(Tag.MediaStorageSOPInstanceUID)) {
                    fmi = ds.createFileMetaInformation(in.getTransferSyntax());
                }
                boolean b = callback.dicomFile(file, fmi, dsPos, ds);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
    }

    private boolean addFile(File f, long endFmi,
                            Attributes fmi) throws IOException {
        String cuid = fmi.getString(Tag.MediaStorageSOPClassUID);
        String iuid = fmi.getString(Tag.MediaStorageSOPInstanceUID);
        String ts = fmi.getString(Tag.TransferSyntaxUID);
        if (cuid == null || iuid == null) {
            return false;
        }
        final ScuInfo scuInfo = ScuInfo.builder().cuid(cuid).ts(ts).iuid(iuid).endFmi(endFmi).filePath(f.getPath())
                .build();

        list.add(scuInfo);
        if (this.aarq.containsPresentationContextFor(cuid, ts)) {
            return true;
        }

        if (!this.aarq.containsPresentationContextFor(cuid)) {
            if (!ts.equals(UID.ExplicitVRLittleEndian)) {
                this.aarq.addPresentationContext(
                        new PresentationContext(this.aarq.getNumberOfPresentationContexts() * 2 + 1, cuid,
                                UID.ExplicitVRLittleEndian));
            }
            if (!ts.equals(UID.ImplicitVRLittleEndian)) {
                this.aarq.addPresentationContext(
                        new PresentationContext(this.aarq.getNumberOfPresentationContexts() * 2 + 1, cuid,
                                UID.ImplicitVRLittleEndian));
            }
        }
        this.aarq.addPresentationContext(
                new PresentationContext(this.aarq.getNumberOfPresentationContexts() * 2 + 1, cuid, ts));
        return true;
    }

    public void send(final File f, long fmiEndPos, String cuid, String iuid,
                     String filets) throws FileNotFoundException {
        String ts = selectTransferSyntax(cuid, filets);

        FileInputStream in = new FileInputStream(f);
        try {
            in.skip(fmiEndPos);
            InputStreamDataWriter data = new InputStreamDataWriter(in);
            this.association.cstore(cuid, iuid, 0, data, ts,
                    rspHandlerFactory.createDimseRspHandler(this.association, f));
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            SafeClose.close(in);
        }
    }

    private String selectTransferSyntax(String cuid, String filets) {
        Set<String> tss = this.association.getTransferSyntaxesFor(cuid);
        if (tss.contains(filets)) {
            return filets;
        }

        if (tss.contains(UID.ExplicitVRLittleEndian)) {
            return UID.ExplicitVRLittleEndian;
        }

        return UID.ImplicitVRLittleEndian;
    }

    public interface RspHandlerFactory {

        DimseRSPHandler createDimseRspHandler(Association as, File file);
    }

    private RspHandlerFactory rspHandlerFactory = new RspHandlerFactory() {

        @Override
        public DimseRSPHandler createDimseRspHandler(Association as, final File file) {
            return new DimseRSPHandler(as.nextMessageID()) {
                @Override
                public void onDimseRSP(Association as, Attributes cmd,
                                       Attributes data) {
                    super.onDimseRSP(as, cmd, data);
                    onCStoreRSP(cmd, file);
                }
            };
        }
    };

    private void onCStoreRSP(Attributes cmd, File f) {
        int status = cmd.getInt(Tag.Status, -1);
        switch (status) {
            case Status.Success:

                System.out.print('.');
                break;
            case Status.CoercionOfDataElements:
            case Status.ElementsDiscarded:
            case Status.DataSetDoesNotMatchSOPClassWarning:
                System.err.println(MessageFormat.format(
                        TagUtils.shortToHexString(status), f));
                System.err.println(cmd);
                break;
            default:
                System.out.print('E');
                System.err.println(cmd);
        }
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    static class ScuInfo {

        private String cuid;
        private String iuid;
        private String ts;
        private long endFmi;
        private String filePath;

        @Override
        public String toString() {
            return "ScuInfo{" +
                    "cuid='" + cuid + '\'' +
                    ", iuid='" + iuid + '\'' +
                    ", ts='" + ts + '\'' +
                    ", endFmi=" + endFmi +
                    ", filePath='" + filePath + '\'' +
                    '}';
        }
    }

}
