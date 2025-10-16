# Countries API Documentation

This document describes the Countries API endpoints for providing country data to mobile applications.

## Overview

The Countries API provides multilingual country information including country codes, names in multiple languages, flags, and primary languages. The data is stored in a PostgreSQL database with JSONB support for efficient multilingual queries.

## Data Structure

### Country Type (TypeScript)

```typescript
export type Country = {
  code: string; // ISO country code (e.g., "TR", "US")
  name: string; // Country name in requested language
  flag: string; // Flag emoji or URL
  language: string; // Primary language code
};
```

### Multilingual Country Type

```typescript
export type MultilingualCountry = {
  code: string;
  name: { [languageCode: string]: string }; // All available translations
  flag: string;
  language: string;
};
```

## API Endpoints

### 1. Get All Countries (Single Language)

```
GET /api/countries?lang={languageCode}
```

**Parameters:**

- `lang` (optional): Language code (default: "en")

**Response:**

```json
{
  "success": true,
  "data": [
    {
      "code": "TR",
      "name": "Türkiye",
      "flag": "🇹🇷",
      "language": "tr"
    },
    {
      "code": "US",
      "name": "United States",
      "flag": "🇺🇸",
      "language": "en"
    }
  ]
}
```

### 2. Get All Countries (Multilingual)

```
GET /api/countries/multilingual
```

**Response:**

```json
{
  "success": true,
  "data": [
    {
      "code": "TR",
      "name": {
        "en": "Turkey",
        "tr": "Türkiye",
        "es": "Turquía",
        "fr": "Turquie",
        "de": "Türkei"
      },
      "flag": "🇹🇷",
      "language": "tr"
    }
  ]
}
```

### 3. Get Country by Code

```
GET /api/countries/{code}?lang={languageCode}
```

**Parameters:**

- `code`: Country code (e.g., "TR", "US")
- `lang` (optional): Language code (default: "en")

### 4. Get Country by Code (Multilingual)

```
GET /api/countries/{code}/multilingual
```

### 5. Get Countries by Primary Language

```
GET /api/countries/by-language/{language}
```

### 6. Get Countries Supporting a Language

```
GET /api/countries/with-language/{languageCode}
```

### 7. Get Available Languages for a Country

```
GET /api/countries/{code}/languages
```

### 8. Get All Available Languages

```
GET /api/countries/languages
```

## Admin Endpoints (Require Authentication)

### 9. Create Country

```
POST /api/countries
Content-Type: application/json

{
  "code": "FR",
  "name": {
    "en": "France",
    "tr": "Fransa",
    "fr": "France"
  },
  "flag": "🇫🇷",
  "language": "fr"
}
```

### 10. Update Country

```
PUT /api/countries/{code}
Content-Type: application/json

{
  "name": {
    "en": "Updated Name",
    "tr": "Güncellenmiş İsim"
  },
  "flag": "🇫🇷",
  "language": "fr"
}
```

### 11. Delete Country

```
DELETE /api/countries/{code}
```

## Database Schema

### Countries Table

```sql
CREATE TABLE countries (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(3) NOT NULL UNIQUE,
    name JSONB NOT NULL,           -- Multilingual names
    flag VARCHAR(10) NOT NULL,     -- Flag emoji or URL
    language VARCHAR(5) NOT NULL,  -- Primary language code
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

### Indexes

- `idx_countries_code`: On `code` column
- `idx_countries_language`: On `language` column
- `idx_countries_name_gin`: GIN index on `name` JSONB column

## Sample Data

The migration includes sample data for major countries with translations in:

- English (en)
- Turkish (tr)
- Spanish (es)
- French (fr)
- German (de)

## Usage Examples

### Mobile App Integration

```typescript
// Get countries in Turkish
const response = await fetch("/api/countries?lang=tr");
const countries = await response.json();

// Get all countries with all translations
const response = await fetch("/api/countries/multilingual");
const countries = await response.json();

// Get specific country in Spanish
const response = await fetch("/api/countries/TR?lang=es");
const country = await response.json();
```

### Language Fallback

The API automatically falls back to the primary language or English if the requested language is not available for a country.

## Performance Considerations

- Uses PostgreSQL JSONB for efficient multilingual queries
- GIN indexes on JSONB columns for fast language-based searches
- Cached responses recommended for mobile apps
- Consider implementing pagination for large country lists

## Error Handling

All endpoints return consistent error responses:

```json
{
  "success": false,
  "error": "Error message description"
}
```

Common HTTP status codes:

- `200 OK`: Success
- `201 Created`: Resource created
- `404 Not Found`: Country not found
- `409 Conflict`: Country already exists
- `500 Internal Server Error`: Server error
