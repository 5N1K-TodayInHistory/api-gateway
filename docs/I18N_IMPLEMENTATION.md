# Çok Dilli (i18n) Event Sistemi

Bu dokümantasyon, API Gateway'deki çok dilli event sisteminin nasıl çalıştığını açıklar.

## Problem

Önceki mimaride şu problemler vardı:

- Event'ler kullanıcının dil tercihine göre filtrelenmiyordu
- Media dosyaları (özellikle ses dosyaları) dil bazlı değildi
- Fallback mekanizması yoktu
- Event servisi ve controller eksikti

## Çözüm

### 1. Geliştirilmiş Media Yapısı

Media dosyaları artık dil bazlı olarak saklanıyor:

```json
{
  "thumbnailUrl": "https://example.com/thumb.jpg",
  "bannerUrl": "https://example.com/banner.jpg",
  "youtubeId": "abc123",
  "audioUrl": "https://example.com/default-audio.mp3",
  "i18n": {
    "tr": {
      "audioUrl": "https://example.com/turkish-audio.mp3"
    },
    "en": {
      "audioUrl": "https://example.com/english-audio.mp3"
    }
  }
}
```

### 2. Event Service

`EventService` sınıfı şu özellikleri sağlar:

- **Dil Bazlı İçerik**: Kullanıcının dil tercihine göre event içeriğini döndürür
- **Fallback Mekanizması**: İstenen dilde içerik yoksa İngilizce'ye, o da yoksa orijinal içeriğe düşer
- **Media Lokalizasyonu**: Ses dosyalarını kullanıcının diline göre ayarlar
- **Pagination**: Sayfalama desteği

### 3. API Endpoints

#### Tüm Event'leri Getir

```http
GET /api/events?page=0&size=20
```

#### Belirli Tarihteki Event'leri Getir

```http
GET /api/events/date?date=2024-01-01&page=0&size=20
```

#### Tarih Aralığındaki Event'leri Getir

```http
GET /api/events/range?startDate=2024-01-01&endDate=2024-01-31&page=0&size=20
```

#### Bugünkü Event'leri Getir

```http
GET /api/events/today?page=0&size=20
```

## Kullanım Senaryosu

### 1. Kullanıcı Kaydı

Kullanıcı kayıt olurken dil tercihini belirler:

```json
{
  "preferences": {
    "language": "tr",
    "selectedCountry": "TR",
    "selectedCategories": ["history", "science"]
  }
}
```

### 2. Event Oluşturma

Admin bir event oluştururken çok dilli içerik ekler:

```json
{
  "title": "New Year Celebration",
  "summary": "Global New Year celebrations",
  "content": "People around the world celebrate...",
  "date": "2024-01-01T00:00:00",
  "category": "HISTORY",
  "country": "ALL",
  "media": {
    "audioUrl": "https://example.com/new-year-audio-en.mp3",
    "i18n": {
      "tr": {
        "audioUrl": "https://example.com/new-year-audio-tr.mp3"
      }
    }
  },
  "i18n": {
    "tr": {
      "title": "Yeni Yıl Kutlaması",
      "summary": "Dünya çapında yeni yıl kutlamaları",
      "content": "Dünya genelinde insanlar yeni bir yılın başlangıcını..."
    }
  }
}
```

### 3. Event Görüntüleme

Türkçe kullanıcı 1 Ocak event'lerini istediğinde:

```http
GET /api/events/date?date=2024-01-01
Authorization: Bearer <token>
```

Sistem otomatik olarak:

1. Kullanıcının dil tercihini (`tr`) alır
2. Event'lerin Türkçe içeriğini döndürür
3. Türkçe ses dosyasını sağlar
4. Eğer Türkçe içerik yoksa İngilizce'ye düşer

## Fallback Stratejisi

1. **Kullanıcının Tercih Ettiği Dil** (örn: `tr`)
2. **Varsayılan Dil** (`en`)
3. **Orijinal İçerik** (event oluşturulurken girilen)

## Veritabanı Yapısı

### Events Tablosu

```sql
CREATE TABLE events (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(500) NOT NULL,
    summary TEXT NOT NULL,
    content TEXT NOT NULL,
    date TIMESTAMPTZ NOT NULL,
    category VARCHAR(50) NOT NULL,
    country VARCHAR(10) NOT NULL,
    media JSONB,           -- Dil bazlı media dosyaları
    engagement JSONB,      -- Beğeni, yorum, paylaşım sayıları
    i18n JSONB,           -- Çok dilli içerik
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
```

### Users Tablosu

```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE,
    username VARCHAR(255) UNIQUE NOT NULL,
    preferences JSONB,     -- Dil tercihi burada
    -- diğer alanlar...
);
```

## Test Senaryoları

Sistem şu durumları test eder:

1. **Türkçe Kullanıcı**: Türkçe içerik döndürülür
2. **İngilizce Kullanıcı**: İngilizce içerik döndürülür
3. **Bilinmeyen Dil**: İngilizce'ye fallback
4. **Dil Tercihi Yok**: Varsayılan İngilizce
5. **Media Lokalizasyonu**: Doğru ses dosyası döndürülür

## Avantajlar

1. **Kullanıcı Deneyimi**: Her kullanıcı kendi dilinde içerik görür
2. **Esneklik**: Yeni diller kolayca eklenebilir
3. **Performans**: Tek sorguda tüm dil verileri gelir
4. **Fallback**: Eksik içerik durumunda sistem çalışmaya devam eder
5. **Media Desteği**: Ses dosyaları da lokalize edilir

## Gelecek Geliştirmeler

1. **Otomatik Çeviri**: Google Translate API entegrasyonu
2. **Daha Fazla Dil**: Arapça, Fransızca, Almanca desteği
3. **Dinamik Dil Değiştirme**: Kullanıcı dil tercihini değiştirebilir
4. **Cache**: Dil bazlı içerik cache'leme
5. **Analytics**: Hangi dillerin daha çok kullanıldığı istatistikleri
