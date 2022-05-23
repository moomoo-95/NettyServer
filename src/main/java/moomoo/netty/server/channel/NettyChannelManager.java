package moomoo.netty.server.channel;

import moomoo.netty.server.channel.module.TcpServerChannel;

public class NettyChannelManager {

    private TcpServerChannel tcpServerChannel = null;

    private static class SingleTon {
        public static final NettyChannelManager INSTANCE = new NettyChannelManager();
    }

    public NettyChannelManager() {
        //
    }

    public static NettyChannelManager getInstance() {
        return SingleTon.INSTANCE;
    }

    public void startTcpServerChannel(String ip, int port) {
        tcpServerChannel = new TcpServerChannel("TCP-SERVER-CHANNEL-01", ip, port);
        tcpServerChannel.start();
        tcpServerChannel.openChannel();
    }

    public void stopTcpServerChannel() {
        tcpServerChannel.closeChannel();
        tcpServerChannel.stop();
    }
}
