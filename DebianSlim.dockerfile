ARG JAVA_VERSION=17.0.1
FROM openjdk:${JAVA_VERSION}-slim as build
WORKDIR /build
COPY target/*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

FROM openjdk:${JAVA_VERSION}-slim
RUN addgroup --system appuser && adduser --system --home /app --ingroup appuser appuser
USER appuser
WORKDIR /app
COPY --from=build /build/dependencies/ ./lib/
COPY --from=build /build/spring-boot-loader/ ./lib/
COPY --from=build /build/application/ ./lib/
RUN mkdir logs
ENTRYPOINT ["java", "-cp", "lib", "org.springframework.boot.loader.JarLauncher"]