package com.fd.portforwarding.mapping;

public class PortMap {
    public final Address localAddress;
    public final Address remoteAddress;

    public PortMap(Address localAddress, Address remoteAddress) {
        this.localAddress = localAddress;
        this.remoteAddress = remoteAddress;
    }

    @Override
    public String toString() {
        return "PortMap{" +
                "localAddress=" + localAddress +
                ", remoteAddress=" + remoteAddress +
                '}';
    }
}
