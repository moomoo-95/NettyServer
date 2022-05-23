package moomoo.netty.server;

import moomoo.netty.server.config.DefaultConfig;

public class AppInstance {

    private static class SingleTon {
        public static final AppInstance INSTANCE = new AppInstance();
    }

    private DefaultConfig defaultConfig;

    public AppInstance() {
        // nothing
    }

    public static AppInstance getInstance() {
        return SingleTon.INSTANCE;
    }

    public void setInstance(String configPath) {
        setDefaultConfig(configPath);
    }

    public DefaultConfig getDefaultConfig() {
        return defaultConfig;
    }

    public void setDefaultConfig(String configPath) {
        this.defaultConfig = new DefaultConfig(configPath);
    }
}
