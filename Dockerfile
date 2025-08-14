# syntax=docker/dockerfile:1

# ---- Build stage ----
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Gradle Wrapper & 설정 복사
COPY gradlew gradlew
COPY gradle gradle
COPY settings.gradle* ./
COPY build.gradle* ./

# 소스 복사
COPY src src

# 권한 부여 후 빌드
RUN chmod +x gradlew
RUN ./gradlew clean bootJar -x test

# ---- Run stage ----
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# (선택) non-root 사용자
RUN addgroup -S app && adduser -S app -G app
USER app

# 빌드 산출물 복사
COPY --from=build /app/build/libs/*.jar /app/app.jar

# Render가 주는 포트 사용 (로컬 기본은 8080)
ENV PORT=8080
# 배포 프로필 기본값
ENV SPRING_PROFILES_ACTIVE=prod

# Render는 $PORT로 리슨해야 외부에서 연결돼요
CMD ["sh", "-c", "java -Dserver.port=$PORT -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} -jar /app/app.jar"]
