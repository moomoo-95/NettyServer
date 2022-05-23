package moomoo.netty.server;

import moomoo.netty.server.service.ServiceManager;

public class NettyServerMain {
    public static void main(String[] args) {
        AppInstance.getInstance().setInstance(args[0]);

        ServiceManager serviceManager = ServiceManager.getInstance();
        serviceManager.loop();
    }
}
