-- =====================================================
-- V3: Add sample events with i18n content and media
-- =====================================================

-- Insert sample events with Turkish and English content
INSERT INTO events (title, summary, content, date, category, country, ratio, media, engagement, i18n) VALUES
(
    'New Year Celebration',
    'Global New Year celebrations',
    'People around the world celebrate the beginning of a new year with various traditions and customs.',
    '2024-01-01 00:00:00+00',
    'HISTORY',
    'ALL',
    90,
    '{
        "thumbnailUrl": "https://example.com/new-year-thumb.jpg",
        "bannerUrl": "https://example.com/new-year-banner.jpg",
        "youtubeId": "abc123",
        "audioUrl": "https://example.com/new-year-audio-en.mp3",
        "i18n": {
            "tr": {
                "audioUrl": "https://example.com/new-year-audio-tr.mp3"
            },
            "en": {
                "audioUrl": "https://example.com/new-year-audio-en.mp3"
            }
        }
    }',
    '{"likes": 0, "comments": 0, "shares": 0}',
    '{
        "tr": {
            "title": "Yeni Yıl Kutlaması",
            "summary": "Dünya çapında yeni yıl kutlamaları",
            "content": "Dünya genelinde insanlar yeni bir yılın başlangıcını çeşitli gelenek ve göreneklerle kutluyorlar."
        },
        "en": {
            "title": "New Year Celebration",
            "summary": "Global New Year celebrations",
            "content": "People around the world celebrate the beginning of a new year with various traditions and customs."
        }
    }'
),
(
    'Scientific Discovery',
    'Breakthrough in renewable energy',
    'Scientists have made a significant breakthrough in renewable energy technology that could revolutionize the industry.',
    '2024-01-15 10:00:00+00',
    'SCIENCE',
    'US',
    85,
    '{
        "thumbnailUrl": "https://example.com/science-thumb.jpg",
        "bannerUrl": "https://example.com/science-banner.jpg",
        "youtubeId": "def456",
        "audioUrl": "https://example.com/science-audio-en.mp3",
        "i18n": {
            "tr": {
                "audioUrl": "https://example.com/science-audio-tr.mp3"
            },
            "en": {
                "audioUrl": "https://example.com/science-audio-en.mp3"
            }
        }
    }',
    '{"likes": 5, "comments": 2, "shares": 1}',
    '{
        "tr": {
            "title": "Bilimsel Keşif",
            "summary": "Yenilenebilir enerjide çığır açan gelişme",
            "content": "Bilim insanları, endüstriyi devrim yaratabilecek yenilenebilir enerji teknolojisinde önemli bir atılım gerçekleştirdiler."
        },
        "en": {
            "title": "Scientific Discovery",
            "summary": "Breakthrough in renewable energy",
            "content": "Scientists have made a significant breakthrough in renewable energy technology that could revolutionize the industry."
        }
    }'
),
(
    'Sports Championship',
    'World Cup final results',
    'The World Cup final has concluded with an exciting match that will be remembered for years to come.',
    '2024-02-01 15:00:00+00',
    'SPORTS',
    'TR',
    80,
    '{
        "thumbnailUrl": "https://example.com/sports-thumb.jpg",
        "bannerUrl": "https://example.com/sports-banner.jpg",
        "youtubeId": "ghi789",
        "audioUrl": "https://example.com/sports-audio-en.mp3",
        "i18n": {
            "tr": {
                "audioUrl": "https://example.com/sports-audio-tr.mp3"
            },
            "en": {
                "audioUrl": "https://example.com/sports-audio-en.mp3"
            }
        }
    }',
    '{"likes": 15, "comments": 8, "shares": 3}',
    '{
        "tr": {
            "title": "Spor Şampiyonası",
            "summary": "Dünya Kupası final sonuçları",
            "content": "Dünya Kupası finali, yıllarca hatırlanacak heyecan verici bir maçla sonuçlandı."
        },
        "en": {
            "title": "Sports Championship",
            "summary": "World Cup final results",
            "content": "The World Cup final has concluded with an exciting match that will be remembered for years to come."
        }
    }'
);

-- Update existing events to have i18n content
UPDATE events 
SET i18n = '{
    "tr": {
        "title": "5N1K''ya Hoş Geldiniz",
        "summary": "Tarihi olaylara açılan kapınız",
        "content": "Bu, 5N1K sistemindeki ilk olaydır. Dünya genelindeki tarihi olayları keşfedebileceğiniz ve öğrenebileceğiniz platformumuza hoş geldiniz."
    },
    "en": {
        "title": "Welcome to 5N1K",
        "summary": "Your gateway to historical events",
        "content": "This is the first event in the 5N1K system. Welcome to our platform where you can discover and learn about historical events from around the world."
    }
}'
WHERE title = 'Welcome to 5N1K';

UPDATE events 
SET i18n = '{
    "tr": {
        "title": "Teknoloji Haberleri",
        "summary": "Teknolojide son gelişmeler",
        "content": "En son teknoloji haberleri ve yeniliklerle güncel kalın."
    },
    "en": {
        "title": "Technology News",
        "summary": "Latest in tech",
        "content": "Stay updated with the latest technology news and innovations."
    }
}'
WHERE title = 'Technology News';

UPDATE events 
SET i18n = '{
    "tr": {
        "title": "Spor Güncellemesi",
        "summary": "Bugün sporda",
        "content": "Spor dünyasından en son güncellemeleri alın."
    },
    "en": {
        "title": "Sports Update",
        "summary": "Today in sports",
        "content": "Get the latest updates from the world of sports."
    }
}'
WHERE title = 'Sports Update';
