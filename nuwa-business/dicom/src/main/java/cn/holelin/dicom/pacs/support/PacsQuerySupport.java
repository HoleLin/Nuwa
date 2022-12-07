package cn.holelin.dicom.pacs.support;

import cn.holelin.dicom.pacs.enums.InformationModelEnum;
import cn.holelin.dicom.pacs.param.PacsQueryParam;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.UID;
import org.dcm4che3.net.Association;
import org.dcm4che3.net.DimseRSPHandler;
import org.dcm4che3.net.Priority;
import org.dcm4che3.net.QueryOption;
import org.dcm4che3.net.Status;
import org.dcm4che3.net.pdu.ExtendedNegotiation;
import org.dcm4che3.net.pdu.PresentationContext;

import java.io.IOException;
import java.util.EnumSet;

/**
 * PACS 查询支持类
 *
 * @Description: C-FIND实现类
 * @Author: HoleLin
 * @CreateDate: 2022/12/7 17:27
 * @UpdateUser: HoleLin
 * @UpdateDate: 2022/12/7 17:27
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
public class PacsQuerySupport extends AbstractPacsOperationTemplate {


    public PacsQuerySupport(PacsQueryParam param) {
        super(param);
    }
    @Override
    public void preConnect(InformationModelEnum model) {
        aarq.addPresentationContext(new PresentationContext(
                this.aarq.getNumberOfPresentationContexts() * 2 + 1, model.cuid,
                CT_IMAGE_STORAGE
        ));
        final EnumSet<QueryOption> queryOptions = EnumSet.allOf(QueryOption.class);
        aarq.addExtendedNegotiation(new ExtendedNegotiation(model.cuid, QueryOption.toExtendedNegotiationInformation(queryOptions)));
    }

    @Override
    public void execute(InformationModelEnum model, Attributes conditions) throws IOException, InterruptedException {
        // 执行c-find
        this.association.cfind(model.cuid, Priority.NORMAL, conditions,
                null, new DimseRSPHandler(association.nextMessageID()) {
                    int cancelAfter;
                    int numMatches;
                    @Override
                    public void onDimseRSP(Association as, Attributes cmd, Attributes data) {
                        super.onDimseRSP(as, cmd, data);
                        int status = cmd.getInt(Tag.Status, -1);
                        if (Status.isPending(status)) {
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
                    }
                });
    }

    /**
     * 构建查询条件
     * @return
     */
    public Attributes buildQueryConditions() {
        return new Attributes();
    }

}
