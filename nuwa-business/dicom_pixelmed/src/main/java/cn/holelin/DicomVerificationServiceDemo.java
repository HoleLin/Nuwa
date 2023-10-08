package cn.holelin;

import com.pixelmed.network.VerificationSOPClassSCU;

/**
 * @author HoleLin
 */
public class DicomVerificationServiceDemo {
    public static void main(String[] args) {
        try {
            //Demonstration of code to verify connectivity against David Clunie's public server hosted on the Amazon cloud

            String remoteEntityHostName = "192.168.11.216"; //their hostname or IP address
            int remoteEntityPort = 11112; //the port their entity is listening on
            String calledAETitle = "DCM4CHEE"; //their Application Entity Title
            String callingAETitle = "LOCAL345"; //our Application Entity Title
            boolean secureTransport = false; //optional - we wont need to use it for our example
            int debugLevel = 2; // zero for no debugging messages, higher values for more verbose messages

            //Call the constructor for this class with the parameters
            //the API construct is a bit different in PixelMed for those you are used to invoking a 'command' method
            VerificationSOPClassSCU echoScu = new VerificationSOPClassSCU(remoteEntityHostName,
                    remoteEntityPort,
                    calledAETitle,
                    callingAETitle,
                    secureTransport,debugLevel);

            //See documentation at: http://www.dclunie.com/pixelmed/software/javadoc/index.html
            //on the class VerificationSOPClassSCU
            //For successful connection, association negotiation and C-ECHO command success status
            //is indicated by the lack of an exception.
        }
        catch (Exception e) {

            //the following exceptions may be thrown if C-ECHO fails:
            //1. java.io.IOException
            //2. DicomException
            //3. DicomNetworkException - if the connection is refused, the association reqeust is reject,
            //or the C-ECHO command reports other than a success status

            e.printStackTrace(); //in real life, do something about this exception
        }
    }
}
