package cn.holelin.common.file.zip;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * @Description: 解压缩策略
 * @Author: HoleLin
 * @CreateDate: 2022/3/29 4:49 PM
 * @UpdateUser: HoleLin
 * @UpdateDate: 2022/3/29 4:49 PM
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
public interface CompressStrategy {


    /**
     * 压缩文件
     *
     * @param targetFile 待压缩的文件(/文件夹)
     * @return 生成的压缩包的路径 默认生成的压缩包与待压缩的文件同级
     */
    String zip(Path targetFile);

    /**
     * 压缩文件并设置压缩包名称(不包含尾缀)
     *
     * @param targetFile 待压缩的文件(/文件夹)
     * @param fileName   压缩包名称
     * @return 压缩后的完整文件路径
     */
    String zip(Path targetFile, String fileName);

    /**
     * 解压文件
     *
     * @param targetFile 待解压的文件
     * @throws IOException 文件不存在异常
     */
    String unzip(Path targetFile) throws IOException;

    /**
     * 解压文件
     *
     * @param targetFile 待解压的文件
     * @param descDir    解压后的文件目录
     * @throws IOException 文件不存在异常
     */
    String unzip(Path targetFile, String descDir) throws IOException;

    /**
     * 验证文件格式是否符合当前策略
     *
     * @param file 待验证的文件对象
     * @return true 符合;false 不符合
     */
    boolean verifyFileFormat(File file);

}
