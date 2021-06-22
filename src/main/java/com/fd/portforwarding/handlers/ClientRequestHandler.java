package com.fd.portforwarding.handlers;

import com.fd.portforwarding.ChannelUtils;
import com.fd.portforwarding.config.Configuration;
import com.fd.portforwarding.mapping.PortMap;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Handle Request From Client
 */
public class ClientRequestHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(ClientRequestHandler.class);
    private final Configuration config;
    private final PortMap portMap;
    private Channel remoteChannel;

    public ClientRequestHandler(Configuration config, PortMap portMap) {
        this.config = config;
        this.portMap = portMap;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelActive();
        // create connect to remote address
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(ctx.channel().eventLoop())
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, false)
                .option(ChannelOption.AUTO_READ, false)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, config.timeout())
                .option(ChannelOption.SO_REUSEADDR, true);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                ChannelPipeline pipeline = socketChannel.pipeline();
                pipeline.addLast(new IdleStateHandler(0, 0, config.timeout(), TimeUnit.MILLISECONDS));
                pipeline.addLast(new DataTransferHandler(ctx.channel()));
                if (config.openLoggingHandler()) {
                    pipeline.addFirst(Handlers.LOGGING_HANDLER);
                }
            }
        });
        bootstrap.connect(portMap.remoteAddress.host, portMap.remoteAddress.port).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {
                    LOG.debug("connect success to remote address {}", portMap);
                    remoteChannel = channelFuture.channel();
                    tryToReadIfNeeded(ctx);
                } else {
                    LOG.warn("connect fail to remote address {}", portMap, channelFuture.cause());
                    ctx.close();
                }
            }
        });
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ChannelUtils.closeOnFlush(remoteChannel);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // act as a data transfer handler
        if (remoteChannel.isActive()) {
            remoteChannel.writeAndFlush(msg).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if (!channelFuture.isSuccess()) {
                        LOG.warn("write data to remote address occurs error {}", portMap, channelFuture.cause());
                        ChannelUtils.closeOnFlush(remoteChannel);
                        ctx.close();
                    } else {
                        tryToReadIfNeeded(ctx);
                    }
                }
            });
        } else {
            ReferenceCountUtil.release(msg);
            ctx.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOG.warn("request handler occurs error", cause);
        ctx.close();
        ChannelUtils.closeOnFlush(remoteChannel);
    }

    private void tryToReadIfNeeded(ChannelHandlerContext ctx) {
        if (!ctx.channel().config().isAutoRead()) {
            ctx.read();
        }
    }
}
