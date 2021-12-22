ARG JAVA_VERSION=17.0.1
FROM openjdk:${JAVA_VERSION}-slim as build
WORKDIR /build
COPY target/*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

FROM openjdk:${JAVA_VERSION}-slim
RUN addgroup --system appuser && adduser --system --home /app --ingroup appuser appuser
USER appuser
WORKDIR /app
COPY --from=build /build/dependencies/ ./
COPY --from=build /build/spring-boot-loader/ ./
COPY --from=build /build/application/ ./
RUN mkdir logs
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]