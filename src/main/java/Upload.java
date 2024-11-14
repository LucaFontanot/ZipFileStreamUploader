import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Upload {
    public static void upload(Path uploadDir, String zipFile) throws IOException {
        SFTP sftp = new SFTP(
                ZipFileStreamUploader.dotenv.get("SFTP_HOST"),
                ZipFileStreamUploader.dotenv.get("SFTP_USER"),
                ZipFileStreamUploader.dotenv.get("SFTP_PASSWORD"),
                Integer.parseInt(ZipFileStreamUploader.dotenv.get("SFTP_PORT", "22"))
        );
        if (!sftp.connect()) {
            System.out.println("Failed to connect to SFTP server");
            return;
        }
        System.out.println("Connected to SFTP server");
        OutputStream sftpOutputStream = sftp.getFileOutputStream(zipFile);
        ZipOutputStream zipOut = new ZipOutputStream(sftpOutputStream);
        File directory = uploadDir.toFile();
        if (directory.exists() && directory.isDirectory()) {
            addFilesToZip(directory, zipOut);
        }
        zipOut.close();
        sftp.close();
    }

    //static Pattern filePattern = Pattern.compile("^[a-zA-Z0-9_\\-\\.]+$");

    private static void addFilesToZip(File directory, ZipOutputStream zipOut) {
        File[] files = directory.listFiles();
        if (files == null) return;
        for (File file : files) {
            if (file.isDirectory()) {
                addFilesToZip(file, zipOut);
            } else {
                try {
                /*if (!filePattern.matcher(file.getName()).matches()) {
                    System.out.println("Skipping file: " + file.getPath());
                    continue;
                }*/
                    System.out.println("Adding file: " + file.getPath());
                    FileInputStream fileIn = new FileInputStream(file);
                    try {
                        ZipEntry zipEntry = new ZipEntry(file.getPath());

                        zipOut.putNextEntry(zipEntry);
                        try {
                            byte[] buffer = new byte[65536];
                            int length;
                            while ((length = fileIn.read(buffer)) >= 0) {
                                zipOut.write(buffer, 0, length);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        fileIn.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    zipOut.closeEntry();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
