import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Download {
    public static void download(String zip, Path outputDir, String zipEntry) {
        SFTP sftp = new SFTP(
                ZipFileStreamUploader.dotenv.get("SFTP_HOST"),
                ZipFileStreamUploader.dotenv.get("SFTP_USER"),
                ZipFileStreamUploader.dotenv.get("SFTP_PASSWORD"),
                Integer.parseInt(ZipFileStreamUploader.dotenv.get("SFTP_PORT","22"))
        );
        if (!sftp.connect()) {
            System.out.println("Failed to connect to SFTP server");
            return;
        }
        System.out.println("Connected to SFTP server");
        InputStream sftpInputStream = sftp.getFileInputStream(zip);
        if (sftpInputStream == null) {
            System.out.println("Failed to get input stream from SFTP server");
            return;
        }
        try (ZipInputStream zipIn = new ZipInputStream(sftpInputStream)) {
            ZipEntry entry;
            while ((entry = zipIn.getNextEntry()) != null) {
                Path filePath = outputDir.resolve(entry.getName());
                if (!entry.getName().startsWith(zipEntry)) {
                    System.out.println("Skipping: " + entry.getName());
                    continue;
                }
                System.out.println("Downloading: " + entry.getName());
                if (entry.isDirectory()) {
                    Files.createDirectories(filePath);
                } else {
                    Files.createDirectories(filePath.getParent());
                    try (FileOutputStream fileOut = new FileOutputStream(filePath.toFile())) {
                        byte[] buffer = new byte[4096];
                        int length;
                        while ((length = zipIn.read(buffer)) > 0) {
                            fileOut.write(buffer, 0, length);
                        }
                    }
                }
                zipIn.closeEntry();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            sftp.close();
        }
    }

}
