package dk.pekilidi.ssh;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Logger;
import com.jcraft.jsch.Session;
import dk.pekilidi.exceptions.CannotReadPropertiesException;
import dk.pekilidi.exceptions.ConfigurationErrorException;
import dk.pekilidi.jasypt.Secrets;
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
        jsch.addIdentity(sshKeyPath, sshKeyPassphrase);

        // Recommended: properly manage known_hosts, host key checking, etc.
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        config.put("PreferredAuthentications", "publickey");
        session.setConfig(config);

		log.info("SSH tunnel: connecting {}@{}:{}", sshUser, sshHost, sshPort);
		session.connect();
		log.info("SSH tunnel: connected");

        forwardedPort = session.setPortForwardingL(localPort, remoteHost, remotePort);
		log.info("SSH tunnel: local {} -> {}:{} (forwardedPort={})",
				localPort, remoteHost, remotePort, forwardedPort);
        connected = true;
    }

    public void disconnect() {
        if (session != null && session.isConnected()) {
            session.disconnect();
        }
        connected = false;
    }

	private void configure() {
		sshHost = requireProperty("ssh.host");
		sshPort = requireIntProperty("ssh.port");
		sshUser = requireProperty("ssh.user");
		localPort = requireIntProperty("ssh.local.port");
		remoteHost = requireProperty("ssh.remote.host");
		remotePort = requireIntProperty("ssh.remote.port");

		sshKeyPath = requireProperty("ssh.private.key.path");

		sshKeyPassphrase = requireProperty("ssh.private.key.passphrase");
		if (!sshKeyPassphrase.startsWith("ENC(")) {
			throw new ConfigurationErrorException("Property `ssh.private.key.passphrase` must be encrypted with Jasypt (ENC\\(\\) format)");
		}
		sshKeyPassphrase = Secrets.decrypt(sshKeyPassphrase.replaceFirst("^ENC\\((.*)\\)$", "$1"));

		debug = Boolean.parseBoolean(tunnelProperties.getProperty("debug", "false"));
	}

	private String requireProperty(String key) {
		String value = tunnelProperties.getProperty(key);
		if (value == null || value.isBlank()) {
			throw new CannotReadPropertiesException("Missing or empty required property `" + key + "`");
		}
		return value;
	}

	private int requireIntProperty(String key) {
		String value = requireProperty(key);
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			throw new ConfigurationErrorException("Invalid integer for property `" + key + "`: " + value, e);
		}
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
