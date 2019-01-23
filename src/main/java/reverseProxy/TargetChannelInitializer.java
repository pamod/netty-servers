package reverseProxy;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;

public class TargetChannelInitializer extends ChannelInitializer<SocketChannel> {
    ContentQueue contentQueue;
    Channel sourceChannel;

    public TargetChannelInitializer(ContentQueue contentQueue, Channel channel) {
        this.contentQueue = contentQueue;
        this.sourceChannel = channel;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline p = socketChannel.pipeline();
        p.addLast(new HttpClientCodec());
        p.addLast(new TargetHandler(this.contentQueue, sourceChannel));
    }
}
