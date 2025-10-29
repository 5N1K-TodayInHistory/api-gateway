# Client Integration Guide - X-Client-Platform Header

## Overview

The Google OAuth2 endpoint `/api/auth/oauth/google` now requires a platform-specific header to determine which Google client ID to use for token verification. This ensures proper security and platform-specific authentication.

## Required Changes

### 1. Add X-Client-Platform Header

All requests to `/api/auth/oauth/google` must include the `X-Client-Platform` header with one of these values:

- `web` - For web applications and backoffice
- `ios` - For iOS mobile applications
- `android` - For Android mobile applications

### 2. Error Handling

If the header is missing or invalid, the API will return:

```json
{
  "success": false,
  "message": "Missing X-Client-Platform header (expected: web|ios|android)"
}
```

or

```json
{
  "success": false,
  "message": "Invalid X-Client-Platform header. Use one of: web, ios, android"
}
```

## Implementation Examples

### Web Application (React/Next.js/Vue/Angular)

```javascript
// Example for web applications
const loginWithGoogle = async (idToken) => {
  try {
    const response = await fetch("/api/auth/oauth/google", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "X-Client-Platform": "web", // Required header
      },
      body: JSON.stringify({
        idToken: idToken,
      }),
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message);
    }

    const data = await response.json();
    return data;
  } catch (error) {
    console.error("Google OAuth failed:", error.message);
    throw error;
  }
};
```

### iOS Application (Swift)

```swift
// Example for iOS applications
func loginWithGoogle(idToken: String) async throws -> AuthResponse {
    guard let url = URL(string: "https://your-api-domain.com/api/auth/oauth/google") else {
        throw APIError.invalidURL
    }

    var request = URLRequest(url: url)
    request.httpMethod = "POST"
    request.setValue("application/json", forHTTPHeaderField: "Content-Type")
    request.setValue("ios", forHTTPHeaderField: "X-Client-Platform")  // Required header

    let body = ["idToken": idToken]
    request.httpBody = try JSONSerialization.data(withJSONObject: body)

    let (data, response) = try await URLSession.shared.data(for: request)

    guard let httpResponse = response as? HTTPURLResponse else {
        throw APIError.invalidResponse
    }

    if httpResponse.statusCode != 200 {
        let errorResponse = try JSONDecoder().decode(APIErrorResponse.self, from: data)
        throw APIError.serverError(errorResponse.message)
    }

    return try JSONDecoder().decode(APIResponse<AuthResponse>.self, from: data)
}
```

### Android Application (Kotlin/Java)

```kotlin
// Example for Android applications
suspend fun loginWithGoogle(idToken: String): Result<AuthResponse> {
    return try {
        val url = "https://your-api-domain.com/api/auth/oauth/google"
        val requestBody = JSONObject().apply {
            put("idToken", idToken)
        }.toString()

        val request = Request.Builder()
            .url(url)
            .post(requestBody.toRequestBody("application/json".toMediaType()))
            .addHeader("Content-Type", "application/json")
            .addHeader("X-Client-Platform", "android")  // Required header
            .build()

        val response = httpClient.newCall(request).execute()

        if (!response.isSuccessful) {
            val errorBody = response.body?.string()
            val errorResponse = Gson().fromJson(errorBody, APIErrorResponse::class.java)
            return Result.failure(Exception(errorResponse.message))
        }

        val responseBody = response.body?.string()
        val apiResponse = Gson().fromJson(responseBody, APIResponse::class.java)
        Result.success(apiResponse.data)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
```

### Flutter Application (Dart)

```dart
// Example for Flutter applications
Future<AuthResponse> loginWithGoogle(String idToken) async {
  try {
    final response = await http.post(
      Uri.parse('https://your-api-domain.com/api/auth/oauth/google'),
      headers: {
        'Content-Type': 'application/json',
        'X-Client-Platform': 'android', // or 'ios' depending on platform
      },
      body: jsonEncode({
        'idToken': idToken,
      }),
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      return AuthResponse.fromJson(data['data']);
    } else {
      final error = jsonDecode(response.body);
      throw Exception(error['message']);
    }
  } catch (e) {
    throw Exception('Google OAuth failed: $e');
  }
}
```

### React Native Application

```javascript
// Example for React Native applications
const loginWithGoogle = async (idToken) => {
  try {
    const platform = Platform.OS === "ios" ? "ios" : "android";

    const response = await fetch(
      "https://your-api-domain.com/api/auth/oauth/google",
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "X-Client-Platform": platform, // Required header
        },
        body: JSON.stringify({
          idToken: idToken,
        }),
      }
    );

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message);
    }

    const data = await response.json();
    return data.data;
  } catch (error) {
    console.error("Google OAuth failed:", error);
    throw error;
  }
};
```

## Testing

### Test Cases to Implement

1. **Valid requests** with correct headers:

   - `X-Client-Platform: web`
   - `X-Client-Platform: ios`
   - `X-Client-Platform: android`

2. **Invalid requests** that should return 400:
   - Missing `X-Client-Platform` header
   - `X-Client-Platform: mobile` (invalid value)
   - `X-Client-Platform: WEB` (case sensitive, should be lowercase)
   - `X-Client-Platform: ` (empty value)

### cURL Test Examples

```bash
# Valid web request
curl -X POST https://your-api-domain.com/api/auth/oauth/google \
  -H "Content-Type: application/json" \
  -H "X-Client-Platform: web" \
  -d '{"idToken": "your-google-id-token"}'

# Invalid request (missing header)
curl -X POST https://your-api-domain.com/api/auth/oauth/google \
  -H "Content-Type: application/json" \
  -d '{"idToken": "your-google-id-token"}'
# Expected: 400 Bad Request with error message

# Invalid request (wrong header value)
curl -X POST https://your-api-domain.com/api/auth/oauth/google \
  -H "Content-Type: application/json" \
  -H "X-Client-Platform: mobile" \
  -d '{"idToken": "your-google-id-token"}'
# Expected: 400 Bad Request with error message
```

## Migration Checklist

- [ ] Add `X-Client-Platform` header to all Google OAuth requests
- [ ] Update error handling to catch 400 responses for missing/invalid headers
- [ ] Test with all supported platforms (web, ios, android)
- [ ] Update API documentation and integration guides
- [ ] Deploy and verify in staging environment
- [ ] Monitor logs for any header-related errors

## Important Notes

1. **Case Sensitivity**: Header values must be lowercase (`web`, `ios`, `android`)
2. **Required Header**: The header is mandatory - no fallback to default platform
3. **Error Handling**: Always handle 400 responses for missing/invalid headers
4. **Platform Detection**: Use appropriate platform detection logic for cross-platform frameworks
5. **Testing**: Test all platform combinations before production deployment

## Support

If you encounter any issues during implementation, please:

1. Check the API response for specific error messages
2. Verify the header format and value
3. Test with the provided cURL examples
4. Contact the backend team with specific error details
