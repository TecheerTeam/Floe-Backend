FROM openjdk:17-jdk-slim AS builder

# 작업 디렉토리 설정
WORKDIR /app

# Gradle Wrapper 파일과 관련 디렉토리 복사
COPY gradlew .
COPY gradle ./gradle

# gradlew에 실행 권한 부여
RUN chmod +x gradlew

# 소스 코드 복사
COPY . .

# Gradle 빌드 실행 (테스트는 제외)
RUN ./gradlew clean build -x test

FROM openjdk:17-jdk-slim
WORKDIR /app

# 빌드된 JAR 파일 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
