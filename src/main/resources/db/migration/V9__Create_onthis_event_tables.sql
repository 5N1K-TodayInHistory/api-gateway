CREATE TABLE onthisday_events (
  id SERIAL PRIMARY KEY,

  date DATE,
  month_day TEXT,
  year INT,
  country TEXT,
  category TEXT,

  title JSONB,
  short_description JSONB,
  importance_reason JSONB,

  hook TEXT,
  why_now TEXT,
  unusual_detail TEXT,

  angle TEXT,
  subtopic TEXT,

  local_relevance INT,
  source_quality INT,
  consensus_sources INT,
  score INT,

  source_urls TEXT[]
);