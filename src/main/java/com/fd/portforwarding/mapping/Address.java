package com.fd.portforwarding.mapping;

public class Address {
    public final String host;
    public final int port;

    public Address(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public String toString() {
        return host + ":" + port;
    }
}
