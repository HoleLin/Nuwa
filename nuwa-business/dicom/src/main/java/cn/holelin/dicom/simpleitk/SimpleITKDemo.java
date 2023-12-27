package cn.holelin.dicom.simpleitk;

import org.itk.simple.*;
import org.springframework.util.StopWatch;

import java.io.File;

/**
 * @author HoleLin
 */
public class SimpleITKDemo {

    public static void main(String[] args) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String inputPath = "G:\\data\\dev_ct\\zips\\1.3.12.2.1107.5.1.4.73749.30000021113000050848300229690";
        String outputPath = "G:\\data\\dev_ct\\zips\\1.3.12.2.1107.5.1.4.73749.30000021113000050848300229690\\output.nii.gz";

        ImageSeriesReader imageSeriesReader = new ImageSeriesReader();
        final VectorString dicomNames = ImageSeriesReader.getGDCMSeriesFileNames(inputPath);
        imageSeriesReader.setFileNames(dicomNames);
        Image image = imageSeriesReader.execute();

        SimpleITK.writeImage(image, outputPath);
        stopWatch.stop();
        System.out.println(stopWatch.getLastTaskTimeMillis());
    }
}
