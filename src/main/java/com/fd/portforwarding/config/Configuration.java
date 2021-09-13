package com.fd.portforwarding.config;

import io.netty.channel.EventLoopGroup;

public interface Configuration {
    /**
     * @return mills timeout for sockets
     */
    int timeout();

    /**
     * @return mills timeout for tcp connect
     */
    int connectTimeout();

    /**
     * @return port forward mapping file path
     */
    String mappingFilePath();

    /**
     * @return io accept thread number
     */
    int ioAcceptThreadNumber();

    /**
     * @return io work thread number
     */
    int ioWorkThreadNumber();

    /**
     * @return max back log size for accept queue
     */
    int ioMaxBacklog();

    /**
     * @return if to open netty logging handler
     */
    boolean openLoggingHandler();

}
