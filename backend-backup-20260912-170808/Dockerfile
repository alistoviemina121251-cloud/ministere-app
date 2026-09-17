# =========================================================
# ÉTAPE 1 : BUILD
# =========================================================

FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /build

COPY pom.xml .

RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn clean package -DskipTests -B


# =========================================================
# ÉTAPE 2 : RUNTIME
# =========================================================

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build \
    /build/target/ministere-app-1.0.0.jar \
    app.jar

RUN mkdir -p /var/data/uploads

EXPOSE 8080

ENV JAVA_OPTS=""

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]