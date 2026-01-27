FROM amazoncorretto:25.0.2-alpine

ENV WORKDIR=/opt
WORKDIR ${WORKDIR}

COPY ./target/ ${WORKDIR}/

EXPOSE 8080

CMD java -Xmx1G -jar ${WORKDIR}/*.jar

