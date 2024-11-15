import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.nio.file.Path;

public class ZipFileStreamUploader {
    public static Dotenv dotenv = Dotenv.load();
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java -jar ZipFileStreamUploader.jar <option> [args]");
            System.out.println("Options:");
            System.out.println("  upload <zipFile> <uploadDir>");
            System.out.println("  download <zipFile> <downloadDir> [zipEntry]");
            return;
        }
        if (args[0].equals("upload")) {
            if (args.length < 3) {
                System.out.println("Usage: java -jar ZipFileStreamUploader.jar upload <zipFile> <uploadDir>");
                return;
            }
            try {
                Upload.upload(Path.of(args[2]), args[1]);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (args[0].equals("download")) {
            if (args.length < 3) {
                System.out.println("Usage: java -jar ZipFileStreamUploader.jar download <zipFile> <downloadDir> [zipEntry]");
                return;
            }
            Download.download(
                    args[1],
                    Path.of(args[2]),
                    args.length > 3 ? args[3] : ""
            );
        }
    }


}
