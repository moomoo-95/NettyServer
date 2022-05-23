package handler;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * tcp channel default handler
 */
// 여러 채널 간 안전하게 공유할 수 있음을 나타냄
@Sharable
public class TcpChannelHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(TcpChannelHandler.class);

    private final String name;
    private final String listenIp;
    private final int listenPort;

    public TcpChannelHandler(String name, String listenIp, int listenPort) {
        this.name = name;
        this.listenIp = listenIp;
        this.listenPort = listenPort;
    }

    /**
     * @fn public void channelRead
     * @brief 메시지가 들어올 때마다 호출되는 메서드
     * @param context 채널
     * @param message 인바운드된 메시지
     */
    @Override
    public void channelRead(ChannelHandlerContext context, Object message){
        String inboundMessage = ((ByteBuf) message).toString(CharsetUtil.UTF_8);

        logger.debug("({}) ({}:{}) TcpChannelHandler received : {}", name, listenIp, listenPort, inboundMessage);
        sendResponse(context, name + listenIp + listenPort + " ok");
    }

    /**
     * @fn public void channelReadComplete
     * @brief channelRead 의 마지막 호출에서 현재 일괄 처리의 마지막 메시지를 처리했음을 핸들러에게 통보하는 메서드
     * @param context 채널
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext context) {
        context.flush();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        logger.warn("({}) ({}:{}) TcpChannelHandler is inactive.", name, listenIp, listenPort);
    }

    /**
     * @fn public void exceptionCaught
     * @brief 읽기 작업 중 예외사항 발생시 예외스택을 출력하고 채널을 닫는 메서드
     * @param context 채널
     * @param cause 예외사항
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
        logger.error("({}) ({}:{}) TcpChannelHandler.exceptionCaught.Exception", name, listenIp, listenPort, cause);
        context.close();
    }

    private void sendResponse(ChannelHandlerContext ctx, String message) {
        ctx.write(message);
    }
}
