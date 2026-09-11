FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY pom.xml .
COPY src ./src
COPY mvnw .
COPY .mvn ./.mvn

# Donner les droits d'ex?cution ? mvnw
RUN chmod +x mvnw

RUN ./mvnw dependency:go-offline -B

RUN ./mvnw clean package -DskipTests

EXPOSE 8080

CMD ["java", "-jar", "target/ministere-app-1.0.0.jar"]
