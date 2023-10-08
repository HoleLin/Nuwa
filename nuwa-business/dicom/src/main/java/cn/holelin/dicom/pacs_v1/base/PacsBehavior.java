package cn.holelin.dicom.pacs_v1.base;

/**
 *
 */
public interface PacsBehavior {

    /**
     * 建立连接
     */
    void connect() throws Exception;

    /**
     * 关闭连接
     */
    void disconnect();

    /**
     * 连通性判断
     *
     * @return true 可以通信,false 无法通信
     */
    boolean echo();
}
