FROM eclipse-temurin:21-jre-jammy
RUN apt-get update && apt-get install -y jq curl && rm -rf /var/lib/apt/lists/*
VOLUME /tmp
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app.jar"]