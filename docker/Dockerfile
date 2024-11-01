# Alpine Linux 기반 이미지 사용
FROM eclipse-temurin:21-jdk-alpine AS build

# 작업 디렉토리 설정
WORKDIR /workspace/app

# Gradle 관련 파일들과 소스 코드를 컨테이너로 복사
COPY gradle/wrapper/ gradle/wrapper/
COPY gradlew build.gradle settings.gradle ./
COPY src/ src/

# Gradlew에 실행 권한 추가
RUN chmod +x gradlew && ./gradlew build -x test

# 실행 스테이지: JRE 21을 포함한 Alpine Linux 기반 이미지 사용
FROM eclipse-temurin:21-jre-alpine
VOLUME /tmp

# 빌드 스테이지에서 생성된 JAR 파일을 현재 이미지로 복사
COPY --from=build /workspace/app/build/libs/*.jar app.jar

# 컨테이너 시작 시 실행할 명령 설정
ENTRYPOINT ["java", "-jar", "/app.jar"]
