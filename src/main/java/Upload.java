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
                System.getenv("SFTP_HOST"),
                System.getenv("SFTP_USER"),
                System.getenv("SFTP_PASSWORD"),
                Integer.parseInt(System.getenv("SFTP_PORT"))
        );
        if (!sftp.connect()) {
            System.out.println("Failed to connect to SFTP server");
            return;
        }
        OutputStream sftpOutputStream = sftp.getFileOutputStream(zipFile);
        ZipOutputStream zipOut = new ZipOutputStream(sftpOutputStream);
        File directory = uploadDir.toFile();
        if (directory.exists() && directory.isDirectory()) {
            addFilesToZip(directory, zipOut);
        }
        zipOut.close();
        sftp.close();
    }

    private static void addFilesToZip(File directory, ZipOutputStream zipOut) throws IOException {
        File[] files = directory.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                addFilesToZip(file, zipOut);
            } else {
                FileInputStream fileIn = new FileInputStream(file);
                ZipEntry zipEntry = new ZipEntry(file.getName());
                zipOut.putNextEntry(zipEntry);

                byte[] buffer = new byte[4096];
                int length;
                while ((length = fileIn.read(buffer)) >= 0) {
                    zipOut.write(buffer, 0, length);
                }

                fileIn.close();
                zipOut.closeEntry();
            }
        }
    }
}
