FROM eclipse-temurin:17-jre-alpine
LABEL maintainer="test_user@gmail.com"
WORKDIR /app
EXPOSE 8084
COPY target/aws-ec2-cloudwatch-sqs-demo-1.0.0.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]