package com.fd.portforwarding;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;


public final class ChannelUtils {

    /**
     * Close Channel, if channel is null or is inactive do nothing
     * @param ch channel
     */
    public static void closeOnFlush(Channel ch) {
        if (ch == null) {
            return;
        }
        if (ch.isActive()) {
            ch.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }
}
