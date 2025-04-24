package dk.pekilidi.ssh;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Logger;
import com.jcraft.jsch.Session;
import dk.pekilidi.exceptions.CannotReadPropertiesException;
import dk.pekilidi.properties.PropertyUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;

@Slf4j
@Getter
public class SshTunnelManager {

    private Session session;
    private boolean connected = false;
    private int forwardedPort;
    private final Properties tunnelProperties;

    String sshHost;
    int sshPort;
    String sshUser;
    int localPort;
    String remoteHost;
    int remotePort;

    String sshKeyPath;
    String sshKeyPassphrase;

    boolean debug = false;

    public SshTunnelManager(File propertiesFile) {
        try {
            tunnelProperties = PropertyUtil.loadFromFile(propertiesFile);
            if(tunnelProperties.isEmpty()) {
                throw new CannotReadPropertiesException("Trying to connect to SSH tunnel but it is not configured");
            }
            configure();
        } catch (IOException e) {
            throw new CannotReadPropertiesException(e);
        }

    }

    public void connectAndForwardPort() throws JSchException {
        if (connected) {
            return;
        }
        if(debug){
            JSch.setLogger(customLogger);
        }
        JSch jsch = new JSch();
        if(Path.of("~/.ssh/known_hosts").toFile().exists()){
            jsch.setKnownHosts("~/.ssh/known_hosts");
        }
        session = jsch.getSession(sshUser, sshHost, sshPort);

        // Force acceptance of newer RSA signature algorithms:
        session.setConfig("PubkeyAcceptedKeyTypes", "rsa-sha2-256,rsa-sha2-512,ssh-rsa");
        session.setConfig("PubkeyAcceptedAlgorithms", "rsa-sha2-256,rsa-sha2-512,ssh-rsa");

        // If using private key:
        jsch.addIdentity(sshKeyPath,sshKeyPassphrase);

        // Recommended: properly manage known_hosts, host key checking, etc.
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        config.put("PreferredAuthentications", "publickey");
        session.setConfig(config);

        session.connect();

        forwardedPort = session.setPortForwardingL(localPort, remoteHost, remotePort);
        connected = true;
    }

    public void disconnect() {
        if (session != null && session.isConnected()) {
            session.disconnect();
        }
        connected = false;
    }

    private void configure() {
        sshHost = tunnelProperties.getProperty("ssh.host");
        sshPort = Integer.parseInt(tunnelProperties.getProperty("ssh.port"));
        sshUser = tunnelProperties.getProperty("ssh.user");
        localPort = Integer.parseInt(tunnelProperties.getProperty("ssh.local.port"));
        remoteHost = tunnelProperties.getProperty("ssh.remote.host");
        remotePort = Integer.parseInt(tunnelProperties.getProperty("ssh.remote.port"));
        sshKeyPath = tunnelProperties.getProperty("ssh.private.key.path");
        sshKeyPassphrase = tunnelProperties.getProperty("ssh.private.key.passphrase");
        debug = Boolean.parseBoolean(tunnelProperties.getProperty("debug", "false"));
    }

    Logger customLogger = new Logger() {
        @Override
        public boolean isEnabled(int level) {
            return true;
        }

        @Override
        public void log(int level, String message) {
            // Print or pipe to your logging framework
            System.out.println("JSch log (level " + level + "): " + message);
        }
    };
}
