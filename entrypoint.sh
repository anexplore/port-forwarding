#!/bin/bash
CMD=$1
case $CMD in
    portforwarding)
        if [[ ""$JVM_ARGS == "" ]]; then
            JVM_ARGS="-Xmx512m "
        fi
        exec java -Dlogback.configurationFile=logback.xml $JVM_ARGS  -jar portforwarding-jar-with-dependencies.jar
    ;;
    *)
        exec $@
    ;;
esac
    
