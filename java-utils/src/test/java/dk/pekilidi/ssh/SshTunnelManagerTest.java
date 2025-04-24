package dk.pekilidi.ssh;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class SshTunnelManagerTest {

    private SshTunnelManager sshTunnelManager;

    @BeforeEach
    void setUp() {
        sshTunnelManager = new SshTunnelManager(new File("src/test/resources/ssh.properties"));
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
        sshTunnelManager.disconnect();
//        assertFalse(sshTunnelManager.connected);
    }
}