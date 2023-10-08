package cn.holelin.dicom.pacs_v1.utils;

import org.dcm4che3.data.UID;

/**
 * @author HoleLin
 */
public class TransferSyntaxHelper {
    public static String[] VERIFICATION = {
            UID.ImplicitVRLittleEndian,
            UID.ExplicitVRLittleEndian,
            UID.ExplicitVRBigEndian
    };

    /**
     * 图像存储 需要的传输语法
     */
    public static String[] IMAGE_STORAGE_ALL_TS = {
            UID.ImplicitVRLittleEndian,
            UID.ExplicitVRLittleEndian,
//            UID.ExplicitVRBigEndian,
//            UID.DeflatedExplicitVRLittleEndian,

            UID.JPEG2000,
            UID.JPEG2000Lossless,

            UID.JPEGExtended12Bit,
            UID.JPEGBaseline8Bit,
            UID.JPEGLosslessSV1,
            UID.JPEGLSNearLossless,
            UID.JPEGLSLossless,
            UID.JPEGSpectralSelectionNonHierarchical79,

            UID.RLELossless,
            UID.MPEG2MPML,
            UID.MPEG2MPHL,
            UID.MPEG4HP41,
            UID.MPEG4HP41BD,
            UID.MPEG4HP422D,
            UID.MPEG4HP423D,
            UID.MPEG4HP42STEREO,
            UID.HEVCMP51,
            UID.HEVCM10P51
    };

}
