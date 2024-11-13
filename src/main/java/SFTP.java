import com.jcraft.jsch.*;

import java.io.OutputStream;

public class SFTP {
    private final String host;
    private final String user;
    private final String password;
    private final int port;

    public SFTP(String host, String user, String password, int port) {
        this.host = host;
        this.user = user;
        this.password = password;
        this.port = port;
    }

    JSch jsch = new JSch();
    Session session = null;
    Channel channel = null;
    ChannelSftp sftpChannel = null;


    public boolean connect(){
        try {
            session = jsch.getSession(user, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            channel = session.openChannel("sftp");
            channel.connect();
            sftpChannel = (ChannelSftp) channel;
            return true;
        } catch (JSchException e) {
            return false;
        }
    }

    public OutputStream getFileOutputStream(String path) {
        try {
            return sftpChannel.put(path, ChannelSftp.OVERWRITE);
        } catch (SftpException e) {
            return null;
        }
    }

    public void close(){
        sftpChannel.exit();
        channel.disconnect();
        session.disconnect();
    }
}
