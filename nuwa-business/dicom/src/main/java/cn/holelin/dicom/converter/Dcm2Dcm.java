package cn.holelin.dicom.converter;

import org.dcm4che3.data.UID;
import org.dcm4che3.imageio.codec.Transcoder;
import org.dcm4che3.util.Property;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * @author HoleLin
 */
public class Dcm2Dcm {

    /**
     * 进行JPEG 2000有损压缩
     *
     * @param src  原始文件
     * @param dest 压缩后的文件
     * @throws IOException
     */
    public void j2ki(File src, final File dest) throws IOException {
        transcodeWithTranscoder(src, dest, UID.JPEG2000);

    }

    /**
     * 进行JPEG LS有损压缩
     *
     * @param src  原始文件
     * @param dest 压缩后的文件
     * @throws IOException
     */
    public void jlsn(File src, final File dest) throws IOException {
        transcodeWithTranscoder(src, dest, UID.JPEGLSNearLossless);

    }

    /**
     * 进行JPEG有损压缩
     *
     * @param src  原始文件
     * @param dest 压缩后的文件
     * @throws IOException
     */
    public void jpeg(File src, final File dest) throws IOException {
        transcodeWithTranscoder(src, dest, UID.JPEGBaseline8Bit);

    }


    /**
     * 进行JPEG 2000无损压缩
     *
     * @param src  原始文件
     * @param dest 压缩后的文件
     * @throws IOException
     */
    public void j2kr(File src, final File dest) throws IOException {
        transcodeWithTranscoder(src, dest, UID.JPEG2000Lossless);

    }

    /**
     * 进行JPEG LS无损压缩
     *
     * @param src  原始文件
     * @param dest 压缩后的文件
     * @throws IOException
     */
    public void jlsl(File src, final File dest) throws IOException {
        transcodeWithTranscoder(src, dest, UID.JPEGLSLossless);

    }


    /**
     * 进行JPEG无损压缩
     *
     * @param src  原始文件
     * @param dest 压缩后的文件
     * @throws IOException
     */
    public void jpll(File src, final File dest) throws IOException {
        transcodeWithTranscoder(src, dest, UID.JPEGLosslessSV1);
    }


    /**
     * @param src   原始文件
     * @param dest  压缩后的文件
     * @param tsuid
     * @throws IOException
     */
    public void transcodeWithTranscoder(File src, final File dest, String tsuid) throws IOException {
        try (Transcoder transcoder = new Transcoder(src)) {
            transcoder.setDestinationTransferSyntax(tsuid);
            transcoder.setIncludeFileMetaInformation(true);
            transcoder.setRetainFileMetaInformation(false);
            transcoder.transcode((transcoder1, dataset) -> new FileOutputStream(dest));
        } catch (Exception e) {
            Files.deleteIfExists(dest.toPath());
            throw e;
        }
    }

    public static void main(String[] args) throws IOException {
        Dcm2Dcm dcm2Dcm = new Dcm2Dcm();
//        String src = "C:\\Users\\YW\\Desktop\\23200905data\\1148625395460038656\\ad225da2-617d-4cc0-8773-183ad140962a";
//        String j2ki = "C:\\Users\\YW\\Desktop\\23200905data\\1\\j2ki.dcm";
//        String j2kr = "C:\\Users\\YW\\Desktop\\23200905data\\1\\j2kr.dcm";
//        String jlsl = "C:\\Users\\YW\\Desktop\\23200905data\\1\\jlsl.dcm";
//        String jlsn = "C:\\Users\\YW\\Desktop\\23200905data\\1\\jlsn.dcm";
//        String jpeg = "C:\\Users\\YW\\Desktop\\23200905data\\1\\jpeg.dcm";
//        String jpll = "C:\\Users\\YW\\Desktop\\23200905data\\1\\jpll.dcm";
//        dcm2Dcm.j2ki(Paths.get(src).toFile(), Paths.get(j2ki).toFile());
//        dcm2Dcm.j2kr(Paths.get(src).toFile(), Paths.get(j2kr).toFile());
//        dcm2Dcm.jlsl(Paths.get(src).toFile(), Paths.get(jlsl).toFile());
//        dcm2Dcm.jlsn(Paths.get(src).toFile(), Paths.get(jlsn).toFile());
//        dcm2Dcm.jpeg(Paths.get(src).toFile(), Paths.get(jpeg).toFile());
//        dcm2Dcm.jpll(Paths.get(src).toFile(), Paths.get(jpll).toFile());

        String srcDir = "C:\\Users\\YW\\Desktop\\23200905data";
        String destDir = "G:\\data\\j2ki";
        Files.walkFileTree(Paths.get(srcDir), new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes attrs)
                    throws IOException {
                File file = path.toFile();
                String name = file.getName();
                String parent = file.getParent().replace(srcDir, destDir);

                Path destPath = Paths.get(parent + File.separator + "converter" + File.separator + name + ".dcm");
                if (!destPath.toFile().exists()) {
                    Files.createDirectories(destPath.getParent());
                    Files.createFile(destPath);
                }
                dcm2Dcm.j2ki(path.toFile(), destPath.toFile());
                return super.visitFile(path, attrs);
            }
        });
    }
}
