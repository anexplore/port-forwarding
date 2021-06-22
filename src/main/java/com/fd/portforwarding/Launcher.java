package com.fd.portforwarding;

public class Launcher {
    
    public static void main(String[] args) throws Exception {
        PortForwardingBootstrap bootstrap = new PortForwardingBootstrap();
        bootstrap.startup();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                bootstrap.shutdown();
            }
        });
    }
}
