package cn.holelin.common.utils;

import java.util.ArrayList;
import org.junit.jupiter.api.Test;

class ZipUtilTest {

    @Test
    void zipFile() {
        final String srcFilePath = "/Users/holelin/Downloads/FC8E4602-0958-4853-8732-A47C24663308.png";
        final String zipFilePath = "/Users/holelin/Downloads/FC8E4602-0958-4853-8732-A47C24663308.zip";
        ZipUtil.zipFile(srcFilePath, zipFilePath);
    }


    @Test
    void zipFiles() {
        String[] scrFilePathList = {"/Users/holelin/Downloads/FC8E4602-0958-4853-8732-A47C24663308.png",
                "/Users/holelin/Downloads/泰莱10月份订单数据(截止至1019).xlsx"};
        final String zipFilePath = "/Users/holelin/Downloads/multi.zip";
        ZipUtil.zipFiles(scrFilePathList, zipFilePath);
    }

    @Test
    void zipDirectory(){
        final String scrDirectoryPath = "/Users/holelin/Downloads/01-入门篇/";
        final String zipFilePath = "/Users/holelin/Downloads/directory.zip";
        ZipUtil.zipDirectory(scrDirectoryPath, zipFilePath);

    }


}