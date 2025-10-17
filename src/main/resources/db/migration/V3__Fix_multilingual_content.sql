-- V3: Fix Multilingual Content for Events
-- Simple migration to update a few events with proper translations

UPDATE events SET 
    title = '{"en": "Music Awards Celebrate Talent", "tr": "Muzik Odulleri Yetenegi Kutluyor", "es": "Premios de Musica Celebran el Talento", "de": "Musikpreise Feiern Talent", "fr": "Prix de Musique Celebrent le Talent", "ar": "جوائز الموسيقى تحتفل بالموهبة"}'
WHERE id = 214;

UPDATE events SET 
    title = '{"en": "Anniversary of Landmark Event", "tr": "Onemli Olayin Yildonumu", "es": "Aniversario del Event Historico", "de": "Jahrestag des Meilensteins", "fr": "Anniversaire de l Event Historique", "ar": "ذكرى الحدث التاريخي"}'
WHERE id = 270;
