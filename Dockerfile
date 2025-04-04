FROM openjdk:21-jdk
WORKDIR /app
# 빌드한 JAR 파일의 정확한 경로와 이름을 확인 후 수정
COPY build/libs/gamel-springboot-opensearch-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]