# Tiggle Android 프로젝트 아키텍처

이 프로젝트는 **MVVM 패턴**과 **클린 아키텍처**를 적용하여 구성되었습니다.

## 🏗️ 아키텍처 개요

```
┌─────────────────────────────────────────────────────────┐
│                 Presentation Layer                      │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐     │
│  │    Screen   │  │  ViewModel  │  │   UiState   │     │
│  │ (Compose UI)│  │    (MVVM)   │  │  (Events)   │     │
│  └─────────────┘  └─────────────┘  └─────────────┘     │
└─────────────────────────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────┐
│                   Domain Layer                          │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐     │
│  │   Entity    │  │  Repository │  │   UseCase   │     │
│  │ (Core Data) │  │ (Interface) │  │ (Business)  │     │
│  └─────────────┘  └─────────────┘  └─────────────┘     │
└─────────────────────────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────┐
│                    Data Layer                           │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐     │
│  │ Repository  │  │ DataSource  │  │     DTO     │     │
│  │    (Impl)   │  │(Remote/Local)│  │   (Model)   │     │
│  └─────────────┘  └─────────────┘  └─────────────┘     │
└─────────────────────────────────────────────────────────┘
```

## 📁 프로젝트 구조

```
app/src/main/java/com/ssafy/tiggle/
├── 📱 presentation/           # Presentation Layer (UI)
│   ├── ui/
│   │   └── user/
│   │       ├── UserScreen.kt      # Compose UI 화면
│   │       ├── UserViewModel.kt   # MVVM ViewModel
│   │       └── UserUiState.kt     # UI 상태 관리
│   └── navigation/
│       └── NavigationGraph.kt     # 화면 네비게이션
│
├── 🏢 domain/                 # Domain Layer (Business Logic)
│   ├── entity/
│   │   └── User.kt               # 도메인 엔티티
│   ├── repository/
│   │   └── UserRepository.kt     # Repository 인터페이스
│   └── usecase/
│       ├── GetAllUsersUseCase.kt     # 비즈니스 로직
│       ├── GetUserByIdUseCase.kt
│       └── SaveUserUseCase.kt
│
├── 💾 data/                   # Data Layer (Data Access)
│   ├── model/
│   │   └── UserDto.kt            # 데이터 전송 객체
│   ├── datasource/
│   │   ├── remote/
│   │   │   ├── UserApiService.kt     # Retrofit API 서비스
│   │   │   └── UserRemoteDataSource.kt
│   │   └── local/
│   │       └── UserLocalDataSource.kt  # 로컬 캐시
│   └── repository/
│       └── UserRepositoryImpl.kt     # Repository 구현체
│
├── 🔧 di/                     # Dependency Injection
│   ├── NetworkModule.kt           # 네트워크 의존성
│   └── RepositoryModule.kt        # Repository 바인딩
│
└── TiggleApplication.kt           # Hilt Application 클래스
```

## 🎯 레이어별 역할

### 1. Presentation Layer

- **책임**: UI 표시 및 사용자 인터랙션 처리
- **구성요소**:
    - `Screen`: Jetpack Compose UI 화면
    - `ViewModel`: UI 상태 관리 및 비즈니스 로직 호출
    - `UiState`: UI 상태 데이터 클래스
    - `UiEvent`: 사용자 액션 이벤트

### 2. Domain Layer

- **책임**: 핵심 비즈니스 로직 및 규칙
- **구성요소**:
    - `Entity`: 핵심 데이터 객체 (순수 Kotlin)
    - `Repository Interface`: 데이터 접근 계약
    - `UseCase`: 특정 비즈니스 로직 캡슐화

### 3. Data Layer

- **책임**: 데이터 액세스 및 저장
- **구성요소**:
    - `Repository Implementation`: Repository 인터페이스 구현
    - `DataSource`: 실제 데이터 소스 (API, DB, 캐시)
    - `DTO`: 데이터 전송 객체 및 변환

## 🛠️ 사용된 기술 스택

- **UI**: Jetpack Compose
- **아키텍처**: MVVM + Clean Architecture
- **의존성 주입**: Hilt
- **비동기 처리**: Kotlin Coroutines + Flow
- **네트워킹**: Retrofit + OkHttp
- **네비게이션**: Navigation Compose

## 📋 의존성 관리

- **Gradle Version Catalog** 사용 (`gradle/libs.versions.toml`)
- 모든 의존성을 중앙에서 관리
- 타입 세이프한 의존성 선언

## 🔄 데이터 플로우

1. **UI 이벤트 발생** → Screen에서 사용자 액션
2. **ViewModel 처리** → UiEvent를 받아 UseCase 호출
3. **UseCase 실행** → 비즈니스 로직 수행
4. **Repository 호출** → 데이터 액세스
5. **DataSource 처리** → 실제 데이터 소스에서 데이터 획득
6. **UI 상태 업데이트** → StateFlow를 통해 UI에 반영

## 🎨 장점

- **관심사 분리**: 각 레이어가 명확한 책임을 가짐
- **테스트 용이성**: 각 레이어를 독립적으로 테스트 가능
- **유지보수성**: 변경사항이 한 레이어에 국한됨
- **확장성**: 새로운 기능 추가가 용이함
- **재사용성**: Domain Layer는 플랫폼 독립적

