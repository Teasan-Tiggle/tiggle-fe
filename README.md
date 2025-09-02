# Tiggle Android App

Tiggle은 AR 기술을 활용한 기부 플랫폼 Android 애플리케이션입니다.

## 📱 프로젝트 개요

- **앱 이름**: Tiggle
- **패키지명**: `com.ssafy.tiggle`
- **최소 SDK**: API 24 (Android 7.0)
- **타겟 SDK**: API 36 (Android 14)
- **아키텍처**: MVVM + Clean Architecture
- **UI 프레임워크**: Jetpack Compose

## 🛠️ 개발환경 요구사항

### 필수 소프트웨어

- **Android Studio**: Hedgehog | 2023.1.1 이상
- **JDK**: 11 이상
- **Gradle**: 8.13
- **Kotlin**: 2.0.21
- **Android Gradle Plugin**: 8.11.1

### 시스템 요구사항

- **운영체제**: Windows 10/11, macOS 10.15+, Ubuntu 18.04+
- **RAM**: 최소 8GB (16GB 권장)
- **저장공간**: 최소 10GB 여유공간
- **Android SDK**: API 24-36 설치

## 🚀 프로젝트 설정 및 실행

### 1. 저장소 클론

```bash
git clone <repository-url>
cd tiggle-fe
```

### 2. Android Studio에서 프로젝트 열기

1. Android Studio 실행
2. `File` → `Open` 선택
3. 프로젝트 루트 폴더(`tiggle-fe`) 선택
4. 프로젝트 로딩 완료까지 대기

### 3. Gradle 동기화

프로젝트를 열면 자동으로 Gradle 동기화가 시작됩니다. 수동으로 동기화하려면:

- **Windows/Linux**: `Ctrl + Shift + O`
- **macOS**: `Cmd + Shift + O`

또는 툴바의 `Sync Project with Gradle Files` 버튼 클릭

### 4. Firebase 설정

프로젝트에는 이미 `google-services.json` 파일이 포함되어 있습니다. Firebase 프로젝트 설정이 필요한 경우:

1. [Firebase Console](https://console.firebase.google.com/) 접속
2. 프로젝트 생성 또는 기존 프로젝트 선택
3. Android 앱 추가
4. `google-services.json` 파일 다운로드
5. `app/` 폴더에 파일 복사

### 5. 앱 실행

#### 에뮬레이터에서 실행

1. **AVD Manager** 열기: `Tools` → `AVD Manager`
2. **Create Virtual Device** 클릭
3. 디바이스 선택 (예: Pixel 7)
4. 시스템 이미지 선택 (API 30 이상 권장)
5. AVD 생성 완료
6. **Run** 버튼 클릭 또는 `Shift + F10`

#### 실제 디바이스에서 실행

1. Android 디바이스에서 **개발자 옵션** 활성화
2. **USB 디버깅** 활성화
3. USB 케이블로 컴퓨터 연결
4. 디바이스 인증 확인
5. **Run** 버튼 클릭

## 📦 빌드

### Debug 빌드

```bash
# 터미널에서 실행
./gradlew assembleDebug

# 또는 Android Studio에서
Build → Build Bundle(s) / APK(s) → Build APK(s)
```

### Release 빌드

```bash
# 터미널에서 실행
./gradlew assembleRelease

# 또는 Android Studio에서
Build → Generate Signed Bundle / APK
```

### APK 위치

빌드된 APK는 다음 경로에서 확인할 수 있습니다:
```
app/build/outputs/apk/debug/app-debug.apk
app/build/outputs/apk/release/app-release.apk
```

## 🏗️ 프로젝트 구조

```
app/src/main/java/com/ssafy/tiggle/
├── 📱 presentation/           # UI 레이어
│   ├── ui/                   # Compose UI 화면들
│   │   ├── donation/         # 기부 관련 화면
│   │   ├── user/             # 사용자 관련 화면
│   │   └── ...
│   └── navigation/           # 네비게이션
├── 🏢 domain/                # 비즈니스 로직 레이어
│   ├── entity/               # 도메인 엔티티
│   ├── repository/           # Repository 인터페이스
│   └── usecase/              # UseCase 클래스들
├── 💾 data/                  # 데이터 레이어
│   ├── model/                # DTO 클래스들
│   ├── datasource/           # 데이터 소스
│   └── repository/           # Repository 구현체
├── 🔧 di/                    # 의존성 주입
├── 🔧 core/                  # 공통 유틸리티
└── TiggleApplication.kt      # Application 클래스
```

## 🛠️ 주요 기술 스택

### UI & 아키텍처
- **Jetpack Compose**: 모던 Android UI 개발
- **MVVM**: 아키텍처 패턴
- **Clean Architecture**: 레이어 분리
- **Navigation Compose**: 화면 네비게이션

### 의존성 주입 & 비동기
- **Hilt**: 의존성 주입
- **Kotlin Coroutines**: 비동기 프로그래밍
- **Flow**: 반응형 스트림

### 네트워킹
- **Retrofit**: HTTP 클라이언트
- **OkHttp**: 네트워크 라이브러리
- **Gson**: JSON 직렬화

### 미디어 & 3D
- **ExoPlayer**: 비디오 재생
- **Filament**: 3D 렌더링 엔진
- **Lottie**: 애니메이션

### 기타
- **Firebase**: 푸시 알림, 분석
- **Room**: 로컬 데이터베이스

## 🔧 개발 도구

### 코드 스타일
- **Kotlin**: 공식 코딩 컨벤션 준수
- **ktlint**: 코드 포맷팅
- **Android Studio**: 기본 린터 사용

### 버전 관리
- **Git**: 소스 코드 버전 관리
- **GitHub**: 원격 저장소

## 🐛 문제 해결

### 일반적인 문제들

#### 1. Gradle 동기화 실패
```bash
# Gradle 캐시 정리
./gradlew clean
./gradlew --refresh-dependencies
```

#### 2. 빌드 오류
```bash
# 프로젝트 클린 빌드
./gradlew clean build
```

#### 3. 메모리 부족 오류
`gradle.properties`에서 JVM 힙 크기 증가:
```properties
org.gradle.jvmargs=-Xmx4096m -Dfile.encoding=UTF-8
```

#### 4. Kotlin 버전 충돌
`gradle/libs.versions.toml`에서 Kotlin 버전 확인:
```toml
kotlin = "2.0.21"
```

### 로그 확인

```bash
# 실시간 로그 확인
adb logcat | grep "com.ssafy.tiggle"
```

## 📱 앱 기능

### 주요 기능
- **AR 기부**: AR 기술을 활용한 기부 경험
- **사용자 관리**: 회원가입, 로그인, 프로필 관리
- **기부 관리**: 기부 내역 조회, 기부 상태 추적
- **푸시 알림**: Firebase Cloud Messaging
- **3D 모델 뷰어**: Filament 엔진을 활용한 3D 렌더링

### 권한 요청
- **인터넷**: 네트워크 통신
- **네트워크 상태**: 연결 상태 확인
- **알림**: 푸시 알림 수신
- **AR 카메라**: AR 기능 사용 (선택적)

## 🤝 기여하기

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 라이선스

이 프로젝트는 SSAFY 프로젝트입니다.

## 📞 문의

프로젝트 관련 문의사항이 있으시면 팀원에게 연락해주세요.

---

**개발팀**: SSAFY 태산산 Team  
**최종 업데이트**: 2025년 8월
