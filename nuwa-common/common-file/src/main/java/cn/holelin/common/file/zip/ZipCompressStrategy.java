package cn.holelin.common.file.zip;

import cn.holelin.common.file.FileFormatValidator;
import cn.holelin.common.file.enums.FileType;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * @Description: 处理Zip包的策略
 * @Author: HoleLin
 * @CreateDate: 2022/3/29 4:53 PM
 * @UpdateUser: HoleLin
 * @UpdateDate: 2022/3/29 4:53 PM
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
public class ZipCompressStrategy extends AbstractCompressStrategy {

    private FileFormatValidator fileFormatValidator = new FileFormatValidator();

    @Override
    public boolean verifyFileFormat(File file) {
        return fileFormatValidator.isZip(file);
    }

    @Override
    public String unzip(Path targetFile) throws IOException {
        final File zipFile = targetFile.toFile();
        if (verifyFileFormat(zipFile)) {
            final String currentDir = zipFile.getPath()
                    .replace(zipFile.getName(), EMPTY_STRING);
            return doUnzip(zipFile, currentDir);
        } else {
            return EMPTY_STRING;
        }
    }

    @Override
    public String unzip(Path targetFile, String descDir) throws IOException {
        final File zipFile = targetFile.toFile();
        if (verifyFileFormat(zipFile)) {
            return doUnzip(zipFile, descDir);
        } else {
            return EMPTY_STRING;
        }
    }

    /**
     * @param targetFile 需要被压缩的文件
     */
    @Override
    public String zip(Path targetFile) {
        final File zipFile = targetFile.toFile();
        final String fileName = zipFile.getName();
        return zipWithOutputFileName(zipFile, fileName);
    }

    @Override
    public String zip(Path targetFile, String fileName) {
        final File zipFile = targetFile.toFile();
        return zipWithOutputFileName(zipFile, fileName);
    }

    /**
     * 压缩文件并指定压缩包的名称
     *
     * @param file     压缩包文件对象
     * @param fileName 压缩包的名称
     * @return 压缩后的文件路径
     */
    private String zipWithOutputFileName(File file, String fileName) {
        final String parent = file.getParent();
        File outputFile = new File(parent, fileName + FileType.ZIP.extension);
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(outputFile))) {
            doZip(zos, file, file.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputFile.getPath();
    }

    /**
     * 压缩文件
     *
     * @param zos        zip流
     * @param file       待压缩的文件
     * @param suffixPath 文件路径前缀
     * @throws IOException
     */
    private void doZip(ZipOutputStream zos, File file, String suffixPath) throws IOException {
        if (file.isDirectory()) {
            // 压缩文件夹
            // 文件夹的目录进入点必须以名称分隔符结尾
            String newPath = suffixPath + File.separator;
            ZipEntry entry = new ZipEntry(newPath);
            zos.putNextEntry(entry);
            for (File childFile : Objects.requireNonNull(file.listFiles())) {
                // 过滤苹果系统的.DS_Store文件
                final String childFileName = childFile.getName();
                if (AbstractCompressStrategy.EXCLUDE_SET.contains(childFileName)) {
                    continue;
                }
                doZip(zos, childFile, newPath + childFileName);
            }
        } else {
            // 压缩单个文件
            // 目录进入点的名字是文件在压缩文件中的路径
            try (FileInputStream fis = new FileInputStream(file)) {
                ZipEntry entry = new ZipEntry(suffixPath);
                // 建立一个目录进入
                zos.putNextEntry(entry);
                int len = 0;
                byte[] buf = new byte[1024];
                while ((len = fis.read(buf)) != -1) {
                    zos.write(buf, 0, len);
                }
            }
            zos.flush();
            // 关闭当前目录进入点，将输入流移动下一个目录进入点
            zos.closeEntry();
        }
    }

    /**
     * 解压文件
     *
     * @param zipFile 压缩包文件对象
     * @param descDir 解压后的文件目录
     * @throws IOException
     */
    private String doUnzip(File zipFile, String descDir) throws IOException {
        if (descDir != null && !descDir.endsWith("/")) {
            // 必须为目录形式
            throw new IOException("必须为目录形式");
        }
        String result = EMPTY_STRING;
        //解决zip文件中有中文目录或者中文文件
        try (ZipFile zip = new ZipFile(zipFile, StandardCharsets.UTF_8)) {
            for (Enumeration<? extends ZipEntry> entries = zip.entries(); entries.hasMoreElements(); ) {
                ZipEntry entry = entries.nextElement();
                String zipEntryName = entry.getName();
                try (InputStream in = zip.getInputStream(entry)) {
                    // zipEntryName中不包含EXCLUDE_SET的内容
                    boolean noSkip = true;
                    for (String exclude : EXCLUDE_SET) {
                        if (zipEntryName.contains(exclude)) {
                            noSkip = false;
                            break;
                        }
                    }
                    if (noSkip) {
                        String outPath = (descDir + zipEntryName).replace("\\*", File.separator);
                        if (result.equals(EMPTY_STRING)) {
                            result = outPath;
                        }
                        //判断路径是否存在,不存在则创建文件路径
                        File file = new File(outPath.substring(0, outPath.lastIndexOf(File.separator)));
                        if (!file.exists()) {
                            file.mkdirs();
                        }
                        //判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压
                        if (new File(outPath).isDirectory()) {
                            continue;
                        }
                        //输出文件路径信息
                        try (OutputStream out = new FileOutputStream(outPath)) {
                            byte[] buffer = new byte[1024];
                            int len;
                            while ((len = in.read(buffer)) > 0) {
                                out.write(buffer, 0, len);
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    public static void main(String[] args) throws IOException {
        final ZipCompressStrategy zipCompressStrategy = new ZipCompressStrategy();
        final String filePath = "/Users/holelin/temp/测试";
        final String zipFilePath = zipCompressStrategy.zip(Path.of(filePath), "测试2");
        System.out.println(zipCompressStrategy.unzip(Path.of(zipFilePath), "/Users/holelin/temp/new/"));
        System.out.println(zipFilePath);

    }
}
