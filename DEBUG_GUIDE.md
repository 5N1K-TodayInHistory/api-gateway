# 🐛 Debug Guide - API Gateway

Bu rehber, API Gateway projesini debug etmek için gerekli tüm ayarları içerir.

## 🚀 Debug Modları

### 1. **Normal Debug Mode** (Uygulama başlar, debugger bağlanabilir)

```bash
./debug.sh
```

### 2. **Remote Debug Mode** (Debugger bağlantısını bekler)

```bash
./debug-remote.sh
```

### 3. **Manuel Debug Mode**

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=debug
```

## 🔧 IDE Debug Konfigürasyonu

### **IntelliJ IDEA**

1. **Run Configuration Oluştur:**

   - `Run` → `Edit Configurations`
   - `+` → `Spring Boot`
   - **Name:** `API Gateway Debug`
   - **Main class:** `com.ehocam.api_gateway.ApiGatewayApplication`
   - **Active profiles:** `debug`
   - **VM options:** `-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005`

2. **Remote Debug Configuration:**
   - `Run` → `Edit Configurations`
   - `+` → `Remote JVM Debug`
   - **Name:** `API Gateway Remote Debug`
   - **Host:** `localhost`
   - **Port:** `5005`
   - **Command line arguments:** `-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005`

### **VS Code**

1. **launch.json Oluştur:**

```json
{
  "version": "0.2.0",
  "configurations": [
    {
      "type": "java",
      "name": "Debug API Gateway",
      "request": "launch",
      "mainClass": "com.ehocam.api_gateway.ApiGatewayApplication",
      "projectName": "api-gateway",
      "args": "--spring.profiles.active=debug",
      "vmArgs": "-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
    },
    {
      "type": "java",
      "name": "Attach to API Gateway",
      "request": "attach",
      "hostName": "localhost",
      "port": 5005
    }
  ]
}
```

### **Eclipse**

1. **Debug Configuration:**

   - `Run` → `Debug Configurations`
   - `Java Application` → `New`
   - **Project:** `api-gateway`
   - **Main class:** `com.ehocam.api_gateway.ApiGatewayApplication`
   - **Arguments** → **VM arguments:** `-Dspring.profiles.active=debug`

2. **Remote Debug:**
   - `Run` → `Debug Configurations`
   - `Remote Java Application` → `New`
   - **Host:** `localhost`
   - **Port:** `5005`

## 📊 Debug Profili Özellikleri

Debug profili (`debug`) aktif olduğunda:

### **Logging Seviyeleri:**

- `com.ehocam.api_gateway`: `DEBUG`
- `org.springframework.security`: `DEBUG`
- `org.springframework.web`: `DEBUG`
- `org.hibernate.SQL`: `DEBUG`
- `org.hibernate.type.descriptor.sql.BasicBinder`: `TRACE`
- `org.springframework.transaction`: `DEBUG`
- `org.springframework.orm.jpa`: `DEBUG`

### **JPA/Hibernate Debug:**

- SQL sorguları gösterilir
- SQL parametreleri gösterilir
- SQL yorumları gösterilir
- Hibernate binding detayları gösterilir

### **Enhanced Logging Pattern:**

```
2025-10-14 15:30:45.123 [main] DEBUG [com.ehocam.api_gateway.service.AuthService] - Processing authentication request
```

## 🔍 Debug İpuçları

### **Breakpoint'ler:**

- `AuthController.googleOAuth2Login()` - Google OAuth2 endpoint
- `GoogleOAuth2Service.verifyIdTokenAndGetUser()` - Token verification
- `TokenService.createRefreshToken()` - Token creation
- `AuthService.generateTokensForUser()` - Token generation

### **Watch Expressions:**

- `request.getIdToken()` - Gelen ID token
- `user.getEmail()` - Kullanıcı email
- `response.getAccessToken()` - Oluşturulan access token

### **Debug Console:**

```java
// Token hash'ini kontrol et
tokenService.hashRefreshToken("test-token")

// Kullanıcı bilgilerini kontrol et
userRepository.findByEmail("test@example.com")
```

## 🚨 Troubleshooting

### **Port 5005 Kullanımda:**

```bash
# Port'u kullanan process'i bul
lsof -i :5005

# Process'i sonlandır
kill -9 <PID>
```

### **Debug Bağlantısı Kurulamıyor:**

1. Firewall ayarlarını kontrol edin
2. Port 5005'in açık olduğundan emin olun
3. IDE'de doğru host/port ayarlarını kontrol edin

### **Log Çıktısı Görünmüyor:**

1. `application.yml`'de debug profili aktif olduğundan emin olun
2. Logging seviyelerini kontrol edin
3. Console output'u kontrol edin

## 📝 Debug Komutları

```bash
# Debug modunda başlat
./debug.sh

# Remote debug modunda başlat
./debug-remote.sh

# Belirli bir profille başlat
./mvnw spring-boot:run -Dspring-boot.run.profiles=debug

# JVM debug parametreleri ile başlat
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
```

## 🎯 Debug Senaryoları

### **1. Google OAuth2 Debug:**

- Breakpoint: `GoogleOAuth2Service.verifyIdTokenAndGetUser()`
- Watch: `idTokenString`, `payload.getEmail()`

### **2. Token Generation Debug:**

- Breakpoint: `TokenService.createRefreshToken()`
- Watch: `opaqueToken`, `tokenHash`

### **3. Database Operations Debug:**

- Breakpoint: `UserRepository.save()`
- Watch: `user.getEmail()`, `user.getRoles()`

---

**Not:** Debug modunda uygulama daha yavaş çalışacaktır. Production'da debug profili kullanmayın!
