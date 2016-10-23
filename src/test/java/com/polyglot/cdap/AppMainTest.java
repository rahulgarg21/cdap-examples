package com.polyglot.cdap;

import co.cask.cdap.api.metrics.RuntimeMetrics;
import co.cask.cdap.test.*;
import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Created by Rajiv Singla on 10/21/2016.
 */
@Ignore
public class AppMainTest extends TestBase {

    @ClassRule
    public static final TestConfiguration CONFIG = new TestConfiguration("explore.enabled", false);

    private static class TestConfig extends AppMain.AppConfig {

        public TestConfig() {
            appName = "TestAppName";
            appDescription = "TestAppDescription";
        }

    }

    @Test
    public void testApp() throws Exception {

        ApplicationManager applicationManager = deployApplication(AppMain.class, new TestConfig());

        // Start flow
        FlowManager nameSaverFlow = applicationManager.getFlowManager("nameSaverFlow").start();
        assertTrue(nameSaverFlow.isRunning());

        // Send data to name stream
        StreamManager nameStream = getStreamManager("nameStream");
        for (int i = 1; i <= 10; i++) {
            nameStream.send("name" + i);
        }

        try {
            RuntimeMetrics nameSaverFlowletMetrics = nameSaverFlow.getFlowletMetrics("nameSaverFlowlet");
            nameSaverFlowletMetrics.waitForProcessed(10, 10, TimeUnit.SECONDS);
        } finally {
            nameSaverFlow.stop();
        }
        assertFalse(nameSaverFlow.isRunning());


        // Start Name Service
        ServiceManager serviceManager = applicationManager.getServiceManager("nameService").start();

        // Wait for service startup
        serviceManager.waitForStatus(true);

        URL url = new URL(serviceManager.getServiceURL(), "name");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        assertEquals(HttpURLConnection.HTTP_OK, connection.getResponseCode());

        String response;
        try {
            response = new String(ByteStreams.toByteArray(connection.getInputStream()), Charsets.UTF_8);
        } finally {
            connection.disconnect();
        }

        assertEquals("name10", response);
    }
}