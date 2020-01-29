package com.payneteasy.swagger.apt.demo;

import org.eclipse.jetty.plus.webapp.EnvConfiguration;
import org.eclipse.jetty.plus.webapp.PlusConfiguration;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.JettyWebXmlConfiguration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebInfConfiguration;
import org.eclipse.jetty.webapp.WebXmlConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.security.ProtectionDomain;

/**
 * @author dvponomarev, 26.12.2019
 */
public class JettyStart {

    private static final Logger LOG = LoggerFactory.getLogger(JettyStart.class);

    public static void main(String[] args) {
        final Server          server    = new Server();
        final ServerConnector connector = new ServerConnector(server);
        connector.setPort(8080);
        server.setConnectors(new Connector[]{connector});

        final WebAppContext webAppContext = new WebAppContext();
        webAppContext.setServer(server);
        webAppContext.setContextPath("/demo");

        final ProtectionDomain protectionDomain = JettyStart.class.getProtectionDomain();
        final URL              location         = protectionDomain.getCodeSource().getLocation();
        webAppContext.setWar(location.toExternalForm());

        webAppContext.getServletContext().getContextHandler().setCompactPath(true);
        final EnvConfiguration envConfiguration = new EnvConfiguration();
        webAppContext.setConfigurations(new Configuration[]{
                new WebInfConfiguration(),
                new WebXmlConfiguration(),
                envConfiguration,
                new PlusConfiguration(),
                new JettyWebXmlConfiguration()
        });

        server.setHandler(webAppContext);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                server.stop();
                server.join();
            } catch (Exception e) {
                LOG.error("Cannot stop server", e);
            }
        }));

        try {
            LOG.info(">>> STARTING EMBEDDED JETTY SERVER, PRESS ANY KEY TO STOP");
            server.start();

            while (System.in.available() == 0) {
                Thread.sleep(5000);
            }
            server.stop();
            server.join();
        } catch (Exception e) {
            LOG.info("Cannot start server", e);
            System.exit(100);
        }
    }

}
