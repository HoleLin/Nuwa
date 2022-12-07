package cn.holelin.dicom.domain;

import lombok.Data;
import org.dcm4che3.data.Attributes;

import java.io.File;

/**
 * @Description: Dicom Image 预处理Bean
 * @Author: HoleLin
 * @CreateDate: 2022/5/7 1:53 PM
 * @UpdateUser: HoleLin
 * @UpdateDate: 2022/5/7 1:53 PM
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
@Data
public class DicomImagePretreatment {
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
    private File file;

    /**
     * 源文件名称
     */
    private String sourceFileName;

    /**
     * -------------------------------------
     *
     *      Dicom Attributes
     *
     * -------------------------------------
     */

    private Attributes metaData;
    private Attributes attributes;
    private Attributes dataSet;


    /**
     * 清空读取到的Dicom信息
     */
    public void clean() {
        this.metaData = null;
        this.attributes = null;
        this.dataSet = null;
    }
}
