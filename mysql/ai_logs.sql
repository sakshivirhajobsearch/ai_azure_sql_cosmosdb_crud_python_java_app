CREATE DATABASE IF NOT EXISTS ai_azure_sql_cosmosdb;

USE ai_azure_sql_cosmosdb;

CREATE TABLE IF NOT EXISTS ai_azure_sql_cosmosdb_logs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    input_text TEXT NOT NULL,
    prediction VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
