# 🚨 URGENT: Google OAuth API Update Required

## What Changed

The `/api/auth/oauth/google` endpoint now requires a **mandatory header** to identify the client platform.

## Required Action

Add `X-Client-Platform` header to all Google OAuth requests:

```http
X-Client-Platform: web    # For web/backoffice
X-Client-Platform: ios    # For iOS apps
X-Client-Platform: android # For Android apps
```

## Quick Implementation

### Web/React/Vue/Angular

```javascript
fetch("/api/auth/oauth/google", {
  method: "POST",
  headers: {
    "Content-Type": "application/json",
    "X-Client-Platform": "web", // ADD THIS
  },
  body: JSON.stringify({ idToken }),
});
```

### iOS Swift

```swift
request.setValue("ios", forHTTPHeaderField: "X-Client-Platform")
```

### Android Kotlin/Java

```kotlin
.addHeader("X-Client-Platform", "android")
```

### Flutter

```dart
headers: {
  'Content-Type': 'application/json',
  'X-Client-Platform': Platform.isIOS ? 'ios' : 'android',
}
```

### React Native

```javascript
headers: {
  'Content-Type': 'application/json',
  'X-Client-Platform': Platform.OS === 'ios' ? 'ios' : 'android',
}
```

## Error Handling

If header is missing/invalid, API returns 400:

```json
{
  "success": false,
  "message": "Missing X-Client-Platform header (expected: web|ios|android)"
}
```

## Testing

```bash
# Test valid request
curl -X POST /api/auth/oauth/google \
  -H "X-Client-Platform: web" \
  -d '{"idToken":"..."}'

# Test invalid request (should return 400)
curl -X POST /api/auth/oauth/google \
  -d '{"idToken":"..."}'
```

## ⚠️ Important

- Header is **mandatory** - no fallback
- Values must be **lowercase**: `web`, `ios`, `android`
- **All existing requests will fail** without this header

## Deadline

**Update required before next API deployment**

---

_Full integration guide: `CLIENT_INTEGRATION_GUIDE.md`_
