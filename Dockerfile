FROM azul/zulu-openjdk-alpine:17.0.1-jre-headless as build
WORKDIR /build
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
RUN java -Djarmode=layertools -jar app.jar extract

FROM azul/zulu-openjdk-alpine:17.0.1-jre-headless
RUN addgroup -S appuser && adduser -S -h /app -s /bin/ash -G appuser appuser
USER appuser
WORKDIR /app
COPY --from=build /build/dependencies/ ./
COPY --from=build /build/spring-boot-loader/ ./
COPY --from=build /build/application/ ./
RUN mkdir logs
ENTRYPOINT ["java", "-Djava.net.preferIPv4Stack=true", "-XX:MaxRAMPercentage=50.0",\
 "-XX:+ExplicitGCInvokesConcurrent", "-Xlog:gc=info:file=logs/gc.log:time,tags:filecount=10,filesize=10m",\
 "-Dcom.sun.management.jmxremote", "-Dcom.sun.management.jmxremote.port=9999", "-Dcom.sun.management.jmxremote.rmi.port=9999", "-Djava.rmi.server.hostname=0.0.0.0", "-Dcom.sun.management.jmxremote.authenticate=false", "-Dcom.sun.management.jmxremote.ssl=false",\
 "org.springframework.boot.loader.JarLauncher"]