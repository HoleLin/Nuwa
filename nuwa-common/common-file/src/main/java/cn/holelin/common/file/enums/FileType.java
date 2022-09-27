package cn.holelin.common.file.enums;

/**
 * @Description:
 * @Author: HoleLin
 * @CreateDate: 2022/3/30 1:36 PM
 * @UpdateUser: HoleLin
 * @UpdateDate: 2022/3/30 1:36 PM
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
public enum FileType {

    /**
     * 压缩包
     */
    ZIP("504B0304", ".zip", 0),
    RAR("526172211A07", ".rar", 0),
    /**
     * DICOM影像
     * 文件开头会有128字节的导言，这部分数据没有内容。接着是4字节DICOM文件标识，存储这"DICM"。
     */
    DICOM("4449434D", ".dcm", 0x80),
    /**
     * 图片
     */
    JPG("FFD8", ".jpg", 0),
    PNG("89504E470D0A1A0A", ".png", 0),

    /**
     * Excel
     */
    XLS_2003("D0CF11E0A1B11AE1", ".xls", 0),
    XLSX_2007("504B0304", ".xlsx", 0),
    /**
     * PDF
     */
    PDF("25504446", ".pdf", 0),
    /**
     * 未知 尚未兼容
     */
    UNKNOWN("", "", 0);
    public String signature;
    public String extension;
    public int offset;

    FileType(String signature, String extension, int offset) {
        this.signature = signature;
        this.extension = extension;
        this.offset = offset;
    }

}
