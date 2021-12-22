ARG JAVA_VERSION=17.0.1
FROM azul/zulu-openjdk-alpine:${JAVA_VERSION}-jre-headless as build
WORKDIR /build
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
RUN java -Djarmode=layertools -jar app.jar extract

FROM azul/zulu-openjdk-alpine:${JAVA_VERSION}-jre-headless
RUN addgroup -S appuser && adduser -S -h /app -s /bin/ash -G appuser appuser
USER appuser
WORKDIR /app
COPY --from=build /build/dependencies/ ./
COPY --from=build /build/spring-boot-loader/ ./
COPY --from=build /build/application/ ./
RUN mkdir logs
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]