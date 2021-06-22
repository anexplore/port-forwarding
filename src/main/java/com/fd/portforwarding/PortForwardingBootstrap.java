package com.fd.portforwarding;

import com.fd.portforwarding.config.Configuration;
import com.fd.portforwarding.config.SystemPropertyAndEnvConfiguration;
import com.fd.portforwarding.handlers.ClientRequestHandler;
import com.fd.portforwarding.handlers.Handlers;
import com.fd.portforwarding.mapping.MappingHolder;
import com.fd.portforwarding.mapping.PortMap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class PortForwardingBootstrap {
    private static final Logger LOG = LoggerFactory.getLogger(PortForwardingBootstrap.class);
    final Configuration config;
    EventLoopGroup acceptEventLoopGroup;
    EventLoopGroup workEventLoopGroup;
    MappingHolder mappingHolder;


    public PortForwardingBootstrap() {
        config = new SystemPropertyAndEnvConfiguration();
    }

    public void startup() throws Exception {
        // init event loop group
        acceptEventLoopGroup = new NioEventLoopGroup(config.ioAcceptThreadNumber());
        workEventLoopGroup = new NioEventLoopGroup(config.ioWorkThreadNumber());
        // build mapping holder
        mappingHolder = MappingHolder.buildMappingHolderFromFile(config.mappingFilePath());
        // init all server bootstraps
        for (Map.Entry<String, PortMap> entry : mappingHolder.entrySet()) {
            PortMap portMap = entry.getValue();
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(acceptEventLoopGroup, workEventLoopGroup);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, false)
                    .childOption(ChannelOption.AUTO_READ, false)
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .option(ChannelOption.SO_BACKLOG, config.ioMaxBacklog());
            if (config.openLoggingHandler()) {
                bootstrap.handler(Handlers.LOGGING_HANDLER);
            }
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    ChannelPipeline pipeline = socketChannel.pipeline();
                    if (config.openLoggingHandler()) {
                        pipeline.addFirst(Handlers.LOGGING_HANDLER);
                    }
                    pipeline.addLast(new ClientRequestHandler(config, portMap));
                }
            });
            bootstrap.bind(portMap.localAddress.host, portMap.localAddress.port).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if (channelFuture.isSuccess()) {
                        LOG.info("success bind local address on {} for remote address {}", portMap.localAddress, portMap.remoteAddress);
                    } else {
                        LOG.error("failed bind local address on {} for remote address {}", portMap.localAddress, portMap.remoteAddress, channelFuture.cause());
                    }
                }
            });
        }
        LOG.info("all port forwarding started, please to check if all port success bind");
    }

    public void shutdown() {
        if (acceptEventLoopGroup != null) {
            acceptEventLoopGroup.shutdownGracefully();
        }
        if (workEventLoopGroup != null) {
            workEventLoopGroup.shutdownGracefully();
        }
        LOG.info("all stopped");
    }
}
