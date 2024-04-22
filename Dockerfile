# 1. JDK 설치(되어있는 이미지를 고르기)
FROM eclipse-temurin:17 as build

# 2. 소스코드 가져오기
# 2-1. 작업 공간 마련하기(없을 경우 생성 후 이동)
WORKDIR /app
# 2-2. 소스코드 복사해오기
COPY . .

# 3. gradlew 실행 권한 부여
RUN chmod +x ./gradlew

# 3. 소스코드 빌드
RUN ./gradlew bootJar

# 3-1. Jar 파일 이동
RUN mv build/libs/*.jar app.jar

# 여기부터 새로운 stage가 시작된다.
FROM eclipse-temurin:17-jre

# Chrome 설치를 위한 환경 설정 및 필수 패키지 설치
RUN apt-get update && apt-get install -y wget gnupg2 \
    fonts-liberation libappindicator3-1 libasound2 libatk-bridge2.0-0 \
    libatk1.0-0 libcups2 libdbus-1-3 libgdk-pixbuf2.0-0 libnspr4 libnss3 \
    libxcomposite1 libxdamage1 libxrandr2 xdg-utils --no-install-recommends \
    && rm -rf /var/lib/apt/lists/*

# Google Chrome 설치
RUN wget -q -O - https://dl.google.com/linux/linux_signing_key.pub | apt-key add - \
    && echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google-chrome.list \
    && apt-get update \
    && apt-get install -y google-chrome-stable --no-install-recommends

WORKDIR /app

# COPY를 하되, 위 build 단계에서 만든 app.jar만 가져온다.
COPY --from=build /app/app.jar .

# 4. Jar 파일 실행
CMD ["java", "-jar", "app.jar"]

# 4 + @. 컨테이너가 실행되었을 때 요청을 듣고 있는 포트를 나열해준다.
EXPOSE 8080