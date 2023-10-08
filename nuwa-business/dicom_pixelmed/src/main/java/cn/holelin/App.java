package cn.holelin;

import com.pixelmed.dicom.*;
import com.pixelmed.display.ConsumerFormatImageMaker;
import com.pixelmed.display.SourceImage;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Hello world!
 */
public class App {
    private static AttributeList list = new AttributeList();

    public static void main(String[] args) {

        String dicomFile = "C:\\Users\\YW\\Desktop\\23200905data\\1148620123228299264\\0bdb3c34-906c-4f45-92b9-b32ef40d68cb";
        String scJpegFilePath = "C:\\Users\\YW\\Downloads\\ChestGeneralImage.jpg";
        String newDicomFile = "C:\\Users\\YW\\Downloads\\Saravanan.dcm";
//        test1(dicomFile);
//        test2(scJpegFilePath, newDicomFile);

//        test3(dicomFile);
        String outputJpgFile = "C:\\Users\\YW\\Downloads\\MR-MONO2-16-head.jpg";
        String outputPngFile = "C:\\Users\\YW\\Downloads\\MR-MONO2-16-head.png";
        String outputTiffFile = "C:\\Users\\YW\\Downloads\\MR-MONO2-16-head.tiff";
        String test4File = "C:\\Users\\YW\\Downloads\\MR-MONO2-8-16x-heart.dcm";
        String test5File = "C:\\Users\\YW\\Downloads\\CT1_J2KR.dcm";
//        test4(testFile, outputJpgFile, outputPngFile, outputTiffFile);
//        test5(test5File);
        String test6FileDir = "C:\\Users\\YW\\Downloads\\compsamples_refanddir\\compsamples_refanddir\\DICOMDIR";
//        test6(test6FileDir);
        test7();
    }

