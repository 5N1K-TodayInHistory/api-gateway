# CacheWrapper - Generic Cache Management

[CacheManager.michaco.net](https://cachemanager.michaco.net/) benzeri generic cache wrapper'ı Spring CacheManager için.

## 🚀 Özellikler

- ✅ **Type-Safe**: Generic type safety ile güvenli cache işlemleri
- ✅ **Clean API**: CacheManager.michaco.net benzeri temiz API
- ✅ **Spring Integration**: Spring CacheManager ile tam entegrasyon
- ✅ **Multiple Patterns**: AddOrUpdate, GetOrCompute, basic CRUD
- ✅ **Statistics**: Cache istatistikleri
- ✅ **Null Safety**: Null-safe operations

## 📦 Kullanım

### 1. Temel Cache İşlemleri

```java
@Autowired
private CacheWrapper cacheWrapper;

// Cache'e değer koy
cacheWrapper.put("myCache", "key1", "Hello World");

// Cache'den değer al
String value = cacheWrapper.get("myCache", "key1", String.class);

// Key var mı kontrol et
boolean exists = cacheWrapper.containsKey("myCache", "key1");

// Cache'den sil
cacheWrapper.evict("myCache", "key1");

// Tüm cache'i temizle
cacheWrapper.clear("myCache");
```

### 2. AddOrUpdate Pattern

```java
// CacheManager.michaco.net benzeri API
Integer result = cacheWrapper.addOrUpdate(
    "userStats",
    "user:123:loginCount",
    1, // Yeni değer (key yoksa)
    currentCount -> currentCount + 1 // Update function (key varsa)
);
```

### 3. GetOrCompute Pattern

```java
// Cache miss durumunda compute function çalışır
String result = cacheWrapper.getOrCompute(
    "expensiveData",
    "complex:calculation:123",
    String.class,
    () -> {
        // Expensive operation - sadece cache miss'te çalışır
        return performExpensiveCalculation();
    }
);
```

### 4. Gerçek Dünya Örnekleri

#### User Session Management

```java
public void manageUserSession(String userId, String sessionId) {
    cacheWrapper.addOrUpdate(
        "userSessions",
        "session:" + userId,
        sessionId, // Yeni session
        oldSession -> sessionId // Session'ı güncelle
    );
}
```

#### API Rate Limiting

```java
public boolean checkRateLimit(String apiKey, int maxRequests) {
    Integer count = cacheWrapper.addOrUpdate(
        "rateLimits",
        "rate:" + apiKey,
        1, // İlk istek
        current -> current + 1 // Artır
    );
    return count <= maxRequests;
}
```

#### Expensive Database Query Caching

```java
public List<EventDto.Response> getEventsCached(String category, int limit) {
    return cacheWrapper.getOrCompute(
        "expensiveQueries",
        category + ":" + limit,
        List.class,
        () -> eventRepository.findByCategory(category, limit) // Expensive DB query
    );
}
```

## 🔧 EventService Integration

EventService'de CacheWrapper kullanımı:

```java
@Service
public class EventService {

    @Autowired
    private CacheWrapper cacheWrapper;

    public Page<EventDto.Response> getTodaysEvents(...) {
        String cacheKey = generateTodayCacheKey(country, type, page, size, language);

        return cacheWrapper.getOrCompute(
            "todayByCountry",
            cacheKey,
            Page.class,
            () -> getEventsForDay(0, type, country, page, size, sort, userId, language)
        );
    }

    public Optional<EventDto.Response> getEventById(Long eventId, String language, Long userId) {
        String cacheKey = "event:" + eventId + ":" + language;

        EventDto.Response cached = cacheWrapper.get("eventDetail", cacheKey, EventDto.Response.class);
        if (cached != null) {
            return Optional.of(cached);
        }

        Optional<EventDto.Response> result = eventRepository.findById(eventId)
                .map(event -> convertToResponse(event, language));

        if (result.isPresent()) {
            cacheWrapper.put("eventDetail", cacheKey, result.get());
        }

        return result;
    }
}
```

## 📊 Cache Statistics

```java
CacheWrapper.CacheStats stats = cacheWrapper.getStats("myCache");
System.out.println("Hits: " + stats.getHits());
System.out.println("Misses: " + stats.getMisses());
System.out.println("Hit Rate: " + stats.getHitRate());
System.out.println("Size: " + stats.getSize());
```

## 🎯 Avantajlar

### CacheManager.michaco.net ile Karşılaştırma

| Özellik             | CacheManager.michaco.net | CacheWrapper      |
| ------------------- | ------------------------ | ----------------- |
| **Platform**        | .NET/C#                  | Java/Spring       |
| **Type Safety**     | ✅ Generic               | ✅ Generic        |
| **AddOrUpdate**     | ✅                       | ✅                |
| **GetOrCompute**    | ✅                       | ✅                |
| **Statistics**      | ✅                       | ✅                |
| **Multiple Layers** | ✅                       | ✅ (Spring Cache) |
| **Serialization**   | ✅                       | ✅ (Jackson)      |
| **Configuration**   | ✅                       | ✅ (Spring)       |

### Spring Cache Annotation'ları ile Karşılaştırma

| Özellik           | @Cacheable | CacheWrapper |
| ----------------- | ---------- | ------------ |
| **Type Safety**   | ❌         | ✅           |
| **Flexibility**   | ❌         | ✅           |
| **Control**       | ❌         | ✅           |
| **Debugging**     | ❌         | ✅           |
| **Complex Logic** | ❌         | ✅           |

## 🚀 Performans

- **Cache Hit**: ~1ms
- **Cache Miss**: DB query time + cache write
- **Memory Usage**: Spring CacheManager controlled
- **Serialization**: Jackson (configurable)

## 🔧 Konfigürasyon

CacheWrapper Spring CacheManager'ı kullanır, bu yüzden `application.yml`'de cache konfigürasyonu:

```yaml
spring:
  cache:
    type: redis
    redis:
      time-to-live: 600000 # 10 minutes
      cache-null-values: false
```

## 📝 Örnekler

Detaylı kullanım örnekleri için `CacheWrapperExamples.java` dosyasına bakın.

## 🎉 Sonuç

CacheWrapper, Spring CacheManager için [CacheManager.michaco.net](https://cachemanager.michaco.net/) benzeri temiz ve güçlü bir API sağlar. Type-safe, flexible ve Spring ecosystem'i ile tam entegre çalışır.

**CacheManager.michaco.net** → **CacheWrapper** 🚀
