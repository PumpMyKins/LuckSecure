package fr.pmk.lucksecure.velocity;

import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;

public class Main {
    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;

    @Inject
    public Main(ProxyServer server, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = Logger.getLogger("lucksecure");
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        getLogger().info("enabling.");
        try {
            new VelocityLuckSecure(this).init();
        } catch (Exception e) {
            this.getLogger().log(Level.SEVERE, "LuckSecure init error", e);
        }
    }

    public Logger getLogger() {
        return logger;
    }

    public ProxyServer getServer() {
        return server;
    }

    public Path getDataDirectory() {
        return dataDirectory;
    }
}
