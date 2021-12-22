FROM openjdk:17.0.1-slim as build
WORKDIR /build
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
RUN java -Djarmode=layertools -jar app.jar extract

FROM openjdk:17.0.1-slim
WORKDIR /app
COPY --from=build /build/dependencies/ ./
COPY --from=build /build/spring-boot-loader/ ./
COPY --from=build /build/application/ ./
RUN addgroup --system appuser && adduser --system --home /app --ingroup appuser appuser
RUN chown -R appuser:appuser /app
USER appuser
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]