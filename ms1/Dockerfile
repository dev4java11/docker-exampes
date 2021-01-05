FROM adoptopenjdk/openjdk15:alpine-slim as builder
COPY .mvn .mvn
COPY mvnw .
COPY pom.xml .
RUN ./mvnw dependency:go-offline
COPY src src
RUN ./mvnw package -DskipTests

FROM adoptopenjdk/openjdk15:alpine-jre as layers
ARG JAR_FILE=target/*.jar
COPY --from=builder ${JAR_FILE} app.jar
RUN java -Djarmode=layertools -jar app.jar extract
           
FROM adoptopenjdk/openjdk15:alpine-jre
COPY --from=layers dependencies/ .
RUN true
COPY --from=layers snapshot-dependencies/ .
RUN true
COPY --from=layers spring-boot-loader/ .
RUN true
COPY --from=layers application/ .
RUN true
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]