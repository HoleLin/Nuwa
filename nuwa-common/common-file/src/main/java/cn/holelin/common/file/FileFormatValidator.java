package cn.holelin.common.file;

import cn.holelin.common.file.enums.FileType;
import cn.hutool.core.util.HexUtil;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Optional;

/**
 * @Description: 文件格式验证器
 * @Author: HoleLin
 * @CreateDate: 2022/3/29 4:58 PM
 * @UpdateUser: HoleLin
 * @UpdateDate: 2022/3/29 4:58 PM
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
public class FileFormatValidator {

    /**
     * 文件签名长度
     */
    private static final int FILE_SIGNATURE_LENGTH = 8;
    /**
     * 文件只读模式
     */
    private static final String FILE_ACCESS_MODE_READ = "r";

    /**
     * 根据文件对象推断文件类型
     *
     * @param file 具体文件对象
     * @return 文件类型
     */
    public FileType deduceFileType(File file, int offset) {
        final byte[] bytes = new byte[FILE_SIGNATURE_LENGTH];
        try (final RandomAccessFile randomAccessFile =
                     new RandomAccessFile(file, FILE_ACCESS_MODE_READ)) {
            randomAccessFile.skipBytes(offset);
            randomAccessFile.read(bytes);
            final String magicNumber = HexUtil.encodeHexStr(bytes).toUpperCase();
            System.out.println(magicNumber);
            final Optional<FileType> fileTypeOptional = Arrays.stream(FileType.values())
                    .filter(it -> magicNumber.startsWith(it.signature))
                    .findFirst();
            if (fileTypeOptional.isPresent()) {
                return fileTypeOptional.get();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return FileType.UNKNOWN;
    }

    /**
     * 根据文件路径推断文件类型
     *
     * @param filePath 文件路径
     * @return 文件类型
     */
    public FileType deduceFileType(String filePath, int offset) {
        final File file = new File(filePath);
        return deduceFileType(file, offset);
    }

    public Boolean isDicom(File file) {
        return FileType.DICOM == deduceFileType(file, FileType.DICOM.offset);
    }

    public static void main(String[] args) {
        System.out.println();
        final String pathname = "/Users/holelin/Downloads/E-HP11-00273/E-HP11-00273_13.dcm";
        final FileFormatValidator fileFormatValidator = new FileFormatValidator();
        System.out.println(fileFormatValidator.isDicom(new File(pathname)));
    }

    public Boolean isZip(File file) {
        return FileType.ZIP == deduceFileType(file, FileType.ZIP.offset);
    }

}
