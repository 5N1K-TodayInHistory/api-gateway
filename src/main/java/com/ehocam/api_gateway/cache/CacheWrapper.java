package com.ehocam.api_gateway.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Generic Cache Wrapper - Spring CacheManager için kolay kullanım wrapper'ı
 * CacheManager.michaco.net benzeri API sağlar
 * 
 * Kullanım örnekleri:
 * - cacheWrapper.get("key", String.class)
 * - cacheWrapper.put("key", "value")
 * - cacheWrapper.addOrUpdate("key", "value", oldValue -> "updated value")
 * - cacheWrapper.getOrCompute("key", () -> expensiveOperation())
 */
@Component
public class CacheWrapper {
    
    private final CacheManager cacheManager;
    
    public CacheWrapper(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }
    
    /**
     * Cache'den değer al
     * @param cacheName Cache adı
     * @param key Cache key
     * @param type Beklenen tip
     * @return Cache'deki değer veya null
     */
    public <T> T get(String cacheName, String key, Class<T> type) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            return null;
        }
        
        Cache.ValueWrapper valueWrapper = cache.get(key);
        if (valueWrapper == null) {
            return null;
        }
        
        Object value = valueWrapper.get();
        if (value != null && type.isAssignableFrom(value.getClass())) {
            return type.cast(value);
        }
        
        return null;
    }
    
    /**
     * Cache'e değer koy
     * @param cacheName Cache adı
     * @param key Cache key
     * @param value Değer
     */
    public void put(String cacheName, String key, Object value) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.put(key, value);
        }
    }
    
    /**
     * Cache'den değer sil
     * @param cacheName Cache adı
     * @param key Cache key
     */
    public void evict(String cacheName, String key) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.evict(key);
        }
    }
    
    /**
     * Tüm cache'i temizle
     * @param cacheName Cache adı
     */
    public void clear(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        }
    }
    
    /**
     * Cache'de key var mı kontrol et
     * @param cacheName Cache adı
     * @param key Cache key
     * @return true eğer key varsa
     */
    public boolean containsKey(String cacheName, String key) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            return false;
        }
        
        return cache.get(key) != null;
    }
    
    /**
     * AddOrUpdate - CacheManager.michaco.net benzeri API
     * Eğer key varsa update function'ı çalıştır, yoksa value'yu koy
     * 
     * @param cacheName Cache adı
     * @param key Cache key
     * @param value Yeni değer (key yoksa kullanılır)
     * @param updateFunction Update function (key varsa çalışır)
     */
    public <T> T addOrUpdate(String cacheName, String key, T value, Function<T, T> updateFunction) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            return value;
        }
        
        Cache.ValueWrapper existingValue = cache.get(key);
        if (existingValue != null) {
            // Key var - update function'ı çalıştır
            @SuppressWarnings("unchecked")
            T existing = (T) existingValue.get();
            T updated = updateFunction.apply(existing);
            cache.put(key, updated);
            return updated;
        } else {
            // Key yok - yeni value'yu koy
            cache.put(key, value);
            return value;
        }
    }
    
    /**
     * GetOrCompute - Cache miss durumunda compute function'ı çalıştır
     * 
     * @param cacheName Cache adı
     * @param key Cache key
     * @param computeFunction Cache miss durumunda çalışacak function
     * @return Cache'deki değer veya compute edilen değer
     */
    public <T> T getOrCompute(String cacheName, String key, Supplier<T> computeFunction) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            return computeFunction.get();
        }
        
        Cache.ValueWrapper valueWrapper = cache.get(key);
        if (valueWrapper != null) {
            @SuppressWarnings("unchecked")
            T cached = (T) valueWrapper.get();
            return cached;
        }
        
        // Cache miss - compute ve cache'e koy
        T computed = computeFunction.get();
        cache.put(key, computed);
        return computed;
    }
    
    /**
     * GetOrCompute with type safety
     * 
     * @param cacheName Cache adı
     * @param key Cache key
     * @param type Beklenen tip
     * @param computeFunction Cache miss durumunda çalışacak function
     * @return Cache'deki değer veya compute edilen değer
     */
    public <T> T getOrCompute(String cacheName, String key, Class<T> type, Supplier<T> computeFunction) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            return computeFunction.get();
        }
        
        Cache.ValueWrapper valueWrapper = cache.get(key);
        if (valueWrapper != null) {
            Object value = valueWrapper.get();
            if (value != null && type.isAssignableFrom(value.getClass())) {
                return type.cast(value);
            }
        }
        
        // Cache miss - compute ve cache'e koy
        T computed = computeFunction.get();
        cache.put(key, computed);
        return computed;
    }
    
    /**
     * Cache statistics - basit istatistikler
     * @param cacheName Cache adı
     * @return Cache istatistikleri
     */
    public CacheStats getStats(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            return new CacheStats(0, 0, 0);
        }
        
        // Spring Cache'de direkt istatistik yok, basit wrapper
        return new CacheStats(0, 0, 0); // TODO: Implement proper stats
    }
    
    /**
     * Cache istatistikleri için basit class
     */
    public static class CacheStats {
        private final long hits;
        private final long misses;
        private final long size;
        
        public CacheStats(long hits, long misses, long size) {
            this.hits = hits;
            this.misses = misses;
            this.size = size;
        }
        
        public long getHits() { return hits; }
        public long getMisses() { return misses; }
        public long getSize() { return size; }
        public double getHitRate() { 
            return hits + misses > 0 ? (double) hits / (hits + misses) : 0.0; 
        }
    }
}
