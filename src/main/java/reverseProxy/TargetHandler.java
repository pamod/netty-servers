package reverseProxy;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpObject;

/**
 * some code by shaf on 6/25/17.
 */
public class TargetHandler extends SimpleChannelInboundHandler<HttpObject> {
    ContentQueue contentQueue;
    Channel sourceChannel;

    public TargetHandler(ContentQueue contentQueue, Channel channel) {
        this.contentQueue = contentQueue;
        this.sourceChannel = channel;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, HttpObject msg) throws Exception {
        sourceChannel.writeAndFlush(msg);
    }
}
