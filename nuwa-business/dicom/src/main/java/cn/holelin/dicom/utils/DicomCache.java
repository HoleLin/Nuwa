package cn.holelin.dicom.utils;

import cn.holelin.dicom.domain.DicomImagePretreatment;
import cn.holelin.dicom.domain.DicomSeries;
import cn.holelin.dicom.domain.DicomStudy;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description:
 * @Author: HoleLin
 * @CreateDate: 2022/5/19 3:17 PM
 * @UpdateUser: HoleLin
 * @UpdateDate: 2022/5/19 3:17 PM
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
public class DicomCache {

    private ConcurrentHashMap<String, DicomStudy> studyMap = new ConcurrentHashMap<>(16);
    private ConcurrentHashMap<String, DicomSeries> seriesMap = new ConcurrentHashMap<>(16);

    private List<DicomImagePretreatment> dicomImages;
    private String businessId;

    public ConcurrentHashMap<String, DicomStudy> getStudyMap() {
        return studyMap;
    }

    public ConcurrentHashMap<String, DicomSeries> getSeriesMap() {
        return seriesMap;
    }

    public void setDicomImages(List<DicomImagePretreatment> dicomImages) {
        this.dicomImages = dicomImages;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public void destroy() {
        studyMap = null;
        seriesMap = null;
    }
}
