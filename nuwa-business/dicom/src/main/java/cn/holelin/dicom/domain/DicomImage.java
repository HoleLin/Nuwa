package cn.holelin.dicom.domain;

import lombok.Data;

import java.io.File;
import java.time.LocalDate;

/**
 * @Description:
 * @Author: HoleLin
 * @CreateDate: 2022/5/19 3:49 PM
 * @UpdateUser: HoleLin
 * @UpdateDate: 2022/5/19 3:49 PM
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
@Data
public class DicomImage extends DicomSeriesBaseInfo{

    public DicomImage() {
    }
    /**
     * -------------------------------------
     *
     *      Source File Information
     *
     * -------------------------------------
     */
    /**
     * dicom对应的文件对象
     */
    private File sourceFile;

    /**
     * 源文件名称
     */
    private String sourceFileName;

    /**
     * 处理后的文件对象
     */
    private File afterHandleFile;
    /**
     * 处理后的文件名
     */
    private String afterHandleFileName;

    /**
     * -------------------------------------
     *
     *      Patient Information
     *
     * -------------------------------------
     */
    private String patientName;
    private String patientSex;
    private String patientAge;
    private String patientID;
    private LocalDate patientBirthDate;
    /**
     *
     */
    private String institutionName;
    /**
     *
     */
    private String sOPInstanceUID;
    /**
     * 检查号：RIS的生成序号,用以标识做检查的次序.
     */
    private String accessionNumber;
    private String convolutionKernel;
    private String protocolName;
    /**
     * 检查的部位
     */
    private String bodyPartExamined;
    /**
     * 层厚
     */
    private Double sliceThickness;
    /**
     * 层间距,单位mm
     */
    private Double spacingBetweenSlices;
    /**
     * 检查模态(MRI/CT/CR/DR)
     */
    private String modality;
    private String imageType;
    private String manufacturer;
    private String manufacturerModelName;
    private String stationName;
    private Integer instanceNumber;
    /**
     * 图像的总行数,行分辨率
     */
    private String rows;
    /**
     * 图像的总列数,列分辨率
     */
    private String columns;

}
