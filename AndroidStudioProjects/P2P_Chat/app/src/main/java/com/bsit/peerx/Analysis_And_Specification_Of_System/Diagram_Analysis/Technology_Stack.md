# Technology Stack

## 1. Tổng quan

Tài liệu này liệt kê toàn bộ công nghệ, nền tảng và thư viện đang được sử dụng trong dự án `P2P_Chat`.

## 2. Nền tảng và công cụ build

| Nhóm | Công nghệ | Phiên bản |
| --- | --- | --- |
| Nền tảng | Android | minSdk 26, targetSdk 36, compileSdk 36 |
| Ngôn ngữ | Kotlin | 2.2.10 |
| Ngôn ngữ phụ trợ | Java | 11 |
| Build tool | Android Gradle Plugin | 9.1.1 |
| Build tool | Gradle KTS | Sử dụng `build.gradle.kts` |
| Sinh mã | KSP | 2.2.10-2.0.2 |
| Style | Kotlin official code style | bật trong `gradle.properties` |

## 3. Kiến trúc ứng dụng

| Nhóm | Công nghệ / Mẫu thiết kế |
| --- | --- |
| Kiến trúc | MVVM |
| UI | Jetpack Compose |
| Điều hướng | Navigation Compose |
| Dependency Injection | Hilt |
| Local persistence | Room |
| Concurrency | Kotlin Coroutines + Flow |

## 4. UI / Presentation

| Thư viện | Vai trò |
| --- | --- |
| `androidx.activity:activity-compose` | Tích hợp Activity với Compose |
| `androidx.compose.ui:ui` | UI Compose lõi |
| `androidx.compose.ui:ui-graphics` | Vẽ và xử lý đồ họa |
| `androidx.compose.ui:ui-tooling-preview` | Preview trong Android Studio |
| `androidx.compose.material3:material3` | Hệ Material 3 |
| `androidx.navigation:navigation-compose` | Điều hướng Compose |
| `androidx.lifecycle:lifecycle-runtime-compose` | Collect state trong Compose |
| `androidx.lifecycle:lifecycle-viewmodel-compose` | Tích hợp ViewModel với Compose |
| `androidx.compose.ui:ui-tooling` | Tooling/debug Compose |
| `androidx.compose.ui:ui-test-junit4` | Test UI Compose |
| `androidx.compose.ui:ui-test-manifest` | Manifest cho Compose tests |
| `androidx.compose.ui:ui-text-google-fonts` | Hỗ trợ Google Fonts |

## 5. Data / Storage

| Thư viện | Vai trò |
| --- | --- |
| `androidx.room:room-runtime` | ORM local database |
| `androidx.room:room-ktx` | Coroutine support cho Room |
| `androidx.room:room-compiler` | Sinh code Room qua KSP |
| `androidx.lifecycle:lifecycle-runtime-ktx` | Lifecycle runtime |
| `androidx.lifecycle:lifecycle-viewmodel-ktx` | ViewModel support |
| `androidx.lifecycle:lifecycle-process` | Process lifecycle |
| `androidx.core:core-ktx` | Kotlin extensions cho Android core |
| `androidx.biometric:biometric` | Xác thực sinh trắc học |
| `android.content.SharedPreferences` | Lưu cấu hình người dùng |

## 6. Đồng bộ, mạng và bảo mật

| Thư viện / API | Vai trò |
| --- | --- |
| `kotlinx.coroutines:coroutines-core` | Xử lý bất đồng bộ |
| `kotlinx.coroutines:coroutines-android` | Coroutine trên Android |
| `kotlinx.serialization:kotlinx-serialization-json` | Serialize/deserialize JSON packet |
| `com.google.crypto.tink:tink-android` | Mã hóa và bảo mật khóa |
| Android Wi‑Fi Direct API | Khám phá peer và kết nối P2P |
| Java Socket API | Giao tiếp TCP control channel |
| `java.net.NetworkInterface` | Lấy địa chỉ IP local |

## 7. DI / Dependency Injection

| Thư viện | Vai trò |
| --- | --- |
| `com.google.dagger:hilt-android` | DI runtime |
| `com.google.dagger:hilt-android-compiler` | Sinh mã Hilt |
| `androidx.hilt:hilt-navigation-compose` | Inject ViewModel trong Compose navigation |

## 8. Kiểm thử

| Thư viện | Vai trò |
| --- | --- |
| `junit:junit` | Unit test |
| `androidx.test.ext:junit` | Android instrumented test support |
| `androidx.test.espresso:espresso-core` | UI test trên thiết bị/emulator |

## 9. Danh sách thư viện chính

- AndroidX Core KTX
- AndroidX Lifecycle Runtime KTX
- AndroidX Lifecycle Runtime Compose
- AndroidX Lifecycle ViewModel KTX
- AndroidX Lifecycle ViewModel Compose
- AndroidX Activity Compose
- Kotlin Coroutines Core
- Kotlin Coroutines Android
- Room Runtime
- Room KTX
- Room Compiler
- Jetpack Compose BOM
- Compose UI
- Compose UI Graphics
- Compose UI Text Google Fonts
- Compose UI Tooling Preview
- Compose Material3
- Navigation Compose
- Hilt Android
- Hilt Android Compiler
- Hilt Navigation Compose
- AndroidX Biometric
- AndroidX Lifecycle Process
- Tink Android
- Kotlinx Serialization JSON
- JUnit
- AndroidX JUnit
- Espresso Core

## 10. Kết luận

Project được xây dựng trên Android hiện đại với Jetpack Compose, Hilt, Room, Coroutines và Serialization. Phần kết nối P2P sử dụng API gốc của Android/Wi‑Fi Direct, socket TCP và Tink để đảm bảo trao đổi dữ liệu và bảo mật.
