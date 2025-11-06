# Etapa 1: build del JAR con Java 17
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /src
COPY pom.xml .
COPY src ./src
RUN mvn -q -DskipTests package -Dfile.encoding=UTF-8

# Etapa 2: runtime
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /src/target/*-SNAPSHOT.jar app.jar
ENV JAVA_OPTS="-Xms256m -Xmx512m"
EXPOSE 8080
CMD ["sh","-c","java $JAVA_OPTS -jar app.jar"]
