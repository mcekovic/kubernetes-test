ARG JAVA_VERSION=17.0.1
FROM azul/zulu-openjdk-alpine:${JAVA_VERSION}-jre-headless as build
WORKDIR /build
COPY target/*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

FROM azul/zulu-openjdk-alpine:${JAVA_VERSION}-jre-headless
RUN addgroup -S appuser && adduser -S -h /app -s /bin/ash -G appuser appuser
USER appuser
WORKDIR /app
COPY --from=build /build/dependencies/ ./lib/
COPY --from=build /build/spring-boot-loader/ ./lib/
COPY --from=build /build/application/ ./lib/
RUN mkdir logs
ENTRYPOINT ["java", "-cp", "lib", "org.springframework.boot.loader.JarLauncher"]