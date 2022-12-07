package cn.holelin.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @Description: Zip文件格式压缩与解压缩
 * @Author: HoleLin
 * @CreateDate: 2022/10/20 10:21
 * @UpdateUser: HoleLin
 * @UpdateDate: 2022/10/20 10:21
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
public class ZipUtil {

    /**
     * 压缩单个文件,文件格式为.zip
     *
     * @param srcFilePath 原始文件绝对路径
     * @param zipFilePath 压缩后的文件绝对路径
     */
    public static void zipFile(String srcFilePath, String zipFilePath) {
        final File scrFile = new File(srcFilePath);
        try (
                final FileOutputStream fileOutputStream = new FileOutputStream(zipFilePath);
                final ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);
                final FileInputStream fileInputStream = new FileInputStream(scrFile)
        ) {
            final ZipEntry zipEntry = new ZipEntry(scrFile.getName());
            zipOutputStream.putNextEntry(zipEntry);
            final byte[] bytes = new byte[1024];
            int length;
            while ((length = fileInputStream.read(bytes)) >= 0) {
                zipOutputStream.write(bytes, 0, length);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 压缩多个路径的文件
     *
     * @param srcFilePathList 原始文件绝对路径列表
     * @param zipFilePath     压缩后的文件绝对路径
     */
    public static void zipFiles(String[] srcFilePathList, String zipFilePath) {
        try (
                final FileOutputStream fileOutputStream = new FileOutputStream(zipFilePath);
                final ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);
        ) {
            for (String s : srcFilePathList) {
                final File scrFile = new File(s);
                try (final FileInputStream fileInputStream = new FileInputStream(scrFile)) {
                    final ZipEntry zipEntry = new ZipEntry(scrFile.getName());
                    zipOutputStream.putNextEntry(zipEntry);
                    final byte[] bytes = new byte[1024];
                    int length;
                    while ((length = fileInputStream.read(bytes)) >= 0) {
                        zipOutputStream.write(bytes, 0, length);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void zipDirectory(String scrDirectoryPath, String zipFilePath) {
        final File srcDirectory = new File(scrDirectoryPath);
        try (
                final FileOutputStream fileOutputStream = new FileOutputStream(zipFilePath);
                final ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);

        ) {
            zipFile(srcDirectory, srcDirectory.getName(), zipOutputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(new ZipEntry(fileName));
                zipOut.closeEntry();
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
                zipOut.closeEntry();
            }
            File[] children = fileToZip.listFiles();
            for (var childFile : children) {
                // Recursively apply function to all children
                zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
            }
            return;
        }
        try (final FileInputStream fis = new FileInputStream(fileToZip)) {
            final ZipEntry zipEntry = new ZipEntry(fileName);
            zipOut.putNextEntry(zipEntry);
            final byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
        }
    }

    public static Integer[] performLottery(int numNumbers, int numbersToPick) {
        var numbers = new ArrayList<Integer>();
        for(var i = 0; i < numNumbers; i++) {
            numbers.add(i+1);
        }

        Collections.shuffle(numbers);
        return numbers.subList(0, numbersToPick).toArray(new Integer[numbersToPick]);
    }

    public static void main(String[] args) {
        for (Integer integer : performLottery(100, 8)) {
            System.out.println(integer);
        }
    }
}
