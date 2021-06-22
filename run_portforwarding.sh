#!/bin/bash
if [[ ""$JVM_ARGS == "" ]]; then
    JVM_ARGS="-Xmx512m "
fi
java -Dlogback.configurationFile=logback.xml $JVM_ARGS  -jar portforwarding-jar-with-dependencies.jar
