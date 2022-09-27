package cn.holelin.common.file.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * @Description:
 * @Author: HoleLin
 * @CreateDate: 2022/5/22 3:51 PM
 * @UpdateUser: HoleLin
 * @UpdateDate: 2022/5/22 3:51 PM
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
public class FileOperationsUtil {


    public static void move(File sourceFile, String targetPath) throws IOException {
        if (Objects.nonNull(sourceFile) && !sourceFile.exists()) {
            return;
        }
        final Path move = Files.move(sourceFile.toPath(), Paths.get(targetPath));
    }

    public void copy() {

    }

    public static void main(String[] args) throws IOException {
        String sourcePath = "/Users/holelin/Projects/MySelf/Java-Notes/doc/1.txt";
        String targetPath = "/Users/holelin/Projects/MySelf/Java-Notes/doc/temp/";
        final File sourceFile = new File(sourcePath);
        move(sourceFile, targetPath);
    }

}
