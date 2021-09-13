# port-forwarding
tcp port forwarding based on netty / 基于Netty的TCP端口流量转发

this project may be helpful when you are not familiar with iptables or other port forwarding techs.

this project may be helpful when you want to do some stuff on data flow, based on this project's source code, you can write your own code to handle data flow before forwarding.

### configuration
this project will read config from java system properties and system env, properties first.

* mappingFilePath,  port mapping file 
* timeout, io timeout in mills for read/write idle timeout, if you connection is long-connection you may need to set timeout to bigger one
* connectTimeout, io timeout in mills for connection to target remote port
* ioAcceptThreadNumber, accept thread number default system cores
* ioWorkThreadNumber, work thread number default system cores
* ioMaxBacklog, socket backlog default 64
* openLoggingHandler, detail netty debug logging

### mapping file 
default port mapping file is **mapping.txt**, in this file you add multi port mapping lines.

this file format must be **"one line for one mapping, local address(host:port) and remote address(host:port) split by ,"**

use \# to add comment

eg:
>\# mapping local host 80 to local host 8080, port 80's any data will forward to 8080
>
>127.0.0.1:80,127.0.0.1:8080    
>
>127.0.0.1:9090,xxx.xxx.xx.xx:1234
>
>127.0.0.1:9091,xxx.xxx.xx.xx:1235

### how to use
#### 1. use released jar
~~~ shell script
jar -Xmx256m -DmappingFilePath=mapping.txt -jar portfowarding-jar-with-dependencies.jar
~~~

#### 2. use docker image
docker image: blueoom/portforwarding

~~~ shell script
docker run -tdi --network=host -e mappingFilePath=mapping.txt -v mapping.txt:/home/portforwarding/mapping.txt blueoom/portforwarding 
~~~
> suggest to use host network for multi port mapping

> use -v to set mapping.txt

> use -e to set configs, use -e JVM_ARGS="-Xmx512m" to set jvm args 

> suggest to use docker compose to manage service containers