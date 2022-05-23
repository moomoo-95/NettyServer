package moomoo.netty.server.channel.module;

import handler.TcpChannelHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import moomoo.netty.server.AppInstance;
import moomoo.netty.server.config.DefaultConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @class public class TcpNettyChannel
 * @brief 서버가 수신할 포트 바인딩, 연결 요청 수락, Channel 구성을 담당하는 매니저 클래스
 */
public class TcpServerChannel {

    private static final Logger logger = LoggerFactory.getLogger(TcpServerChannel.class);

    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();
    private ServerBootstrap b;

    private Channel serverChannel;

    private final String channelId;
    private final String listenIp;
    private final int listenPort;

    public TcpServerChannel(String channelId, String listenIp, int listenPort) {
        this.channelId = channelId;
        this.listenIp = listenIp;
        this.listenPort = listenPort;
    }

    /**
     * @fn public void start()
     * @brief Manager를 실행시키는 메서
     */
    public void start() {
        DefaultConfig config = AppInstance.getInstance().getDefaultConfig();

        // 새 연결 수락 및 데이터 읽기/쓰기와 같은 이벤트 처리 수행
        bossGroup = new NioEventLoopGroup();
        // 서버를 부트스트랩하고 바인딩하는데 이용
        b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_RCVBUF, config.getNettyRecvBufSize())
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) {
                        socketChannel.pipeline().addLast(new TcpChannelHandler(channelId, listenIp, listenPort));
                    }
                });
    }

    public void stop () {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    public Channel openChannel () {
        if (serverChannel != null) {
            logger.warn("({}) ({}:{}) Channel is already opened.", channelId, listenIp, listenPort);
            return null;
        }

        InetAddress address;
        ChannelFuture channelFuture;

        try {
            address = InetAddress.getByName(listenIp);
        } catch (UnknownHostException e) {
            logger.warn("UnknownHostException is occurred. (ip={})", listenIp, e);
            return null;
        }

        try {
            channelFuture = b.bind(address, listenPort).sync();
            serverChannel = channelFuture.channel();
            logger.debug("({}) ({}:{}) Channel is opened.", channelId, listenIp, listenPort);

            return serverChannel;
        } catch (Exception e) {
            logger.error("({}) ({}:{}) Channel is interrupted. ", channelId, listenIp, listenPort, e);
            Thread.currentThread().interrupt();
            return null;
        }
    }

    public void closeChannel() {
        if (serverChannel == null) {
            logger.warn("Channel is already closed.");
            return;
        }

        serverChannel.close();
        serverChannel = null;
        logger.debug("Channel is closed.");
    }
}
