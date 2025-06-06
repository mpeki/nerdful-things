package dk.pekilidi.ssh;

import dk.pekilidi.jasypt.Secrets;
import org.apache.sshd.common.forward.PortForwardingEventListener;
import org.apache.sshd.common.session.Session;
import org.apache.sshd.common.util.net.SshdSocketAddress;
import org.apache.sshd.server.SshServer;

import org.apache.sshd.server.auth.pubkey.AcceptAllPublickeyAuthenticator;
import org.apache.sshd.server.forward.AcceptAllForwardingFilter;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.junit.jupiter.api.*;
import org.junitpioneer.jupiter.SetEnvironmentVariable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

@SetEnvironmentVariable(key = "JASYPT_ENCRYPTOR_PASSWORD", value = "testMasterPassword")
class SshTunnelManagerTest {

    private static final Path KEY_DIR = Paths.get("src/test/resources/keys");
    private static SshServer sshd;
    private SshTunnelManager sshTunnelManager;

    @BeforeAll
    static void startSshd() throws Exception {
        sshd = SshServer.setUpDefaultServer();
        sshd.setPort(0);                                      // pick a random free port
        sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider());
        sshd.setPublickeyAuthenticator(AcceptAllPublickeyAuthenticator.INSTANCE);
        sshd.setForwardingFilter(AcceptAllForwardingFilter.INSTANCE);

        sshd.addPortForwardingEventListener(new PortForwardingEventListener() {
            @Override
            public void establishedExplicitTunnel(
                    Session session, SshdSocketAddress local, SshdSocketAddress remote, boolean localForward, SshdSocketAddress boundAddress, Throwable reason)
                    throws IOException {
                System.out.println("Tunnel established: " + local + " → " + remote);
            }
        });
        sshd.start();
    }

    @AfterAll
    static void stopSshd() throws Exception {
        sshd.stop();
    }

    @BeforeEach
    void setUp() throws IOException {
        Path props = Files.createTempFile("ssh", ".properties");
        Files.writeString(props, """
                ssh.host=localhost
                ssh.port=%d
                ssh.user=testuser
                ssh.local.port=0
                ssh.remote.host=127.0.0.1
                ssh.remote.port=5432
                ssh.private.key.path=%s
                ssh.private.key.passphrase=ENC(%s)
                debug=false
                """.formatted(sshd.getPort(),
                KEY_DIR.resolve("id_rsa").toAbsolutePath(),
                Secrets.encrypt("sshTest")));

        sshTunnelManager = new SshTunnelManager(props.toFile());
    }


    @AfterEach
    void tearDown() {
        sshTunnelManager.disconnect();
    }

    @Test
    void testConnectAndForwardPort() {
        assertDoesNotThrow(() -> {
            sshTunnelManager.connectAndForwardPort();
        });

        assertTrue(sshTunnelManager.getForwardedPort() > 0);
        assertTrue(sshTunnelManager.isConnected());
    }

    @Test
    void testDisconnect() {
        assertDoesNotThrow(sshTunnelManager::connectAndForwardPort);
        sshTunnelManager.disconnect();
        assertTrue(sshTunnelManager.getForwardedPort() > 0);
    }
}