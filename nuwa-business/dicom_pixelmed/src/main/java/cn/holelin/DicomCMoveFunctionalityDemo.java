package cn.holelin;

import java.io.File;
    import java.io.IOException;

    import com.pixelmed.dicom.Attribute;
    import com.pixelmed.dicom.AttributeList;
    import com.pixelmed.dicom.AttributeTag;
    import com.pixelmed.dicom.CodeStringAttribute;
    import com.pixelmed.dicom.DicomException;
    import com.pixelmed.dicom.SOPClass;
    import com.pixelmed.dicom.SpecificCharacterSet;
    import com.pixelmed.dicom.StoredFilePathStrategy;
    import com.pixelmed.dicom.TagFromName;
    import com.pixelmed.dicom.UniqueIdentifierAttribute;
    import com.pixelmed.network.DicomNetworkException;
    import com.pixelmed.network.FindSOPClassSCU;
    import com.pixelmed.network.IdentifierHandler;
    import com.pixelmed.network.MoveSOPClassSCU;
    import com.pixelmed.network.ReceivedObjectHandler;
    import com.pixelmed.network.StorageSOPClassSCPDispatcher;

    public class DicomCMoveFunctionalityDemo {
        
        public static void main(String arg[]) {
            
            //Summary of what we are doing here:
            
            //1. Start a C-STORE SCP server to be able listen for data pushed to us
            //2. Perform a C-FIND operation for all studies matching a specific patient
            //3. For each study found, retrieve all DICOM objects belonging to the study using a C-MOVE request
            //3. As each file is received, write the information about the incoming data to the console
            
            try {
                
                String storeScpAeTitle = "YW_PACS_LOCAL";
                int storeScpPortNumber = 10087;
                File pathToStoreIncomingDicomFiles = new File("C:\\Data\\pacs\\pixelmed");
                
                //start a DICOM Store SCP Server to listen for data that will be pushed to us
                Thread thread = new Thread(new StorageSOPClassSCPDispatcher(storeScpPortNumber, storeScpAeTitle, pathToStoreIncomingDicomFiles, StoredFilePathStrategy.BYSOPINSTANCEUIDINSINGLEFOLDER, new OurCMoveDemoStoreHandler()));
                thread.start();
                
                // use the default character set for VR encoding - override this as necessary
                SpecificCharacterSet specificCharacterSet = new SpecificCharacterSet((String[])null);
                AttributeList identifier = new AttributeList();
                
                //build the attributes that you would like to retrieve as well as passing in any search criteria
                identifier.putNewAttribute(TagFromName.QueryRetrieveLevel).addValue("STUDY"); //specific query root
                identifier.putNewAttribute(TagFromName.PatientName,specificCharacterSet).addValue("HE*");
                identifier.putNewAttribute(TagFromName.PatientID,specificCharacterSet);
                identifier.putNewAttribute(TagFromName.PatientBirthDate);
                identifier.putNewAttribute(TagFromName.PatientSex);
                identifier.putNewAttribute(TagFromName.StudyInstanceUID);
                identifier.putNewAttribute(TagFromName.SOPInstanceUID);
                identifier.putNewAttribute(TagFromName.StudyDescription);
                identifier.putNewAttribute(TagFromName.StudyDate);
                
                //retrieve all studies belonging to patient with name 'Bowen'
                new FindSOPClassSCU("192.168.11.216",
                        11112,
                        "DCM4CHEE",
                        "OurFindScu",
                        SOPClass.StudyRootQueryRetrieveInformationModelFind,
                        identifier,
                        new OurCMoveFindHandler());
                
            }
            catch (Exception e) {
                e.printStackTrace(System.err); // in real life, do something about this exception
                System.exit(0);
            }
        }

    }

    class OurCMoveFindHandler extends IdentifierHandler {
        
        private static String moveSCPAddress = "192.168.11.216";
        private static String moveScpAeTitle = "DCM4CHEE";
        private static int moveScpPortNumber = 11112;
        private static String moveScuAeTitle = "YW_PACS_LOCAL";
        private static String storeScpAeTitle = "YW_PACS_LOCAL";
        
        @Override
        public void doSomethingWithIdentifier(AttributeList attributeListForFindResult) throws DicomException {
            System.out.println("Matched result:" + attributeListForFindResult);
            
            String studyInstanceUID = attributeListForFindResult.get(TagFromName.StudyInstanceUID).getSingleStringValueOrEmptyString();
            
            try {
                AttributeList identifier = new AttributeList();
                { AttributeTag tag = TagFromName.QueryRetrieveLevel; Attribute attribute = new CodeStringAttribute(tag); attribute.addValue("STUDY"); identifier.put(tag,attribute); }
                { AttributeTag tag = TagFromName.StudyInstanceUID; Attribute attribute = new UniqueIdentifierAttribute(tag); attribute.addValue(studyInstanceUID); identifier.put(tag,attribute); }
                
                
                new MoveSOPClassSCU(moveSCPAddress, moveScpPortNumber,moveScpAeTitle,moveScuAeTitle,storeScpAeTitle,SOPClass.StudyRootQueryRetrieveInformationModelMove,identifier);
            }
            catch (Exception e) {
                System.out.println("Error during move operation" + e); // in real life, do something about this exception
                e.printStackTrace(System.err);
            }
        }
        
    }

    class OurCMoveDemoStoreHandler extends ReceivedObjectHandler {

        @Override
        public void sendReceivedObjectIndication(String filename, String transferSyntax, String calledAetTitle)
                throws DicomNetworkException, DicomException, IOException {
            
            System.out.println("Incoming data from " + calledAetTitle + "...");
            System.out.println("filename:" + filename);
            System.out.println("transferSyntax:" + transferSyntax);
            
        }
        
    }