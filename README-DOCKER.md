# API Gateway - Docker Setup

Bu proje Spring Boot tabanlı bir API Gateway uygulamasıdır ve Docker ile containerize edilmiştir.

## 🚀 Hızlı Başlangıç

### Gereksinimler

- Docker 20.10+
- Docker Compose 2.0+
- Java 21 (development için)

### Development Ortamı

1. **Repository'yi klonlayın:**

```bash
git clone <repository-url>
cd api-gateway
```

2. **Environment dosyasını oluşturun:**

```bash
cp env.example .env
# .env dosyasını düzenleyin
```

3. **Database ve Redis servislerini başlatın:**

```bash
docker-compose -f docker-compose.dev.yml up -d
```

4. **Spring Boot uygulamasını çalıştırın:**

```bash
./mvnw spring-boot:run
```

5. **Uygulamaya erişin:**

- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- Health Check: http://localhost:8080/actuator/health

### Production Deployment

Production deployment Dokploy üzerinden yapılır. GitHub Actions otomatik olarak Docker image'ı build eder ve GitHub Container Registry'ye push eder.

## 🏗️ Docker Yapısı

### Multi-Stage Build

- **Builder Stage**: Maven ile uygulama build edilir
- **Production Stage**: Sadece JRE ve uygulama JAR'ı içerir

### Security Best Practices

- Non-root user kullanımı
- Minimal base image (Alpine Linux)
- Health checks
- Resource limits
- Security scanning (Trivy)

## 🔧 Konfigürasyon

### Environment Variables

| Variable                 | Description               | Default                                       |
| ------------------------ | ------------------------- | --------------------------------------------- |
| `DATABASE_URL`           | PostgreSQL connection URL | `jdbc:postgresql://postgres:5432/api_gateway` |
| `DATABASE_USERNAME`      | Database username         | `postgres`                                    |
| `DATABASE_PASSWORD`      | Database password         | `postgres`                                    |
| `REDIS_HOST`             | Redis host                | `redis`                                       |
| `REDIS_PORT`             | Redis port                | `6379`                                        |
| `JWT_SECRET`             | JWT secret key            | -                                             |
| `GOOGLE_CLIENT_ID`       | Google OAuth2 client ID   | -                                             |
| `CORS_ALLOWED_ORIGINS`   | CORS allowed origins      | -                                             |
| `SERVER_PORT`            | Application port          | `8080`                                        |
| `SPRING_PROFILES_ACTIVE` | Spring profile            | `prod`                                        |

### Docker Compose Services (Development Only)

#### postgres

- PostgreSQL 15 database
- Port: 5432
- Persistent volume: `postgres_data_dev`

#### redis

- Redis 7 cache
- Port: 6379
- Persistent volume: `redis_data_dev`

## 🚀 CI/CD Pipeline

### GitHub Actions Workflow

Pipeline aşağıdaki adımları içerir:

1. **Test**: Unit testler çalıştırılır
2. **Build**: Docker image build edilir
3. **Security Scan**: Trivy ile güvenlik taraması
4. **Push**: GitHub Container Registry'ye push

### Registry

- **GitHub Container Registry (GHCR)** kullanılır
- Multi-architecture support (AMD64, ARM64)
- Layer caching ile optimize edilmiş build
- Dokploy üzerinden production deployment

## 📊 Monitoring

### Health Checks

- Application: `http://localhost:8080/actuator/health`
- Database: PostgreSQL health check
- Cache: Redis ping check

### Logs

```bash
# Database ve Redis logları
docker-compose -f docker-compose.dev.yml logs -f

# Database logları
docker-compose -f docker-compose.dev.yml logs -f postgres

# Redis logları
docker-compose -f docker-compose.dev.yml logs -f redis
```

## 🔒 Security

### Docker Security

- Non-root user (appuser:1001)
- Minimal base image
- No unnecessary packages
- Security scanning

### Application Security

- JWT authentication
- OAuth2 integration
- CORS configuration
- Rate limiting
- Input validation

## 🛠️ Development

### Development Workflow

```bash
# 1. Database ve Redis servislerini başlat
docker-compose -f docker-compose.dev.yml up -d

# 2. Spring Boot uygulamasını çalıştır
./mvnw spring-boot:run

# 3. Debug mode için
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
```

### Database Migration

```bash
# Flyway migration otomatik çalışır
# Database servisini başlat
docker-compose -f docker-compose.dev.yml up -d postgres
```

## 📝 Useful Commands

```bash
# Development servislerini başlat
docker-compose -f docker-compose.dev.yml up -d

# Development servislerini durdur
docker-compose -f docker-compose.dev.yml down

# Volume'ları da sil
docker-compose -f docker-compose.dev.yml down -v

# Logları takip et
docker-compose -f docker-compose.dev.yml logs -f

# Database'e bağlan
docker-compose -f docker-compose.dev.yml exec postgres psql -U postgres -d api_gateway

# Redis'e bağlan
docker-compose -f docker-compose.dev.yml exec redis redis-cli

# Spring Boot uygulamasını çalıştır
./mvnw spring-boot:run

# Testleri çalıştır
./mvnw test

# Docker image build et
docker build -t api-gateway .
```

## 🐛 Troubleshooting

### Common Issues

1. **Port conflicts**: 8080, 5432, 6379 portlarının boş olduğundan emin olun
2. **Permission issues**: Docker daemon'ın çalıştığından emin olun
3. **Memory issues**: Docker Desktop'ta yeterli memory ayırın

### Debug Commands

```bash
# Container durumunu kontrol et
docker-compose -f docker-compose.dev.yml ps

# Resource kullanımını kontrol et
docker stats

# Network'i kontrol et
docker network ls
docker network inspect api-gateway_api-gateway-network

# Database connection test
docker-compose -f docker-compose.dev.yml exec postgres pg_isready -U postgres

# Redis connection test
docker-compose -f docker-compose.dev.yml exec redis redis-cli ping
```

## 📚 Additional Resources

- [Spring Boot Docker Guide](https://spring.io/guides/gs/spring-boot-docker/)
- [Docker Best Practices](https://docs.docker.com/develop/dev-best-practices/)
- [GitHub Actions](https://docs.github.com/en/actions)
