package cn.holelin;

import com.pixelmed.dicom.SOPClass;
import com.pixelmed.dicom.TransferSyntax;
import com.pixelmed.network.Association;
import com.pixelmed.network.AssociationFactory;
import com.pixelmed.network.PresentationContext;

import java.util.LinkedList;

/**
 * @author HoleLin
 */
public class AssociationsInDicomDemo {
    public static void main(String[] args) {

        try {
            LinkedList<PresentationContext> prestnContexts = new LinkedList<PresentationContext>();
            LinkedList<String> transferSyntaxList = new LinkedList<String>();
            transferSyntaxList.add(TransferSyntax.Default);
            transferSyntaxList.add(TransferSyntax.ExplicitVRBigEndian);

            byte prentnContextIdOfVerfSopClass = 1;//use any number here for tracking
            String verificationSopClass = SOPClass.Verification; //this is the UID for the Verification SOP class

            //Print it to console so you can see its UID
            System.out.println("The UID of the SOP class that we are using as Abstract Syntax is " + verificationSopClass);

            //Make a list of presentation contexts consisting of the Abstract syntax and the list of transfer syntaxes
            //We will ask the Called AE to see whether it supports these
            prestnContexts.add(new PresentationContext(prentnContextIdOfVerfSopClass, verificationSopClass,transferSyntaxList));

            System.out.println("Attempting association establishment with remote peer...");

            //Attempt to create the association to Medical Connections public DICOM server
            Association association = AssociationFactory.createNewAssociation("192.168.11.216", //their hostname or IP address
                    11112, //the port their entity is listening on
                    "DCM4CHEE", //their Application Entity Title
                    "OurJavaClient", //our Application Entity Title
                    prestnContexts,
                    null,
                    false);

            //print the details of the association established to the console
            System.out.println(association);

            //Check to see if the presentation context is supported by the Called AE
            byte supportedContextId = association.getSuitablePresentationContextID(verificationSopClass);
            System.out.println("The Verification SOP class is supported");
            //Check to see what transfer syntax is preferred by the Called AE
            String transferSyntaxSupported = association.getTransferSyntaxForPresentationContextID(supportedContextId);
            //You should see Explicit VR Big-endian UID - 1.2.840.10008.1.2.2 returned here.
            //This is because an Explicit VR transfer syntax is always be preferable over Implicit (or the "Default") transfer syntax
            System.out.println("The transfer syntax supported for this presentation context is " + transferSyntaxSupported);

        } catch (Exception e) {
            //In real-life, do something about these exceptions
            e.printStackTrace();
        }
    }
}
