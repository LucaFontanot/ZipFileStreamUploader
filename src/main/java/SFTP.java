import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.session.SessionHeartbeatController;
import org.apache.sshd.sftp.client.SftpClient;
import org.apache.sshd.sftp.client.SftpClientFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

public class SFTP {
    private final String host;
    private final String user;
    private final String password;
    private final int port;
    private SshClient client;
    private ClientSession session;
    private SftpClient sftpClient;

    public SFTP(String host, String user, String password, int port) {
        this.host = host;
        this.user = user;
        this.password = password;
        this.port = port;
    }

    public boolean connect() {
        try {
            client = SshClient.setUpDefaultClient();
            client.start();
            session = client.connect(user, host, port).verify(10, TimeUnit.SECONDS).getSession();
            session.addPasswordIdentity(password);
            session.auth().verify(10, TimeUnit.SECONDS);
            session.setSessionHeartbeat(SessionHeartbeatController.HeartbeatType.IGNORE, TimeUnit.SECONDS, 30);
            sftpClient = SftpClientFactory.instance().createSftpClient(session);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void reconnect() {
        close();
        connect();
    }

    public OutputStream getFileOutputStream(String path) {
        try {
            return sftpClient.write(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public InputStream getFileInputStream(String path) {
        try {
            return sftpClient.read(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void close() {
        try {
            if (sftpClient != null) {
                sftpClient.close();
            }
            if (session != null) {
                session.close();
            }
            if (client != null) {
                client.stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
