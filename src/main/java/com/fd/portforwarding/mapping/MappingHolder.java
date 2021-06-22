package com.fd.portforwarding.mapping;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MappingHolder {

    private final ConcurrentHashMap<String, PortMap> mapping;

    public MappingHolder() {
        mapping = new ConcurrentHashMap<>();
    }

    /**
     * find {@link PortMap} by local address
     *
     * @param host local address host
     * @param port local address port
     * @return {@link PortMap}
     */
    public PortMap resolveMappingByLocalAddress(String host, int port) {
        return mapping.get(String.format("%s:%d", host, port));
    }

    /**
     * @return all mapping items
     */
    public Set<Map.Entry<String, PortMap>> entrySet() {
        return mapping.entrySet();
    }

    /**
     * add not null port map to holder
     * @param portMap {@link PortMap} must not be null
     */
    public void addPortMap(PortMap portMap) {
        if (portMap == null || portMap.localAddress == null || portMap.remoteAddress == null) {
            throw new RuntimeException("local address or remote address cannot be null");
        }
        mapping.put(String.format("%s:%d", portMap.localAddress.host, portMap.localAddress.port), portMap);
    }

    /**
     * @return mapping size
     */
    public int size() {
        return mapping.size();
    }

    /**
     * Read address mapping from local file
     * <pre>
     * file format must be "One line for one mapping, local address(host:port) and remote address(host:port) split by ,"
     * use # to add comment
     * eg:
     *    # mapping local host 80 to local host 8080, port 80's any data will forward to 8080
     *    127.0.0.1:80,127.0.0.1:8080
     * </pre>
     *
     * @param filePath file path
     * @return {@link MappingHolder}
     * @throws Exception file read exceptions or wrong data format
     */
    public static MappingHolder buildMappingHolderFromFile(String filePath) throws Exception {
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        MappingHolder holder = new MappingHolder();
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            String[] localRemote = line.split(",");
            String[] localHostPort = localRemote[0].split(":");
            String[] remoteHostPort = localRemote[1].split(":");
            holder.addPortMap(new PortMap(new Address(localHostPort[0], Integer.parseInt(localHostPort[1])),
                    new Address(remoteHostPort[0], Integer.parseInt(remoteHostPort[1]))));
        }
        return holder;
    }
}
