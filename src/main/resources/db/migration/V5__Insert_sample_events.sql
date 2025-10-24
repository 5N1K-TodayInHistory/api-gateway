-- =====================================================
-- V5: Insert Sample Events
-- =====================================================

-- Insert sample events
INSERT INTO events (title, description, content, date, type, country, image_url, video_urls, audio_urls, likes_count, comments_count, month_day, score) VALUES
('{"en": "International Trade Agreement"}', '{"en": "The national budget bill passes after intense debate."}', '{"en": "Officials presented reforms aimed at transparency and voter participation."}', '2024-01-15', 'politics', 'TR', 'https://example.com/image1.jpg', '["https://example.com/video1.mp4"]', '["https://example.com/audio1.mp3"]', 150, 25, '01-15', 85),

('{"en": "Quantum Computing Breakthrough"}', '{"en": "Scientists achieve quantum supremacy with new processor."}', '{"en": "The new quantum processor can solve problems that would take classical computers thousands of years."}', '2024-01-20', 'science', 'US', 'https://example.com/image2.jpg', '["https://example.com/video2.mp4"]', '["https://example.com/audio2.mp3"]', 300, 45, '01-20', 95),

('{"en": "World Cup Final"}', '{"en": "Championship match determines the world champion."}', '{"en": "The final match will be broadcast live to millions of viewers worldwide."}', '2024-02-10', 'sports', 'ALL', 'https://example.com/image3.jpg', '["https://example.com/video3.mp4"]', '["https://example.com/audio3.mp3"]', 500, 80, '02-10', 90),

('{"en": "Historical Monument Restoration"}', '{"en": "Ancient monument restored to its original glory."}', '{"en": "The restoration project took three years and involved international experts."}', '2024-03-05', 'history', 'IT', 'https://example.com/image4.jpg', '["https://example.com/video4.mp4"]', '["https://example.com/audio4.mp3"]', 200, 30, '03-05', 75),

('{"en": "Music Festival"}', '{"en": "Annual music festival featuring international artists."}', '{"en": "The festival will feature over 50 artists from around the world."}', '2024-04-15', 'entertainment', 'GB', 'https://example.com/image5.jpg', '["https://example.com/video5.mp4"]', '["https://example.com/audio5.mp3"]', 400, 60, '04-15', 80);
