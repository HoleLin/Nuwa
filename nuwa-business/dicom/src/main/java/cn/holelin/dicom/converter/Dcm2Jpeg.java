package cn.holelin.dicom.converter;


import org.dcm4che3.imageio.plugins.dcm.DicomImageReadParam;
import org.dcm4che3.imageio.plugins.dcm.DicomImageReader;
import org.dcm4che3.imageio.plugins.dcm.DicomImageReaderSpi;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageInputStream;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;

/**
 * @author HoleLin
 */
public class Dcm2Jpeg {

    public static void main(String[] args) throws IOException {
        String sourcePath = "C:\\Users\\YW\\Desktop\\23200905data\\1148628816594427904\\b737a3e2-51ee-4ff1-92ad-98a2fd27dfa0";
        String outputPath = "C:\\Users\\YW\\Desktop\\23200905data\\test5.jpeg";
        // 读取DICOM文件
        FileImageInputStream dicomStream = new FileImageInputStream(new File(sourcePath));

        // 创建DICOM图像阅读器
        DicomImageReader reader = new DicomImageReader(new DicomImageReaderSpi());
        reader.setInput(dicomStream);

        // 获取像素数据和窗口函数参数
        int width = reader.getWidth(0);
        int height = reader.getHeight(0);
        Raster raster = reader.readRaster(0, new DicomImageReadParam());

        // 创建BufferedImage对象并填充像素数据
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        image.setData(raster);
        // 将BufferedImage对象写入JPEG文件
        File jpegFile = new File(outputPath);
        ImageIO.write(image, "JPEG", jpegFile);
    }


}
