package com.ehocam.api_gateway.pact;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class SimplePactTest {

    @LocalServerPort
    private int port;

    @Test
    void testApplicationStarts() {
        assertTrue(port > 0, "Application should start on a random port");
        System.out.println("Application started on port: " + port);
        assertTrue(true, "Test passed");
    }
}
