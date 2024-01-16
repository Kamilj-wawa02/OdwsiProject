FROM eclipse-temurin:21-jdk
RUN apt-get update && apt-get install -y maven
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests
ENTRYPOINT ["java", "-Dspring.profiles.active=production", "-jar", "/app/target/OdwsiProject-0.0.1-SNAPSHOT.jar"]