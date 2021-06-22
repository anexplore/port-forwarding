package com.fd.portforwarding.config;

/**
 * get configuration from system property or system env
 */
public class SystemPropertyAndEnvConfiguration implements Configuration {

    public static final String CONFIG_PREFIX = "";

    /**
     * Get Value for key from os system env or jvm properties, jvm priority is higher than os env
     * @param key key
     * @return value or {@code defaultValue} if pro is empty
     */
    public static String getFromEnvOrPro(String key, String defaultValue) {
        String pro = System.getProperty(key);
        if (isNullOrEmpty(pro)) {
            pro = System.getenv(key);
        }
        return isNullOrEmpty(pro) ? defaultValue : pro;
    }

    @Override
    public int timeout() {
        return Integer.parseInt(getFromEnvOrPro(CONFIG_PREFIX + "timeout", "10000"));
    }

    @Override
    public String mappingFilePath() {
        return getFromEnvOrPro(CONFIG_PREFIX + "mappingFilePath", "mapping.txt");
    }

    @Override
    public int ioAcceptThreadNumber() {
        return Integer.parseInt(getFromEnvOrPro(CONFIG_PREFIX + "ioAcceptThreadNumber", Runtime.getRuntime().availableProcessors() + ""));
    }

    @Override
    public int ioWorkThreadNumber() {
        return Integer.parseInt(getFromEnvOrPro(CONFIG_PREFIX + "ioWorkThreadNumber", Runtime.getRuntime().availableProcessors() + ""));
    }

    @Override
    public int ioMaxBacklog() {
        return Integer.parseInt(getFromEnvOrPro(CONFIG_PREFIX + "ioMaxBacklog", 64 + ""));
    }

    @Override
    public boolean openLoggingHandler() {
        return Boolean.parseBoolean(getFromEnvOrPro(CONFIG_PREFIX + "openLoggingHandler", "" + false));
    }

    private static boolean isNullOrEmpty(String string) {
        return string == null || string.isEmpty();
    }
}
