package cn.holelin;

import com.pixelmed.dicom.*;
import com.pixelmed.network.FindSOPClassSCU;
import com.pixelmed.network.IdentifierHandler;

public class DicomCFindFunctionalityDemo {

    public static void main(String arg[]) {

        try {
            // use the default character set for VR encoding - override this as necessary
            SpecificCharacterSet specificCharacterSet = new SpecificCharacterSet((String[]) null);
            AttributeList identifier = new AttributeList();

            //build the attributes that you would like to retrieve as well as passing in any search criteria
            identifier.putNewAttribute(TagFromName.QueryRetrieveLevel).addValue("SERIES"); //specific query root
            identifier.putNewAttribute(TagFromName.PatientName, specificCharacterSet);
            identifier.putNewAttribute(TagFromName.PatientID, specificCharacterSet);
            identifier.putNewAttribute(TagFromName.PatientBirthDate);
            identifier.putNewAttribute(TagFromName.PatientSex);
            identifier.putNewAttribute(TagFromName.StudyInstanceUID).addValue("1.3.46.670589.33.1.63670626341464636200001.4789286977609378411");
            identifier.putNewAttribute(TagFromName.SOPInstanceUID);
            identifier.putNewAttribute(TagFromName.StudyDescription);
            identifier.putNewAttribute(TagFromName.StudyDate);
            identifier.putNewAttribute(TagFromName.SliceThickness);
            identifier.putNewAttribute(TagFromName.SeriesInstanceUID);

            //retrieve all studies belonging to patient with name 'Bowen'
            new FindSOPClassSCU("192.168.11.216",
                    11112,
                    "DCM4CHEE",
                    "OURCLIENT",
                    SOPClass.StudyRootQueryRetrieveInformationModelFind, identifier,
                    new OurCustomFindIdentifierHandler());

        } catch (Exception e) {
            e.printStackTrace(System.err); // in real life, do something about this exception
            System.exit(0);
        }
    }
    static class OurCustomFindIdentifierHandler extends IdentifierHandler {

        //add additional constructors here as necessary to pass more information handling

        @Override
        public void doSomethingWithIdentifier(AttributeList attributeListForFindResult) throws DicomException {
            System.out.println("Matched result:" + attributeListForFindResult);

            String studyInstanceUID = attributeListForFindResult.get(TagFromName.StudyInstanceUID).getSingleStringValueOrEmptyString();
            System.out.println("studyInstanceUID:" + studyInstanceUID);

            //do other things you need to do with the matched results
            String sliceThickness = attributeListForFindResult.get(TagFromName.SliceThickness).getSingleStringValueOrNull();

            System.out.println("Slice Thickness: " + sliceThickness);
        }

    }


}