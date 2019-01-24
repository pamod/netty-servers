package reverseProxy;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * some code by shaf on 6/25/17.
 */
public class SourceHandler extends ChannelInboundHandlerAdapter {

    ContentQueue contentQueue;
    ChannelFuture connectFuture;
    Channel targetChannel;
    Bootstrap bootstrap;

    public SourceHandler(ContentQueue contentQueue) {
        this.contentQueue = contentQueue;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        contentQueue.addContent(msg);
        if (connectFuture.isDone()) {
            targetChannel.writeAndFlush(msg);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx)
            throws Exception {
        bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class)
                .handler(new TargetChannelInitializer(this.contentQueue, ctx.channel()));
        bootstrap.group(ctx.channel().eventLoop());
        connectFuture = bootstrap.connect(new InetSocketAddress("localhost", 8688));
        connectFuture.addListener(new SourceHandlerListener(this.contentQueue, this));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        connectFuture.channel().eventLoop().shutdownGracefully();
        bootstrap = null;
//        connectFuture.channel().close();

    }

    public void setTargetChannel(Channel targetChannel) {
        this.targetChannel = targetChannel;
    }
}
