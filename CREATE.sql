DROP TABLE IF EXISTS Users, Exchanges, Stocks, Rates, Dividends, Statistics, Transactions;

CREATE TABLE Users (
    user_id SERIAL PRIMARY KEY,
    username CITEXT UNIQUE, -- Using CITEXT for case-insensitive unique constraint
    password VARCHAR(255),
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    image_path VARCHAR(255),
    disp_curr CHAR(3)
);
	
CREATE TABLE Exchanges (
    exchange_id SERIAL PRIMARY KEY,
    exchange CITEXT UNIQUE,
    country_code CHAR(2),
    suffix CITEXT UNIQUE
);

CREATE TABLE Stocks (
    stock_id SERIAL PRIMARY KEY,
    stock_ticker VARCHAR(10),
    stock_name CITEXT UNIQUE,
    stock_type VARCHAR(10),
    exchange_id INT,
    last_price DECIMAL(38, 20),
    base_currency CHAR(3),
    div_ind CHAR(1),
    delist_ind CHAR(1),
	CONSTRAINT unique_stock_ticker_exchange UNIQUE (stock_ticker, exchange_id)
);
	
CREATE TABLE Rates (
    rate_id SERIAL PRIMARY KEY,
    rate_name CITEXT UNIQUE,
    rate DECIMAL(38, 20)
);

CREATE TABLE Dividends (
    dividend_id SERIAL PRIMARY KEY,
    stock_id INT,
    exchange_id INT,
    ex_date DATE,
    pay_date DATE,
    payout DECIMAL(38,20)
);

CREATE TABLE Statistics (
    statistic_id SERIAL PRIMARY KEY,
    user_id INT,
    stock_id INT,
    total_units VARCHAR(255),
    total_cost VARCHAR(255),
    total_value VARCHAR(255),
    realized_profits VARCHAR(255),
    unrealized_profits VARCHAR(255),
    dividends_earned VARCHAR(255),
    total_profits VARCHAR(255),
    CONSTRAINT unique_user_stock_id UNIQUE (user_id, stock_id)
);

CREATE TABLE Transactions (
    transaction_id SERIAL PRIMARY KEY,
    user_id INT,
    transaction_date DATE,
    transaction_type CHAR(4),
    stock_id INT,
    units VARCHAR(50),
    unit_price DECIMAL(38, 20),
    fees DECIMAL(38, 20),
    currency CHAR(3)
);