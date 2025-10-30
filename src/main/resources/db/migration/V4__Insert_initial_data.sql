-- =====================================================
-- V4: Insert Initial Data
-- =====================================================

-- Insert countries
INSERT INTO countries (code, name, flag_url) VALUES
('TR', '{"en": "Turkey"}', '🇹🇷'),
('US', '{"en": "United States"}', '🇺🇸'),
('GB', '{"en": "United Kingdom"}', '🇬🇧'),
('DE', '{"en": "Germany"}', '🇩🇪'),
('FR', '{"en": "France"}', '🇫🇷'),
('ES', '{"en": "Spain"}', '🇪🇸'),
('IT', '{"en": "Italy"}', '🇮🇹'),
('RU', '{"en": "Russia"}', '🇷🇺'),
('CN', '{"en": "China"}', '🇨🇳'),
('JP', '{"en": "Japan"}', '🇯🇵'),
('BR', '{"en": "Brazil"}', '🇧🇷'),
('IN', '{"en": "India"}', '🇮🇳'),
('CA', '{"en": "Canada"}', '🇨🇦'),
('AU', '{"en": "Australia"}', '🇦🇺'),
('MX', '{"en": "Mexico"}', '🇲🇽'),
('KR', '{"en": "South Korea"}', '🇰🇷'),
('SA', '{"en": "Saudi Arabia"}', '🇸🇦'),
('AE', '{"en": "United Arab Emirates"}', '🇦🇪'),
('EG', '{"en": "Egypt"}', '🇪🇬'),
('ALL', '{"en": "Global"}', '🌍');

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
('politics', '{"en": "Politics", "tr": "Politika", "es": "Política", "de": "Politik", "fr": "Politique", "ar": "السياسة"}', '{"en": "Political events, elections, government decisions", "tr": "Siyasi olaylar, seçimler, hükümet kararları", "es": "Eventos políticos, elecciones, decisiones gubernamentales", "de": "Politische Ereignisse, Wahlen, Regierungsentscheidungen", "fr": "Événements politiques, élections, décisions gouvernementales", "ar": "الأحداث السياسية والانتخابات والقرارات الحكومية"}', '🏛️', '#FF6B6B'),
('economy', '{"en": "Economy", "tr": "Ekonomi", "es": "Economía", "de": "Wirtschaft", "fr": "Économie", "ar": "الاقتصاد"}', '{"en": "Economic events, market trends, financial news", "tr": "Ekonomik olaylar, pazar trendleri, finansal haberler", "es": "Eventos económicos, tendencias del mercado, noticias financieras", "de": "Wirtschaftliche Ereignisse, Markttrends, Finanznachrichten", "fr": "Événements économiques, tendances du marché, actualités financières", "ar": "الأحداث الاقتصادية واتجاهات السوق والأخبار المالية"}', '💰', '#4ECDC4'),
('science', '{"en": "Science", "tr": "Bilim", "es": "Ciencia", "de": "Wissenschaft", "fr": "Science", "ar": "العلوم"}', '{"en": "Scientific discoveries, research, technology", "tr": "Bilimsel keşifler, araştırma, teknoloji", "es": "Descubrimientos científicos, investigación, tecnología", "de": "Wissenschaftliche Entdeckungen, Forschung, Technologie", "fr": "Découvertes scientifiques, recherche, technologie", "ar": "الاكتشافات العلمية والبحث والتكنولوجيا"}', '🔬', '#45B7D1'),
('technology', '{"en": "Technology", "tr": "Teknoloji", "es": "Tecnología", "de": "Technologie", "fr": "Technologie", "ar": "التكنولوجيا"}', '{"en": "Tech innovations, digital developments, AI", "tr": "Teknoloji yenilikleri, dijital gelişmeler, yapay zeka", "es": "Innovaciones tecnológicas, desarrollos digitales, IA", "de": "Technische Innovationen, digitale Entwicklungen, KI", "fr": "Innovations technologiques, développements numériques, IA", "ar": "الابتكارات التكنولوجية والتطورات الرقمية والذكاء الاصطناعي"}', '💻', '#96CEB4'),
('environment', '{"en": "Environment", "tr": "Çevre", "es": "Medio Ambiente", "de": "Umwelt", "fr": "Environnement", "ar": "البيئة"}', '{"en": "Climate change, environmental protection, sustainability", "tr": "İklim değişikliği, çevre koruma, sürdürülebilirlik", "es": "Cambio climático, protección ambiental, sostenibilidad", "de": "Klimawandel, Umweltschutz, Nachhaltigkeit", "fr": "Changement climatique, protection de l''environnement, durabilité", "ar": "تغير المناخ وحماية البيئة والاستدامة"}', '🌱', '#FFEAA7'),
('health', '{"en": "Health", "tr": "Sağlık", "es": "Salud", "de": "Gesundheit", "fr": "Santé", "ar": "الصحة"}', '{"en": "Medical breakthroughs, public health, healthcare", "tr": "Tıbbi buluşlar, halk sağlığı, sağlık hizmetleri", "es": "Avances médicos, salud pública, atención médica", "de": "Medizinische Durchbrüche, öffentliche Gesundheit, Gesundheitswesen", "fr": "Percées médicales, santé publique, soins de santé", "ar": "الاكتشافات الطبية والصحة العامة والرعاية الصحية"}', '🏥', '#DDA0DD'),
('education', '{"en": "Education", "tr": "Eğitim", "es": "Educación", "de": "Bildung", "fr": "Éducation", "ar": "التعليم"}', '{"en": "Educational reforms, academic achievements, learning", "tr": "Eğitim reformları, akademik başarılar, öğrenme", "es": "Reformas educativas, logros académicos, aprendizaje", "de": "Bildungsreformen, akademische Leistungen, Lernen", "fr": "Réformes éducatives, réalisations académiques, apprentissage", "ar": "الإصلاحات التعليمية والإنجازات الأكاديمية والتعلم"}', '📚', '#FFB347'),
('culture', '{"en": "Culture", "tr": "Kültür", "es": "Cultura", "de": "Kultur", "fr": "Culture", "ar": "الثقافة"}', '{"en": "Cultural events, arts, traditions", "tr": "Kültürel etkinlikler, sanat, gelenekler", "es": "Eventos culturales, artes, tradiciones", "de": "Kulturelle Veranstaltungen, Künste, Traditionen", "fr": "Événements culturels, arts, traditions", "ar": "الأحداث الثقافية والفنون والتقاليد"}', '🎭', '#FF69B4'),
('sports', '{"en": "Sports", "tr": "Spor", "es": "Deportes", "de": "Sport", "fr": "Sports", "ar": "الرياضة"}', '{"en": "Sports events, competitions, achievements", "tr": "Spor etkinlikleri, yarışmalar, başarılar", "es": "Eventos deportivos, competiciones, logros", "de": "Sportveranstaltungen, Wettkämpfe, Leistungen", "fr": "Événements sportifs, compétitions, réalisations", "ar": "الأحداث الرياضية والمسابقات والإنجازات"}', '⚽', '#32CD32'),
('entertainment', '{"en": "Entertainment", "tr": "Eğlence", "es": "Entretenimiento", "de": "Unterhaltung", "fr": "Divertissement", "ar": "الترفيه"}', '{"en": "Entertainment events, movies, music, shows", "tr": "Eğlence etkinlikleri, filmler, müzik, şovlar", "es": "Eventos de entretenimiento, películas, música, espectáculos", "de": "Unterhaltungsveranstaltungen, Filme, Musik, Shows", "fr": "Événements de divertissement, films, musique, spectacles", "ar": "أحداث الترفيه والأفلام والموسيقى والعروض"}', '🎬', '#FFD700'),
('crime', '{"en": "Crime", "tr": "Suç", "es": "Crimen", "de": "Verbrechen", "fr": "Crime", "ar": "الجريمة"}', '{"en": "Crime news, law enforcement, justice", "tr": "Suç haberleri, kolluk kuvvetleri, adalet", "es": "Noticias de crimen, aplicación de la ley, justicia", "de": "Kriminalnachrichten, Strafverfolgung, Justiz", "fr": "Actualités criminelles, application de la loi, justice", "ar": "أخبار الجريمة وإنفاذ القانون والعدالة"}', '🚨', '#DC143C'),
('disaster', '{"en": "Disaster", "tr": "Felaket", "es": "Desastre", "de": "Katastrophe", "fr": "Catastrophe", "ar": "الكارثة"}', '{"en": "Natural disasters, emergencies, relief efforts", "tr": "Doğal afetler, acil durumlar, yardım çalışmaları", "es": "Desastres naturales, emergencias, esfuerzos de socorro", "de": "Naturkatastrophen, Notfälle, Hilfsmaßnahmen", "fr": "Catastrophes naturelles, urgences, efforts de secours", "ar": "الكوارث الطبيعية والطوارئ وجهود الإغاثة"}', '🌪️', '#8B0000'),
('military', '{"en": "Military", "tr": "Askeri", "es": "Militar", "de": "Militär", "fr": "Militaire", "ar": "عسكري"}', '{"en": "Military operations, defense, security", "tr": "Askeri operasyonlar, savunma, güvenlik", "es": "Operaciones militares, defensa, seguridad", "de": "Militäroperationen, Verteidigung, Sicherheit", "fr": "Opérations militaires, défense, sécurité", "ar": "العمليات العسكرية والدفاع والأمن"}', '🛡️', '#2F4F4F'),
('international', '{"en": "International", "tr": "Uluslararası", "es": "Internacional", "de": "International", "fr": "International", "ar": "دولي"}', '{"en": "International relations, diplomacy, global events", "tr": "Uluslararası ilişkiler, diplomasi, küresel olaylar", "es": "Relaciones internacionales, diplomacia, eventos globales", "de": "Internationale Beziehungen, Diplomatie, globale Ereignisse", "fr": "Relations internationales, diplomatie, événements mondiaux", "ar": "العلاقات الدولية والدبلوماسية والأحداث العالمية"}', '🌍', '#4169E1'),
('social', '{"en": "Social", "tr": "Sosyal", "es": "Social", "de": "Sozial", "fr": "Social", "ar": "اجتماعي"}', '{"en": "Social issues, community events, activism", "tr": "Sosyal konular, topluluk etkinlikleri, aktivizm", "es": "Problemas sociales, eventos comunitarios, activismo", "de": "Soziale Themen, Gemeinschaftsveranstaltungen, Aktivismus", "fr": "Problèmes sociaux, événements communautaires, activisme", "ar": "القضايا الاجتماعية والأحداث المجتمعية والنشاط"}', '👥', '#FF6347'),
('innovation', '{"en": "Innovation", "tr": "İnovasyon", "es": "Innovación", "de": "Innovation", "fr": "Innovation", "ar": "الابتكار"}', '{"en": "Innovation, startups, breakthrough technologies", "tr": "İnovasyon, startuplar, çığır açan teknolojiler", "es": "Innovación, startups, tecnologías revolucionarias", "de": "Innovation, Startups, bahnbrechende Technologien", "fr": "Innovation, startups, technologies révolutionnaires", "ar": "الابتكار والشركات الناشئة والتقنيات الرائدة"}', '💡', '#FF8C00'),
('religion', '{"en": "Religion", "tr": "Din", "es": "Religión", "de": "Religion", "fr": "Religion", "ar": "الدين"}', '{"en": "Religious events, faith, spirituality", "tr": "Dini etkinlikler, inanç, maneviyat", "es": "Eventos religiosos, fe, espiritualidad", "de": "Religiöse Veranstaltungen, Glaube, Spiritualität", "fr": "Événements religieux, foi, spiritualité", "ar": "الأحداث الدينية والإيمان والروحانية"}', '⛪', '#9370DB'),
('transportation', '{"en": "Transportation", "tr": "Ulaşım", "es": "Transporte", "de": "Transport", "fr": "Transport", "ar": "النقل"}', '{"en": "Transportation, infrastructure, mobility", "tr": "Ulaşım, altyapı, mobilite", "es": "Transporte, infraestructura, movilidad", "de": "Transport, Infrastruktur, Mobilität", "fr": "Transport, infrastructure, mobilité", "ar": "النقل والبنية التحتية والتنقل"}', '🚗', '#20B2AA'),
('culture_history', '{"en": "Culture & History", "tr": "Kültür ve Tarih", "es": "Cultura e Historia", "de": "Kultur & Geschichte", "fr": "Culture et Histoire", "ar": "الثقافة والتاريخ"}', '{"en": "Cultural heritage, historical events, traditions", "tr": "Kültürel miras, tarihi olaylar, gelenekler", "es": "Patrimonio cultural, eventos históricos, tradiciones", "de": "Kulturerbe, historische Ereignisse, Traditionen", "fr": "Patrimoine culturel, événements historiques, traditions", "ar": "التراث الثقافي والأحداث التاريخية والتقاليد"}', '🏛️', '#8B4513'),
('other', '{"en": "Other", "tr": "Diğer", "es": "Otro", "de": "Andere", "fr": "Autre", "ar": "أخرى"}', '{"en": "Other events, miscellaneous", "tr": "Diğer olaylar, çeşitli", "es": "Otros eventos, misceláneos", "de": "Andere Ereignisse, Verschiedenes", "fr": "Autres événements, divers", "ar": "أحداث أخرى ومتنوعة"}', '📋', '#808080');