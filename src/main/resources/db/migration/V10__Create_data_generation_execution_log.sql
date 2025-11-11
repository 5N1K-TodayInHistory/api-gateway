-- =====================================================
-- V10: Create data generation execution log table
-- =====================================================
-- This table tracks data generation executions by date
-- to prevent duplicate data generation for the same date

CREATE TABLE execution_log (
    id BIGSERIAL PRIMARY KEY,
    execution_date DATE NOT NULL,
    job_type VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('IN_PROGRESS', 'SUCCESS', 'FAILED')),
    started_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    completed_at TIMESTAMPTZ,
    error_message TEXT,
    records_processed INTEGER DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Create partial unique index to ensure only one successful execution per date and job type
CREATE UNIQUE INDEX idx_execution_log_unique_success 
ON execution_log(execution_date, job_type) 
WHERE status = 'SUCCESS';

-- Create indexes for fast lookups
CREATE INDEX idx_execution_log_date ON execution_log(execution_date);
CREATE INDEX idx_execution_log_job_type ON execution_log(job_type);
CREATE INDEX idx_execution_log_status ON execution_log(status);
CREATE INDEX idx_execution_log_date_job_type ON execution_log(execution_date, job_type);
CREATE INDEX idx_execution_log_created_at ON execution_log(created_at DESC);

-- Add trigger for updated_at
CREATE TRIGGER update_execution_log_updated_at BEFORE UPDATE ON execution_log
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Add comment to document the table
COMMENT ON TABLE execution_log IS 'Tracks data generation job executions by date to prevent duplicate processing';
COMMENT ON COLUMN execution_log.execution_date IS 'The date for which data was generated';
COMMENT ON COLUMN execution_log.job_type IS 'Type of job (e.g., EVENT_GENERATION, DAILY_SYNC)';
COMMENT ON COLUMN execution_log.status IS 'Execution status: IN_PROGRESS, SUCCESS, or FAILED';
COMMENT ON COLUMN execution_log.records_processed IS 'Number of records processed in this execution';

