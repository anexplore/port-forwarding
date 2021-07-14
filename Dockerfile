FROM openjdk:8
LABEL MAINTAINER anexplore@github.com

ADD entrypoint.sh run_portforwarding.sh logback.xml /home/portforwarding/
ADD portforwarding-jar-with-dependencies.jar /home/portforwarding
ADD mapping.txt /home/portforwarding
WORKDIR /home/portforwarding
RUN chmod 755 entrypoint.sh
ENTRYPOINT ["./entrypoint.sh"]
CMD ["portforwarding"]
