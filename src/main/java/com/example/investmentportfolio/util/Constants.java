package com.example.investmentportfolio.util;

import jakarta.validation.ConstraintViolation;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class Constants {
    private Constants() {}

    // General
    public static final String ASTERISK = "********************************************************************";
    public static final String BUY = "Buy";
    public static final String CHART = "chart";
    public static final String COST = "TOTAL COST CALCULATION";
    public static final String CURRENCY = "currency";
    public static final String DIVIDENDS = "TOTAL DIVIDENDS CALCULATION";
    public static final String EMPTY_STRING = "";
    public static final String GET = "GET";
    public static final String META = "meta";
    public static final String REALIZED_PROFITS = "REALIZED PROFITS CALCULATION";
    public static final String REGULAR_MARKET_PRICE = "regularMarketPrice";
    public static final String RESULT = "result";
    public static final String SLASH = "/";
    public static final String TOTAL_PROFITS = "TOTAL PROFITS CALCULATION";
    public static final String UNITS = "TOTAL UNITS CALCULATION";
    public static final String UNREALIZED_PROFITS = "UNREALIZED PROFITS CALCULATION";
    public static final String USER = "USER";
    public static final String VALUE = "TOTAL VALUE CALCULATION";
    public static final String YAHOO_FINANCE_URL = "https://query1.finance.yahoo.com/v8/finance/chart/";

    // Error Codes
    public static final String BAD_REQUEST_ERROR_CODE = "400 Bad Request";
    public static final String UNAUTHORIZED_ERROR_CODE = "401 Unauthorized";
    public static final String FORBIDDEN_ERROR_CODE = "403 Forbidden";
    public static final String NOT_FOUND_ERROR_CODE = "404 Not Found";
    public static final String INTERNAL_SERVER_ERROR_ERROR_CODE = "500 Internal Server Error";

    // Security
    public static final String FORBIDDEN_ERROR_MESSAGE = "You do not have permission to access this resource.";
    public static final String GENERATE_REFRESH_TOKEN_MESSAGE = "Generated a new refresh token.";
    public static final String INVALID_CREDENTIALS_ERROR_MESSAGE = "Invalid Credentials. Please try again.";
    public static final String SESSION_EXPIRED_MESSAGE = "Your session has expired. Please log in again.";
    public static final String SUCCESSFUL_REGISTRATION_MESSAGE = "You’re all set! Your registration is complete, and you can proceed to log in.";
    public static final String USERNAME_TAKEN_ERROR_MESSAGE = "This username is already taken. Please try again.";
    public static final String VALID_REFRESH_TOKEN_MESSAGE = "Refresh token is still valid; continuing to use the current token.";

    // Logging
    public static final String COST_LOG = "Cost: {} ${}";
    public static final String DIVIDENDS_EARNED_ON_EX_DATE_LOG = "Dividends Earned on ex Date %s: %s $%s";
    public static final String REALIZED_PROFITS_LOG = "Realized Profits: {} ${}";
    public static final String STATISTICS_FOR_USER_WITH_ID = "Statistics for user id: %d";
    public static final String STOCK_TICKER_LOG = "Stock Ticker: {}";
    public static final String THREE_PLACEHOLDERS_LOG = "{}: {} {}";
    public static final String TOTAL_COST_LOG = "Total Cost: {} ${}";
    public static final String TOTAL_DIVIDENDS_EARNED_2F_LOG = "Total Dividends Earned: %s $%.2f";
    public static final String TOTAL_DIVIDENDS_EARNED_LOG = "Total Dividends Earned: {} ${}";
    public static final String TOTAL_PROFITS_LOG = "Total Profits: {} ${}";
    public static final String TOTAL_REALIZED_PROFITS_LOG = "Total Realized Profits: {} ${}";
    public static final String TOTAL_UNITS_FOR_USER_LOG = "Total units calculation completed for userId: {}";
    public static final String TOTAL_UNITS_LOG = "Total Units for {}: {}";
    public static final String TOTAL_UNREALIZED_PROFITS_LOG = "Total Unrealized Profits: {} ${}";
    public static final String TOTAL_VALUE_LOG = "Value: {} ${}";
    public static final String UNREALIZED_PROFITS_LOG = "Unrealized Profits: {} ${}";
    public static final String VALUE_LOG = "Value: {} ${}";

    // Dividend
    public static final String DIVIDEND_WITH_SAME_EX_OR_PAY_DATE_EXISTS = "A dividend with the same ex date or pay date already exists.";
    public static final String NO_DIVIDEND_FOUND_WITH_ID = "No dividend found with id: %d";
    public static final String NO_DIVIDENDS_FOUND = "No dividend(s) found.";

    // Exchange
    public static final String EXCHANGE_SAME_NAME_OR_SUFFIX = "An exchange with the same name or suffix already exists.";
    public static final String NO_EXCHANGE_FOUND_WITH_ID = "No exchange found with id: %d";
    public static final String NO_EXCHANGE_FOUND_WITH_NAME = "No exchange found with name: %s";
    public static final String NO_EXCHANGE_FOUND_WITH_STOCK_ID = "No exchange found with stock id: %d";
    public static final String NO_EXCHANGE_FOUND_WITH_SUFFIX = "No exchange found with suffix: %s";
    public static final String NO_EXCHANGES_FOUND = "No exchange(s) found.";
    public static final String NO_EXCHANGES_FOUND_WITH_COUNTRY_CODE = "No exchange(s) found with country code: %s";

    // Rate
    public static final String NO_RATE_FOUND_WITH_ID = "No rate found with id: %d";
    public static final String NO_RATE_FOUND_WITH_NAME = "No rate found with name: %s";
    public static final String NO_RATES_FOUND = "No rate(s) found.";
    public static final String RATE_ALREADY_EXISTS = "A rate with the same name already exists.";

    // Statistic
    public static final String ERROR_RETRIEVING_STATISTICS_FOR_STOCK_WITH_ID_AND_USER_WITH_ID = "Error retrieving statistic for stock ID %d and user id %d";
    public static final String NO_STATISTIC_FOUND_WITH_ID = "No statistic found with id: %d";
    public static final String NO_STATISTICS_FOUND = "No statistic(s) found.";
    public static final String NO_STATISTICS_FOUND_FOR_USER_WITH_ID = "No statistics found for user id: %d";
    public static final String STATISTIC_FOR_USER_WITH_SAME_TICKER_AND_EXCHANGE_ALREADY_EXISTS = "A statistic for this user with the same ticker and exchange already exists.";

    // Stock
    public static final String INVALID_STOCK = "Invalid stock";
    public static final String NO_STOCK_FOUND_IN_EXCHANGE = "Stock with ticker %s cannot be found in exchange: %s";
    public static final String NO_STOCK_FOUND_WITH_ID = "No stock found with id: %d";
    public static final String NO_STOCK_FOUND_WITH_TICKER = "No stock found with ticker: %s";
    public static final String NO_STOCK_FOUND_WITH_WITH_STOCK_IDS = "No stocks found with provided stock ids.";
    public static final String NO_STOCKS_FOUND = "No stock(s) found.";
    public static final String NO_STOCKS_FOUND_FOR_USER_WITH_ID = "No stocks found for user with id: %d";
    public static final String NO_STOCKS_FOUND_WITH_FILTERS = "No stocks were found matching the provided filters.";
    public static final String STOCK_TICKER = "Stock Ticker: %s";
    public static final String STOCK_TICKER_NOT_FOUND_IN_EXCHANGE = "Stock ticker %s cannot be found in exchange: %s";
    public static final String STOCK_WITH_SAME_TICKER_ALREADY_EXISTS = "A stock with the same ticker already exists.";

    // Transaction
    public static final String NO_BUY_TRANSACTION_BEFORE_SELL_TRANSACTION = "There should be a buy transaction before a sell transaction.";
    public static final String NO_BUY_TRANSACTION_FOUND_FOR_USER_WITH_ID = "No buy transactions found with ticker %s for user id: %d";
    public static final String NO_TRANSACTION_FOUND_FOR_USER_WITH_ID = "No transactions found for user id: %d";
    public static final String NO_TRANSACTION_FOUND_WITH_ID = "No transaction found with id: %d";
    public static final String NO_TRANSACTIONS_FOUND = "No transaction(s) found.";

    // User
    public static final String NO_USER_FOUND_WITH_ID = "No user found with id: %d";
    public static final String NO_USER_FOUND_WITH_USERNAME = "No user found with username: %s";
    public static final String NO_USERS_FOUND = "No user(s) found.";

    // Exception Methods
    public static AlreadyExistsException returnAlreadyExistsException(Logger logger, String errorMessage, Object... placeholders) {
        List<String> errorMessages = Collections.singletonList(String.format(errorMessage, placeholders));
        logger.error(errorMessages);
        return new AlreadyExistsException(new CustomError(Constants.BAD_REQUEST_ERROR_CODE, errorMessages));
    }

    public static GeneralException returnGeneralException(Logger logger, String errorMessage, Object... placeholders) {
        List<String> errorMessages = Collections.singletonList(String.format(errorMessage, placeholders));
        logger.error(errorMessages);
        return new GeneralException(new CustomError(Constants.INTERNAL_SERVER_ERROR_ERROR_CODE, errorMessages));
    }

    public static NotFoundException returnNotFoundException(Logger logger, String errorMessage, Object... placeholders) {
        List<String> errorMessages = Collections.singletonList(String.format(errorMessage, placeholders));
        logger.error(errorMessages);
        return new NotFoundException(new CustomError(Constants.NOT_FOUND_ERROR_CODE, errorMessages)); // returning instead of directly throwing for jacoco coverage purposes
    }

    public static <T> ValidationException returnValidationException(Logger logger, Set<ConstraintViolation<T>> violations) {
        List<String> errorMessages = violations.stream().map(ConstraintViolation::getMessage).toList();
        logger.error(errorMessages);
        return new ValidationException(new CustomError(Constants.BAD_REQUEST_ERROR_CODE, errorMessages));
    }
}