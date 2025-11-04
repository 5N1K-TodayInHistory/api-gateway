# Redis Cache Implementation for 5N1K API Gateway

Bu dokümantasyon, 5N1K API Gateway projesinde Redis cache yapısının nasıl kurulduğunu ve kullanıldığını açıklar.

## 🎯 Genel Bakış

Redis cache sistemi, uygulamanın performansını artırmak ve veritabanı yükünü azaltmak için tasarlanmıştır. Farklı cache türleri için özelleştirilmiş TTL (Time To Live) değerleri kullanılır.

## 📁 Oluşturulan Dosyalar

### 1. RedisConfig.java

- **Konum**: `src/main/java/com/ehocam/api_gateway/config/RedisConfig.java`
- **Amaç**: Redis bağlantı yapılandırması
- **Özellikler**:
  - Lettuce client kullanımı
  - JSON serialization
  - String key serialization
  - Connection pool yapılandırması

### 2. CacheConfig.java

- **Konum**: `src/main/java/com/ehocam/api_gateway/config/CacheConfig.java`
- **Amaç**: Cache manager ve TTL yapılandırması
- **Özellikler**:
  - Cache-specific TTL değerleri
  - Redis cache manager
  - JSON serialization
  - Environment-based configuration

## ⚙️ Yapılandırma

### application.yml Güncellemeleri

```yaml
# Cache Configuration
cache:
  type: redis
  redis:
    time-to-live: 300000 # Default 5 minutes in milliseconds
    cache-null-values: false
    enable-statistics: true

# Cache TTL Configuration (in seconds)
cache:
  ttl:
    todayByCountry: 600    # 10 minutes
    eventDetail: 1800     # 30 minutes
    similarEvents: 3600   # 60 minutes
    trending24h: 300      # 5 minutes
    supportedLocales: 21600 # 6 hours
    countries: 21600       # 6 hours
    languages: 21600      # 6 hours
```

### Ana Uygulama Sınıfı

`ApiGatewayApplication.java` dosyasına `@EnableCaching` annotation'ı eklendi.

## 🗂️ Cache Türleri ve TTL Değerleri

| Cache Türü         | TTL       | Açıklama                         |
| ------------------ | --------- | -------------------------------- |
| `todayByCountry`   | 10 dakika | Günlük ülke bazlı eventler       |
| `eventDetail`      | 30 dakika | Tekil event detayları            |
| `similarEvents`    | 60 dakika | Benzer eventler                  |
| `trending24h`      | 5 dakika  | Son 24 saatteki popüler eventler |
| `supportedLocales` | 6 saat    | Desteklenen dil kodları          |
| `countries`        | 6 saat    | Ülke listesi                     |
| `languages`        | 6 saat    | Dil listesi                      |

## 🔧 Cache Annotation'ları

### @Cacheable

Veri okuma işlemlerinde kullanılır:

```java
@Cacheable(value = "todayByCountry", key = "#country + ':' + T(java.time.LocalDate).now().getMonthValue() + '-' + T(java.time.LocalDate).now().getDayOfMonth() + ':' + #type + ':' + #page + ':' + #size + ':' + #language")
public Page<EventDto.Response> getTodaysEvents(String language, String type, String country,
                                               int page, int size, String sort, Long userId)
```

### @CacheEvict

Veri değişikliklerinde cache'i temizlemek için kullanılır:

```java
@CacheEvict(value = {"eventDetail", "todayByCountry", "trending24h"}, allEntries = true)
public EventDto.LikeResponse likeEvent(Long eventId, Long userId)
```

## 📊 Key Formatları

### 1. todayByCountry

```
todayByCountry::{country}:{monthDay}:{type}:{page}:{size}:{language}
```

**Örnek**: `todayByCountry::TR:12-25:politics:0:20:tr`

### 2. eventDetail

```
eventDetail::{id}:{language}
```

**Örnek**: `eventDetail::123:tr`

### 3. similarEvents

```
similarEvents::{id}:{language}
```

**Örnek**: `similarEvents::123:tr`

### 4. trending24h

```
trending24h::{language}:{page}:{size}
```

**Örnek**: `trending24h::tr:0:20`

### 5. countries

```
countries::{language}
```

**Örnek**: `countries::tr`

### 6. languages

```
languages::{language}
```

**Örnek**: `languages::tr`

### 7. supportedLocales

```
supportedLocales::{language}
```

**Örnek**: `supportedLocales::tr`

