package cn.holelin.dicom.pacs_v1.support;

import cn.holelin.dicom.pacs_v1.base.DefaultPacsBehaviorSupport;
import cn.holelin.dicom.pacs_v1.domain.PacsBaseConfig;
import cn.holelin.dicom.pacs_v1.enums.InformationModelEnum;
import cn.holelin.dicom.pacs_v1.request.PacsSearchRequest;
import cn.holelin.dicom.pacs_v1.response.PacsSearchResponse;
import cn.holelin.dicom.pacs_v1.utils.PacsHelper;
import cn.hutool.core.lang.Assert;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.net.Association;
import org.dcm4che3.net.DimseRSPHandler;
import org.dcm4che3.net.Priority;
import org.dcm4che3.net.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

/**
 * FIND SCU实现类
 *
 * @author HoleLin
 */
public class PacsCFindSupport extends DefaultPacsBehaviorSupport {
    private final Logger log = LoggerFactory.getLogger(PacsCFindSupport.class);

    public PacsCFindSupport(PacsBaseConfig config) {
        super(config);
    }
    public PacsCFindSupport(PacsBaseConfig config,Boolean needExtendedNegotiation) {
        super(config,needExtendedNegotiation);
    }

    @Override
    public List<PacsSearchResponse> doExecuteWithResult(InformationModelEnum model, Attributes conditions) {
        List<PacsSearchResponse> result = new ArrayList<>();
        Association association = getAssociation();
        Assert.isTrue(Objects.nonNull(association), "无法连接远端PACS服务,请检查远端PACS配置是否正常!");
        CountDownLatch latch = new CountDownLatch(1);
        CFindDimseRSPHandler dimseRspHandler = new CFindDimseRSPHandler(association.nextMessageID(), latch, result);
        try {
            association.cfind(model.getCuid(), Priority.NORMAL, conditions,
                    null, dimseRspHandler);
            latch.await();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            disconnect();
        }
        return result;
    }


    /**
     * C-FIND 处理器
     */
    static class CFindDimseRSPHandler extends DimseRSPHandler {

        int cancelAfter;
        int numMatches;
        CountDownLatch latch;
        List<PacsSearchResponse> result;

        public CFindDimseRSPHandler(int msgId, CountDownLatch latch, List<PacsSearchResponse> result) {
            super(msgId);
            this.latch = latch;
            this.result = result;
        }


        @Override
        public void onDimseRSP(Association as, Attributes cmd, Attributes data) {
            super.onDimseRSP(as, cmd, data);
            int status = cmd.getInt(Tag.Status, -1);
            if (Status.isPending(status)) {
                PacsSearchResponse pacsSearchResponse = new PacsSearchResponse();
                pacsSearchResponse.setPatientId(data.getString(Tag.PatientID));
                pacsSearchResponse.setPatientName(data.getString(Tag.PatientName));
                pacsSearchResponse.setStudyDate(data.getString(Tag.StudyDate));
                pacsSearchResponse.setStudyTime(data.getString(Tag.StudyTime));
                pacsSearchResponse.setAccessionNumber(data.getString(Tag.AccessionNumber));
                pacsSearchResponse.setNumberOfSeriesRelatedInstances(data.getInt(Tag.NumberOfSeriesRelatedInstances, 0));
                pacsSearchResponse.setSeriesInstanceUid(data.getString(Tag.SeriesInstanceUID));
                pacsSearchResponse.setStudyInstanceUid(data.getString(Tag.StudyInstanceUID));

                pacsSearchResponse.setModality(data.getString(Tag.Modality));
                pacsSearchResponse.setBodyPartExamined(data.getString(Tag.BodyPartExamined));
                pacsSearchResponse.setStudyDescription(data.getString(Tag.StudyDescription));
                pacsSearchResponse.setSeriesDescription(data.getString(Tag.SeriesDescription));
                pacsSearchResponse.setSliceThickness(data.getDouble(Tag.SliceThickness,0D));
                pacsSearchResponse.setPatientAge(data.getString(Tag.PatientAge));
                pacsSearchResponse.setPatientSex(data.getString(Tag.PatientSex));
                result.add(pacsSearchResponse);
                ++numMatches;
                if (cancelAfter != 0 && numMatches >= cancelAfter) {
                    try {
                        cancel(as);
                        cancelAfter = 0;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (Status.Success == status) {
                latch.countDown();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        PacsBaseConfig pacsScuConfig = new PacsBaseConfig();
        pacsScuConfig.setRemotePort(11112);
        pacsScuConfig.setRemoteHostName("192.168.11.216");
        pacsScuConfig.setRemoteAeTitle("DCM4CHEE");
        String testFindScu = "test_find_scu";
        pacsScuConfig.setAeTitle(testFindScu);
        pacsScuConfig.setDeviceName(testFindScu);


        PacsCFindSupport support = new PacsCFindSupport(pacsScuConfig);
        PacsSearchRequest request = new PacsSearchRequest();
//        request.setStudyInstanceUid("1.2.840.113619.186.18420258241203197.20180813103736326.517");

        System.out.println(support.echo());
        List<PacsSearchResponse> result = support.executeWithResult(InformationModelEnum.FIND, PacsHelper.buildFindAttributes(request));
        System.out.println(result.size());
    }

}