    private static void test7() {
        String dicomRootDirectory = "C:\\Users\\YW\\Desktop\\23200905data\\1148620123228299264";
        String dicomDirectoryFileName = dicomRootDirectory + File.separatorChar + "SampleDICOMDIR";

        //These are the image files that we will include as file-set
        //remember, they may be under different directories such as patient, study & series folders usually
        String imageFile1 = dicomRootDirectory + File.separatorChar + "0bdb3c34-906c-4f45-92b9-b32ef40d68cb";
        String imageFile2 = dicomRootDirectory + File.separatorChar + "1ac2b28c-42ec-46b2-a320-23d874fdf08b";
        String imageFile3 = dicomRootDirectory + File.separatorChar + "2b019e38-9c1e-4fbc-b2ba-3ae2e3ffa701";
        String imageFile4 = dicomRootDirectory + File.separatorChar + "5c40be55-a29e-40a6-9391-ada0d872ab07";

        try {
            String[] sourceFiles = new String[] {imageFile1, imageFile2, imageFile3, imageFile4};
            DicomDirectory dicomDirectory = new DicomDirectory(sourceFiles);
            dicomDirectory.write(dicomDirectoryFileName);
            System.out.println(dicomDirectory);
            DicomDirectoryBrowser.main(new String[] {dicomDirectoryFileName});
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void test6(String dicomDirectoryFileName) {
        try {
            AttributeList list = new AttributeList();
//            list.read(new DicomInputStream(new File(dicomDirectoryFileName)));
//
//            DicomDirectory dicomDirectory = new DicomDirectory(list);
//            System.out.println(dicomDirectory.toString());
            list.read(dicomDirectoryFileName);
            DicomDirectoryBrowser.main(new String[] {dicomDirectoryFileName});
        } catch (Exception e) {
            e.printStackTrace(); //in real life, do something about this exception
        }

    }

    private static void test5(String dicomFile) {
        try {
            JFrame frame = new JFrame();
            SourceImage sImg = new SourceImage(dicomFile);
            System.out.println("Number of frames: " + sImg.getNumberOfFrames());
            OverriddenSingleImagePanelForDemo singleImagePanel = new OverriddenSingleImagePanelForDemo(sImg);
            frame.add(singleImagePanel);
            frame.setBackground(Color.BLACK);
            frame.setSize(sImg.getWidth(), sImg.getHeight());
            frame.setTitle("Demo for view, scroll and window width/level operations");
            frame.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace(); //in real life, do something about this exception
        }

    }

    private static void test4(String dicomFile, String outputJpgFile, String outputPngFile, String outputTiffFile) {

        try {
            ConsumerFormatImageMaker.convertFileToEightBitImage(dicomFile, outputJpgFile, "jpeg", 0);
            ConsumerFormatImageMaker.convertFileToEightBitImage(dicomFile, outputPngFile, "png", 0);
            ConsumerFormatImageMaker.convertFileToEightBitImage(dicomFile, outputTiffFile, "tiff", 0);
        } catch (Exception e) {
            e.printStackTrace(); //in real life, do something about this exception
        }
    }

    private static void test3(String dicomFile) {
        try {
            list.read(dicomFile);
            System.out.println("Transfer Syntax:" + getTagInformation(TagFromName.TransferSyntaxUID));
            System.out.println("SOP Class:" + getTagInformation(TagFromName.SOPClassUID));
            System.out.println("Modality:" + getTagInformation(TagFromName.Modality));
            System.out.println("Samples Per Pixel:" + getTagInformation(TagFromName.SamplesPerPixel));
            System.out.println("Photo Int:" + getTagInformation(TagFromName.PhotometricInterpretation));
            System.out.println("Pixel Spacing:" + getTagInformation(TagFromName.PixelSpacing));
            System.out.println("Bits Allocated:" + getTagInformation(TagFromName.BitsAllocated));
            System.out.println("Bits Stored:" + getTagInformation(TagFromName.BitsStored));
            System.out.println("High Bit:" + getTagInformation(TagFromName.HighBit));

            SourceImage img = new com.pixelmed.display.SourceImage(list);
            System.out.println("Number of frames " + img.getNumberOfFrames());
            System.out.println("Width " + img.getWidth());//all frames will have same width
            System.out.println("Height " + img.getHeight());//all frames will have same height
            System.out.println("Is Grayscale? " + img.isGrayscale());
            System.out.println("Pixel Data present:" + (list.get(TagFromName.PixelData) != null));
            OtherWordAttribute pixelAttribute = (OtherWordAttribute) (list.get(TagFromName.PixelData));

            //get the 16 bit pixel data values
            short[] data = pixelAttribute.getShortValues();
        } catch (Exception e) {
            e.printStackTrace(); //in real life, do something about this exception
        }
    }

    private static void test2(String scJpegFilePath, String newDicomFile) {
        try {
            //generate the DICOM file from the jpeg file and the other attributes supplied
            //note: The PixelMed toolkit encodes some attributes for you automatically as well
            //you may have to explicitly control in your situation
            //the API construct is a bit different in PixelMed for those you are used to invoking a 'command' method
            new ImageToDicom(scJpegFilePath, //path to existing JPEG image
                    newDicomFile, //output DICOM file with full path
                    "Saravanan Subramanian", //name of patient
                    "12121221", //patient id
                    "2323232322", //study id
                    "3232323232", //series number
                    "42423232234"); //instance number
            //now, dump the contents of the DICOM file to the console
            AttributeList list = new AttributeList();
            list.read(newDicomFile);
            System.out.println(list.toString());

        } catch (Exception e) {
            e.printStackTrace(); //in real life, do something about this exception
        }
    }

    private static void test1(String dicomFile) {
        try {
            list.read(dicomFile);
            System.out.println("Study Instance UID:" + getTagInformation(TagFromName.StudyInstanceUID));
            System.out.println("Series Instance UID:" + getTagInformation(TagFromName.SeriesInstanceUID));
            System.out.println("SOP Class UID:" + getTagInformation(TagFromName.SOPClassUID));
            System.out.println("SOP Instance UID:" + getTagInformation(TagFromName.SOPInstanceUID));
            System.out.println("Transfer Syntax UID:" + getTagInformation(TagFromName.TransferSyntaxUID));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getTagInformation(AttributeTag attrTag) {
        return Attribute.getDelimitedStringValuesOrEmptyString(list, attrTag);
    }
}
