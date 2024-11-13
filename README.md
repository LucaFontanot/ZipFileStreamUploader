# ZipFileStreamUploader
ZipFileStreamUploader is a simple Java application that uploads a file to an SFTP server. The file is zipped and uploaded in chunks to the server. This is useful for uploading large files to an SFTP server with limited memory.

The application does not store any temporary files, and memory usage is kept to a minimum.

## How to setup
Download ZipFileStreamUploader.jar

Create a .env file in the same directory as the jar file.
```
SFTP_HOST=your-sftp-host
SFTP_USER=your-sftp-user
SFTP_PASSWORD=your-sftp-password
SFTP_PORT=your-sftp-port
```

## How to use to upload a directory
```bash
java -jar ZipFileStreamUploader.jar upload ZipFileName.zip /path/to/compress
```

## How to use to download a directory
```bash
java -jar ZipFileStreamUploader.jar download ZipFileName.zip /path/to/download
```

If you only want to download a specific directory inside the zip file, you can specify the directory name as the third argument.
```bash
java -jar ZipFileStreamUploader.jar download ZipFileName.zip /path/to/download path/to/directory
```