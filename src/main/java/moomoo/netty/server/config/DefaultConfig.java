package moomoo.netty.server.config;

import org.ini4j.Ini;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class DefaultConfig {

    private static final Logger log = LoggerFactory.getLogger(DefaultConfig.class);

    private static final String CONFIG_LOG = "Load [{}] config...(OK)";

    private Ini ini = null;

    // SECTION
    private static final String SECTION_COMMON = "COMMON";
    private static final String SECTION_NETTY= "NETTY";

    // Field
    // COMMON
    private static final String FIELD_COMMON_LOCAL_IP = "LOCAL_IP";
    private static final String  FIELD_COMMON_LOCAL_PORT = "LOCAL_PORT";

    // NETTY
    private static final String FIELD_NETTY_RECV_BUF_SIZE = "RECV_BUF_SIZE";

    //COMMON
    private String commonLocalIp = "";
    private int commonLocalPort = 0;
    // NETTY
    private int nettyRecvBufSize = 0;

    public DefaultConfig(String configPath) {
        File iniFile = new File(configPath);
        if (!iniFile.isFile() || !iniFile.exists()) {
            log.warn("Not found the config path. (path={})", configPath);
            System.exit(1);
            return;
        }

        try {
            this.ini = new Ini(iniFile);

            loadCommonConfig();
            loadNettyConfig();
        } catch (Exception e) {
            log.error("ConfigManager ", e);
        }
    }

    private void loadCommonConfig() {
        this.commonLocalIp = getIniValue(SECTION_COMMON, FIELD_COMMON_LOCAL_IP);
        this.commonLocalPort = Integer.parseInt(getIniValue(SECTION_COMMON, FIELD_COMMON_LOCAL_PORT));
        log.debug(CONFIG_LOG, SECTION_COMMON);
    }

    private void loadNettyConfig() {
        this.nettyRecvBufSize = Integer.parseInt(getIniValue(SECTION_NETTY, FIELD_NETTY_RECV_BUF_SIZE));
        log.debug(CONFIG_LOG, SECTION_NETTY);
    }

    private String getIniValue(String section, String key){
        String value = ini.get(section, key);
        if (value == null) {
            log.error("[{}] \"{}\" is null.", section, key);
            System.exit(1);
            return null;
        }

        value = value.trim();
        log.debug("Get [{}] config [{}] : [{}]", section, key, value);
        return  value;
    }

    //common
    public String getCommonLocalIp() {
        return commonLocalIp;
    }

    public int getCommonLocalPort() {
        return commonLocalPort;
    }

    public int getNettyRecvBufSize() {
        return nettyRecvBufSize;
    }
}