## 🚀 Kullanım Örnekleri

### EventService Cache Kullanımı

```java
@Service
@Transactional
public class EventService {

    // Cache'den okuma
    @Cacheable(value = "eventDetail", key = "#eventId + ':' + #language")
    public Optional<EventDto.Response> getEventById(Long eventId, String language, Long userId) {
        // Implementation
    }

    // Cache'i temizleme
    @CacheEvict(value = {"eventDetail", "todayByCountry", "trending24h"}, allEntries = true)
    public EventDto.LikeResponse likeEvent(Long eventId, Long userId) {
        // Implementation
    }
}
```

### CountryService Cache Kullanımı

```java
@Service
@Transactional
public class CountryService {

    @Cacheable(value = "countries", key = "#language")
    public List<CountryDto.Response> getAllCountries(String language) {
        // Implementation
    }
}
```

### LanguageService Cache Kullanımı

```java
@Service
@Transactional
public class LanguageService {

    @Cacheable(value = "languages", key = "#language")
    public List<LanguageDto.Response> getAllLanguages(String language) {
        // Implementation
    }

    @Cacheable(value = "supportedLocales", key = "#language")
    public List<String> getSupportedLocales(String language) {
        // Implementation
    }
}
```

## 🔍 Cache İstatistikleri

Cache istatistikleri `application.yml` dosyasında etkinleştirilmiştir:

```yaml
cache:
  redis:
    enable-statistics: true
```

Bu istatistiklere Spring Boot Actuator endpoints üzerinden erişilebilir.

## 🛠️ Geliştirme ve Test

### Redis Bağlantısını Test Etme

```bash
# Redis bağlantısını test et
redis-cli ping
```

### Cache Durumunu Kontrol Etme

```bash
# Redis'teki tüm key'leri listele
redis-cli keys "*"

# Belirli bir pattern'deki key'leri listele
redis-cli keys "todayByCountry:*"
```

## 📈 Performans Optimizasyonu

### 1. TTL Stratejisi

- **Kısa TTL**: Sık değişen veriler (trending events)
- **Orta TTL**: Orta sıklıkta değişen veriler (event details)
- **Uzun TTL**: Nadiren değişen veriler (countries, languages)

### 2. Key Design

- Hierarchical key structure
- Language-aware caching
- Date-based invalidation

### 3. Cache Eviction Strategy

- Automatic TTL-based eviction
- Manual cache eviction on data changes
- Selective cache clearing

## 🔧 Environment Variables

```bash
# Redis Configuration
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_USERNAME=
REDIS_PASSWORD=
REDIS_DATABASE=0

# Cache TTL Override (optional)
CACHE_TTL_TODAY_BY_COUNTRY=600
CACHE_TTL_EVENT_DETAIL=1800
CACHE_TTL_SIMILAR_EVENTS=3600
CACHE_TTL_TRENDING_24H=300
CACHE_TTL_SUPPORTED_LOCALES=21600
CACHE_TTL_COUNTRIES=21600
CACHE_TTL_LANGUAGES=21600
```

## 🚨 Monitoring ve Alerting

### Cache Hit/Miss Oranları

- Spring Boot Actuator metrics
- Redis monitoring tools
- Application logs

### Cache Size Monitoring

```bash
# Redis memory usage
redis-cli info memory

# Cache size by type
redis-cli --scan --pattern "todayByCountry:*" | wc -l
```

## 🔄 Cache Invalidation Strategies

### 1. Time-based Invalidation

- TTL değerleri ile otomatik temizleme

### 2. Event-based Invalidation

- Veri değişikliklerinde manuel temizleme
- `@CacheEvict` annotation'ları

### 3. Pattern-based Invalidation

- Belirli pattern'deki tüm cache'leri temizleme

## 📝 Best Practices

1. **Key Naming**: Anlamlı ve tutarlı key isimleri
2. **TTL Management**: Veri türüne göre uygun TTL değerleri
3. **Serialization**: JSON serialization kullanımı
4. **Error Handling**: Cache hatalarında fallback mekanizması
5. **Monitoring**: Cache performansının sürekli izlenmesi

## 🎯 Gelecek Geliştirmeler

1. **Cache Warming**: Uygulama başlangıcında cache'i önceden doldurma
2. **Distributed Caching**: Multi-instance deployment için
3. **Cache Compression**: Memory kullanımını optimize etme
4. **Advanced Eviction**: LRU, LFU gibi gelişmiş eviction stratejileri
