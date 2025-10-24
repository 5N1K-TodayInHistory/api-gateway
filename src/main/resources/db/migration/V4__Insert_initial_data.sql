-- =====================================================
-- V4: Insert Initial Data
-- =====================================================

-- Insert countries
INSERT INTO countries (code, name, flag_url) VALUES
('TR', '{"en": "Turkey"}', 'https://flagcdn.com/tr.svg'),
('US', '{"en": "United States"}', 'https://flagcdn.com/us.svg'),
('GB', '{"en": "United Kingdom"}', 'https://flagcdn.com/gb.svg'),
('DE', '{"en": "Germany"}', 'https://flagcdn.com/de.svg'),
('FR', '{"en": "France"}', 'https://flagcdn.com/fr.svg'),
('ES', '{"en": "Spain"}', 'https://flagcdn.com/es.svg'),
('IT', '{"en": "Italy"}', 'https://flagcdn.com/it.svg'),
('RU', '{"en": "Russia"}', 'https://flagcdn.com/ru.svg'),
('CN', '{"en": "China"}', 'https://flagcdn.com/cn.svg'),
('JP', '{"en": "Japan"}', 'https://flagcdn.com/jp.svg'),
('BR', '{"en": "Brazil"}', 'https://flagcdn.com/br.svg'),
('IN', '{"en": "India"}', 'https://flagcdn.com/in.svg'),
('CA', '{"en": "Canada"}', 'https://flagcdn.com/ca.svg'),
('AU', '{"en": "Australia"}', 'https://flagcdn.com/au.svg'),
('MX', '{"en": "Mexico"}', 'https://flagcdn.com/mx.svg'),
('KR', '{"en": "South Korea"}', 'https://flagcdn.com/kr.svg'),
('SA', '{"en": "Saudi Arabia"}', 'https://flagcdn.com/sa.svg'),
('AE', '{"en": "United Arab Emirates"}', 'https://flagcdn.com/ae.svg'),
('EG', '{"en": "Egypt"}', 'https://flagcdn.com/eg.svg'),
('ALL', '{"en": "Global"}', 'https://flagcdn.com/world.svg');

-- Insert languages
INSERT INTO languages (code, name, native_name) VALUES
('en', '{"en": "English"}', 'English'),
('tr', '{"en": "Turkish"}', 'Turkce'),
('es', '{"en": "Spanish"}', 'Espanol'),
('de', '{"en": "German"}', 'Deutsch'),
('fr', '{"en": "French"}', 'Francais'),
('ar', '{"en": "Arabic"}', 'العربية');

-- Insert event types
INSERT INTO event_types (code, name, description, icon_url, color) VALUES
('politics', '{"en": "Politics"}', '{"en": "Political events, elections, government decisions"}', 'https://cdn-icons-png.flaticon.com/512/1828/1828439.png', '#FF6B6B'),
('science', '{"en": "Science"}', '{"en": "Scientific discoveries, research, technology"}', 'https://cdn-icons-png.flaticon.com/512/1828/1828440.png', '#4ECDC4'),
('sports', '{"en": "Sports"}', '{"en": "Sports events, competitions, achievements"}', 'https://cdn-icons-png.flaticon.com/512/1828/1828441.png', '#45B7D1'),
('history', '{"en": "History"}', '{"en": "Historical events, anniversaries, milestones"}', 'https://cdn-icons-png.flaticon.com/512/1828/1828442.png', '#96CEB4'),
('entertainment', '{"en": "Entertainment"}', '{"en": "Entertainment events, movies, music, shows"}', 'https://cdn-icons-png.flaticon.com/512/1828/1828443.png', '#FFEAA7'),
('all', '{"en": "All Events"}', '{"en": "All types of events"}', 'https://cdn-icons-png.flaticon.com/512/1828/1828444.png', '#DDA0DD');