package com.example.investmentportfolio.service.impl;

import com.example.investmentportfolio.dto.StatisticDto;
import com.example.investmentportfolio.mapper.StatisticMapper;
import com.example.investmentportfolio.model.*;
import com.example.investmentportfolio.repository.*;
import com.example.investmentportfolio.service.StatisticService;
import com.example.investmentportfolio.util.*;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.example.investmentportfolio.util.Constants.*;

@Service
public class StatisticServiceImpl implements StatisticService {
    private static final Logger LOGGER = LogManager.getLogger(StatisticServiceImpl.class);
    private final StatisticRepository statisticRepository;
    private final StockRepository stockRepository;
    private final ExchangeRepository exchangeRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final DividendRepository dividendRepository;
    private final RateRepository rateRepository;
    private final StatisticMapper statisticMapper;
    private final Validator validator;

    public StatisticServiceImpl(StatisticRepository statisticRepository, StockRepository stockRepository, ExchangeRepository exchangeRepository, UserRepository userRepository, TransactionRepository transactionRepository, DividendRepository dividendRepository, RateRepository rateRepository, StatisticMapper statisticMapper) {
        this.statisticRepository = statisticRepository;
        this.stockRepository = stockRepository;
        this.exchangeRepository = exchangeRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.dividendRepository = dividendRepository;
        this.rateRepository = rateRepository;
        this.statisticMapper = statisticMapper;
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Override
    public StatisticDto createStatistic(StatisticDto statisticDto) {
        Set<ConstraintViolation<StatisticDto>> violations = validator.validate(statisticDto, CreateValidation.class);
        if (!violations.isEmpty()) {
            List<String> errorMessages = violations.stream().map(ConstraintViolation::getMessage).toList();
            LOGGER.error(errorMessages);
            throw new ValidationException(new CustomError(Constants.BAD_REQUEST_ERROR_CODE, errorMessages));
        } else {
            Statistic statistic = statisticMapper.convertToEntity(statisticDto);
            Optional<Long> optionalUserId = userRepository.findIdByUsername(statisticDto.getUsername().toUpperCase());
            if (optionalUserId.isPresent()) {
                statistic.setUserId(optionalUserId.get());
            } else {
                List<String> errorMessages = Collections.singletonList(String.format("No user found with username: %s", statisticDto.getUsername()));
                LOGGER.error(errorMessages);
                throw new NotFoundException(new CustomError(Constants.NOT_FOUND_ERROR_CODE, errorMessages));
            }
            Optional<Long> optionalExchangeId = exchangeRepository.findIdByExchange(statisticDto.getExchange().toUpperCase());
            if (optionalExchangeId.isEmpty()) {
                List<String> errorMessages = Collections.singletonList(String.format("No exchange found with name: %s", statisticDto.getExchange()));
                LOGGER.error(errorMessages);
                throw new NotFoundException(new CustomError(Constants.NOT_FOUND_ERROR_CODE, errorMessages));
            }
            Optional<Long> optionalStockId = stockRepository.findIdByTickerAndExchangeId(statisticDto.getStockTicker().toUpperCase(), optionalExchangeId.get());
            if (optionalStockId.isPresent()) {
                statistic.setStockId(optionalStockId.get());
            } else {
                List<String> errorMessages = Collections.singletonList(String.format("Stock ticker %s cannot be found in exchange: %s", statisticDto.getStockTicker(), statisticDto.getExchange()));
                LOGGER.error(errorMessages);
                throw new NotFoundException(new CustomError(Constants.NOT_FOUND_ERROR_CODE, errorMessages));
            }
            if (statisticRepository.existsByUserIdAndStockId(statistic.getUserId(), statistic.getStockId())) {
                List<String> errorMessages = Collections.singletonList("A statistic for this user with the same ticker and exchange already exists.");
                LOGGER.error(errorMessages);
                throw new AlreadyExistsException(new CustomError(Constants.BAD_REQUEST_ERROR_CODE, errorMessages));
            } else {
                statisticRepository.save(statistic);
                return statisticMapper.convertToDto(statistic);
            }
        }
    }

    @Override
    public List<StatisticDto> getAllStatistics() {
        List<Statistic> statistics = statisticRepository.findAll();
        if (!statistics.isEmpty()) {
            return statistics.stream().map(statistic -> {
                Optional<User> optionalUser = userRepository.findById(statistic.getUserId());
                optionalUser.ifPresent(user -> statistic.setUsername(String.valueOf(user.getUsername())));
                Optional<Stock> optionalStock = stockRepository.findById(statistic.getStockId());
                optionalStock.ifPresent(stock -> statistic.setStockTicker(stock.getStockTicker()));
                Optional<String> optionalExchange = stockRepository.findExchangeByStockId(statistic.getStockId());
                optionalExchange.ifPresent(statistic::setExchange);
                return statisticMapper.convertToDto(statistic);
            }).toList();
        } else {
            List<String> errorMessages = Collections.singletonList("No statistic(s) found.");
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(Constants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
    }

    @Override
    public StatisticDto getStatisticById(Long statisticId) {
        Optional<Statistic> optionalStatistic = statisticRepository.findById(statisticId);
        if (optionalStatistic.isPresent()) {
            Statistic statistic = optionalStatistic.get();
            Optional<User> optionalUser = userRepository.findById(statistic.getUserId());
            optionalUser.ifPresent(user -> statistic.setUsername(String.valueOf(user.getUsername())));
            Optional<Stock> optionalStock = stockRepository.findById(statistic.getStockId());
            optionalStock.ifPresent(stock -> statistic.setStockTicker(stock.getStockTicker()));
            Optional<String> optionalExchange = stockRepository.findExchangeByStockId(statistic.getStockId());
            optionalExchange.ifPresent(statistic::setExchange);
            return statisticMapper.convertToDto(statistic);
        } else {
            List<String> errorMessages = Collections.singletonList(String.format(NO_STATISTIC_FOUND_WITH_ID, statisticId));
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(Constants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
    }

    @Override
    public List<StatisticDto> getStatisticsByUserId(Long userId) {
        List<Statistic> statistics = statisticRepository.findByUserId(userId);
        if (!statistics.isEmpty()) {
            return statistics.stream().map(statistic -> {
                Optional<User> optionalUser = userRepository.findById(statistic.getUserId());
                optionalUser.ifPresent(user -> statistic.setUsername(String.valueOf(user.getUsername())));
                Optional<Stock> optionalStock = stockRepository.findById(statistic.getStockId());
                optionalStock.ifPresent(stock -> statistic.setStockTicker(stock.getStockTicker()));
                Optional<String> optionalExchange = stockRepository.findExchangeByStockId(statistic.getStockId());
                optionalExchange.ifPresent(statistic::setExchange);
                return statisticMapper.convertToDto(statistic);
            }).toList();
        } else {
            List<String> errorMessages = Collections.singletonList(String.format("No statistics found for user id: %d", userId));
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(Constants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
    }

    @Override
    public StatisticDto updateStatisticById(Long statisticId, StatisticDto statisticDto) {
        Optional<Statistic> optionalStatistic = statisticRepository.findById(statisticId);
        if (optionalStatistic.isPresent()) {
            Statistic updatedStatistic = statisticMapper.updateEntityWithDto(statisticDto, optionalStatistic.get());
            Optional<Long> optionalUserId = userRepository.findIdByUsername(statisticDto.getUsername().toUpperCase());
            if (optionalUserId.isPresent()) {
                updatedStatistic.setUserId(optionalUserId.get());
            } else {
                List<String> errorMessages = Collections.singletonList(String.format("No user found with username: %s", statisticDto.getUsername()));
                LOGGER.error(errorMessages);
                throw new NotFoundException(new CustomError(Constants.NOT_FOUND_ERROR_CODE, errorMessages));
            }
            Optional<Long> optionalExchangeId = exchangeRepository.findIdByExchange(statisticDto.getExchange().toUpperCase());
            if (optionalExchangeId.isEmpty()) {
                List<String> errorMessages = Collections.singletonList(String.format("No exchange found with name: %s", statisticDto.getExchange()));
                LOGGER.error(errorMessages);
                throw new NotFoundException(new CustomError(Constants.NOT_FOUND_ERROR_CODE, errorMessages));
            }
            Optional<Long> optionalStockId = stockRepository.findIdByTickerAndExchangeId(statisticDto.getStockTicker().toUpperCase(), optionalExchangeId.get());
            if (optionalStockId.isPresent()) {
                updatedStatistic.setStockId(optionalStockId.get());
            } else {
                List<String> errorMessages = Collections.singletonList(String.format("Stock ticker %s cannot be found in exchange: %s", statisticDto.getStockTicker(), statisticDto.getExchange()));
                LOGGER.error(errorMessages);
                throw new NotFoundException(new CustomError(Constants.NOT_FOUND_ERROR_CODE, errorMessages));
            }
            statisticRepository.save(updatedStatistic);
            return statisticMapper.convertToDto(updatedStatistic);
        } else {
            List<String> errorMessages = Collections.singletonList(String.format(NO_STATISTIC_FOUND_WITH_ID, statisticId));
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(Constants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
    }

    @Override
    @Transactional
    public void deleteAllStatistics() {
        List<Statistic> statistics = statisticRepository.findAll();
        if (!statistics.isEmpty()) {
            statisticRepository.deleteAll();
        } else {
            List<String> errorMessages = Collections.singletonList("No statistic(s) found.");
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(Constants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
    }

    @Override
    @Transactional
    public void deleteStatisticById(Long statisticId) {
        Optional<Statistic> optionalStatistic = statisticRepository.findById(statisticId);
        if (optionalStatistic.isPresent()) {
            statisticRepository.deleteById(statisticId);
        } else {
            List<String> errorMessages = Collections.singletonList(String.format(NO_STATISTIC_FOUND_WITH_ID, statisticId));
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(Constants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
    }

    @Override
    @Transactional
    public void deleteStatisticsByUserId(Long userId) {
        List<Statistic> statistics = statisticRepository.findByUserId(userId);
        if (!statistics.isEmpty()) {
            statisticRepository.deleteByUserId(userId);
        } else {
            List<String> errorMessages = Collections.singletonList(String.format("No statistics found for user id: %d", userId));
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(Constants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
    }

    @Override
    public BigDecimal calculateTotalUnitsOwnedOnGivenDate(Long userId, Long stockId, String date) {
        List<Transaction> transactions = transactionRepository.findByUserIdAndStockIdAndDate(userId, stockId, date);
        String stockTicker = stockRepository.findStockTickerByStockId(stockId);
        BigDecimal totalUnits = transactions.stream().map(transaction -> {
            BigDecimal units = new BigDecimal(transaction.getUnits());
            return "Buy".equalsIgnoreCase(transaction.getTransactionType().trim()) ? units : units.negate();
        }).reduce(BigDecimal.ZERO, BigDecimal::add);
        LOGGER.info("Total Units for {}: {}", stockTicker, totalUnits);
        return totalUnits;
    }

    @Override
    @Transactional
    public void calculateTotalUnits(Long userId) {
        LOGGER.info(Constants.ASTERISK);
        LOGGER.info(Constants.UNITS);
        LOGGER.info(Constants.ASTERISK);
        List<Long> stockIds = stockRepository.findAllStockIdsByUserId(userId);
        if (stockIds.isEmpty()) {
            List<String> errorMessages = Collections.singletonList(String.format(NO_STOCKS_FOUND_FOR_USER_WITH_ID, userId));
            LOGGER.info(errorMessages);
            return;
        }
        stockIds.forEach(stockId -> processStockUnits(userId, stockId));
        LOGGER.info("Total units calculation completed for userId: {}", userId);
    }

    private String getUserDisplayCurrency(Long userId) {
        return userRepository.findById(userId).map(User::getDisplayCurrency).orElseThrow(() -> {
            List<String> errorMessages = Collections.singletonList(String.format(NO_USER_FOUND_WITH_ID, userId));
            LOGGER.error(errorMessages);
            return new NotFoundException(new CustomError(Constants.NOT_FOUND_ERROR_CODE, errorMessages));
        });
    }

    private void processStockUnits(Long userId, Long stockId) {
        BigDecimal stockUnits = calculateTotalUnitsOwnedOnGivenDate(userId, stockId, LocalDate.now().toString());
        if (statisticRepository.existsByUserIdAndStockId(userId, stockId)) {
            statisticRepository.updateUnits(stockUnits, userId, stockId);
        } else {
            saveStatistic(userId, stockId, stockUnits);
        }
    }

    private void saveStatistic(Long userId, Long stockId, BigDecimal stockUnits) {
        Statistic statistic = new Statistic();
        statistic.setUserId(userId);
        statistic.setStockId(stockId);
        statistic.setTotalUnits(String.valueOf(stockUnits));
        statisticRepository.save(statistic);
    }

    @Override
    public BigDecimal calculateTotalCostByStock(Long userId, Long stockId) {
        String displayCurrency = getUserDisplayCurrency(userId);
        Optional<Stock> stock = stockRepository.findById(stockId);
        if (stock.isEmpty()) {
            List<String> errorMessages = Collections.singletonList(INVALID_STOCK);
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(Constants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
        LOGGER.info("Stock Ticker: {}", stock.get().getStockTicker());
        List<Transaction> transactions = transactionRepository.getBuyTransactionsByStock(userId, stockId);
        if (transactions.isEmpty()) {
            List<String> errorMessages = Collections.singletonList(String.format("No buy transactions found with ticker %s for user id: %d", stock.get().getStockTicker(), userId));
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(Constants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
        BigDecimal totalCost = transactions.stream().map(transaction -> calculateTransactionCost(transaction, displayCurrency)).reduce(BigDecimal.ZERO, BigDecimal::add);
        LOGGER.info("Cost: {} ${}", displayCurrency, totalCost.stripTrailingZeros());
        LOGGER.info("");
        return totalCost;
    }

    private BigDecimal calculateTransactionCost(Transaction transaction, String displayCurrency) {
        String currency = transaction.getCurrency();
        BigDecimal rate = getExchangeRate(currency, displayCurrency);
        BigDecimal unitPrice = new BigDecimal(transaction.getUnitPrice());
        BigDecimal units = new BigDecimal(transaction.getUnits());
        BigDecimal fees = new BigDecimal(transaction.getFees());
        return unitPrice.multiply(units).multiply(rate).add(fees);
    }

    private BigDecimal getExchangeRate(String currency, String displayCurrency) {
        if (currency.equals(displayCurrency)) {
            return BigDecimal.ONE;
        }
        String rateName = currency + "/" + displayCurrency;
        return rateRepository.findByRateNameIgnoreCase(rateName).map(rate -> new BigDecimal(rate.getRate())).orElseThrow(() -> {
            List<String> errorMessages = Collections.singletonList(String.format(INVALID_RATE, rateName));
            LOGGER.error(errorMessages);
            return new NotFoundException(new CustomError(Constants.NOT_FOUND_ERROR_CODE, errorMessages));
        });
    }

    @Transactional
    @Override
    public void calculateTotalCost(Long userId) {
        LOGGER.info(Constants.ASTERISK);
        LOGGER.info(Constants.COST);
        LOGGER.info(Constants.ASTERISK);
        String displayCurrency = getUserDisplayCurrency(userId);
        List<Long> stockIds = stockRepository.findAllStockIdsByUserId(userId);
        if (stockIds.isEmpty()) {
            LOGGER.info(String.format(NO_STOCKS_FOUND_FOR_USER_WITH_ID, userId));
            return;
        }
        List<Optional<Stock>> optionalStocks = stockRepository.findByStockIds(stockIds);
        if (optionalStocks.isEmpty()) {
            return; // No stocks to process
        }
        processStocksTotalCost(userId, optionalStocks);
        BigDecimal totalCost = calculateTotalCost(userId, stockIds);
        LOGGER.info("Total Cost: {} ${}", displayCurrency, totalCost.stripTrailingZeros());
    }

    private void processStocksTotalCost(Long userId, List<Optional<Stock>> optionalStocks) {
        for (Optional<Stock> optionalStock : optionalStocks) {
            if (optionalStock.isPresent()) {
                Stock stock = optionalStock.get();
                Long stockId = stock.getStockId();
                BigDecimal totalStockCost = calculateTotalCostByStock(userId, stockId);
                saveOrUpdateTotalCost(userId, stockId, totalStockCost);
            } else {
                List<String> errorMessages = Collections.singletonList(INVALID_STOCK);
                LOGGER.error(errorMessages);
                throw new NotFoundException(new CustomError(Constants.NOT_FOUND_ERROR_CODE, errorMessages));
            }
        }
    }

    private void saveOrUpdateTotalCost(Long userId, Long stockId, BigDecimal totalStockCost) {
        if (statisticRepository.existsByUserIdAndStockId(userId, stockId)) {
            statisticRepository.updateCost(totalStockCost, userId, stockId);
        } else {
            Statistic statistic = new Statistic();
            statistic.setUserId(userId);
            statistic.setStockId(stockId);
            statistic.setTotalCost(String.valueOf(totalStockCost));
            statisticRepository.save(statistic);
        }
    }

    private BigDecimal calculateTotalCost(Long userId, List<Long> stockIds) {
        return stockIds.stream().map(stockId -> statisticRepository.getCost(userId, stockId)).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal calculateTotalValueByStock(Long userId, Long stockId) {
        Stock stock = getStockById(stockId);
        BigDecimal stockUnits = statisticRepository.getStockUnits(userId, stockId);
        BigDecimal lastPrice = stockRepository.findLastPriceByStockId(stockId);
        BigDecimal totalValue = stockUnits.multiply(lastPrice);
        String baseCurrency = stock.getBaseCurrency();
        LOGGER.info("Value: {} ${}", baseCurrency, totalValue.stripTrailingZeros());
        LOGGER.info("");
        return totalValue;
    }

    private Stock getStockById(Long stockId) {
        return stockRepository.findById(stockId).orElseThrow(() -> {
            List<String> errorMessages = Collections.singletonList(INVALID_STOCK);
            LOGGER.error(errorMessages);
            return new NotFoundException(new CustomError(Constants.NOT_FOUND_ERROR_CODE, errorMessages));
        });
    }

    @Transactional
    @Override
    public void calculateTotalValue(Long userId) {
        LOGGER.info(Constants.ASTERISK);
        LOGGER.info(Constants.VALUE);
        LOGGER.info(Constants.ASTERISK);
        String displayCurrency = getUserDisplayCurrency(userId);
        List<Long> stockIds = stockRepository.findAllStockIdsByUserId(userId);
        if (stockIds.isEmpty()) {
            LOGGER.info(String.format(NO_STOCKS_FOUND_FOR_USER_WITH_ID, userId));
            return;
        }
        List<Optional<Stock>> optionalStocks = stockRepository.findByStockIds(stockIds);
        if (optionalStocks.isEmpty()) {
            return;
        }
        processStocksValue(userId, displayCurrency, optionalStocks);
        BigDecimal totalValue = calculateTotalValue(userId, stockIds);
        LOGGER.info("Total Value: {} ${}", displayCurrency, totalValue.stripTrailingZeros());
    }

    private void processStocksValue(Long userId, String displayCurrency, List<Optional<Stock>> optionalStocks) {
        for (Optional<Stock> optionalStock : optionalStocks) {
            if (optionalStock.isPresent()) {
                Stock stock = optionalStock.get();
                Long stockId = stock.getStockId();
                BigDecimal totalStockValue = calculateTotalValueByStock(userId, stockId);
                updateOrSaveStockValue(userId, stockId, totalStockValue, displayCurrency);
            } else {
                List<String> errorMessages = Collections.singletonList(INVALID_STOCK);
                LOGGER.error(errorMessages);
                throw new NotFoundException(new CustomError(Constants.NOT_FOUND_ERROR_CODE, errorMessages));
            }
        }
    }

    private void updateOrSaveStockValue(Long userId, Long stockId, BigDecimal totalStockValue, String displayCurrency) {
        String baseCurrency = stockRepository.findBaseCurrencyByStockId(stockId);
        BigDecimal rate = calculateExchangeRate(displayCurrency, baseCurrency);
        if (statisticRepository.existsByUserIdAndStockId(userId, stockId)) {
            statisticRepository.updateValue(totalStockValue.multiply(rate), userId, stockId);
        } else {
            Statistic statistic = new Statistic();
            statistic.setUserId(userId);
            statistic.setStockId(stockId);
            statistic.setTotalValue(String.valueOf(totalStockValue.multiply(rate)));
            statisticRepository.save(statistic);
        }
    }

    private BigDecimal calculateExchangeRate(String displayCurrency, String baseCurrency) {
        if (!baseCurrency.equals(displayCurrency)) {
            String rateName = baseCurrency + "/" + displayCurrency;
            Optional<Rate> optionalRate = rateRepository.findByRateNameIgnoreCase(rateName);
            if (optionalRate.isPresent()) {
                return new BigDecimal(optionalRate.get().getRate());
            } else {
                List<String> errorMessages = Collections.singletonList(String.format(INVALID_RATE, rateName));
                LOGGER.error(errorMessages);
                throw new NotFoundException(new CustomError(Constants.NOT_FOUND_ERROR_CODE, errorMessages));
            }
        }
        return BigDecimal.ONE;
    }

    private BigDecimal calculateTotalValue(Long userId, List<Long> stockIds) {
        return stockIds.stream().map(stockId -> statisticRepository.getValue(userId, stockId)).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal calculateRealizedProfitsByStock(Long userId, Long stockId) {
        Optional<Stock> stock = stockRepository.findById(stockId);
        String displayCurrency = getUserDisplayCurrency(userId);
        if (stock.isEmpty()) {
            handleInvalidStock();
        } else {
            Stock foundStock = stock.get();
            LOGGER.info(String.format(STOCK_TICKER, foundStock.getStockTicker()));
        }
        List<Transaction> sellTransactions = transactionRepository.getSellTransactionsByStock(userId, stockId);
        if (sellTransactions.isEmpty()) {
            LOGGER.info("Realized Profits: {} ${}", displayCurrency, BigDecimal.ZERO);
            LOGGER.info("");
            return BigDecimal.ZERO;
        } else {
            return calculateTotalRealizedProfits(userId, stockId, displayCurrency, sellTransactions);
        }
    }

    private BigDecimal calculateTotalRealizedProfits(Long userId, Long stockId, String displayCurrency, List<Transaction> sellTransactions) {
        BigDecimal totalStockRealizedProfits = BigDecimal.ZERO;
        for (Transaction sellTransaction : sellTransactions) {
            BigDecimal rate = getRateForTransaction(sellTransaction, displayCurrency);
            BigDecimal unitsSold = new BigDecimal(sellTransaction.getUnits());
            BigDecimal unitSellingPrice = new BigDecimal(sellTransaction.getUnitPrice()).multiply(rate);
            BigDecimal sellingFees = new BigDecimal(sellTransaction.getFees()).multiply(rate);
            List<Transaction> buyTransactions = transactionRepository.getBuyTransactionsByUserIdAndStockIdAndDate(userId, stockId, sellTransaction.getTransactionDate());
            if (buyTransactions.isEmpty()) {
                List<String> errorMessages = Collections.singletonList("There should be a buy transaction before a sell transaction.");
                LOGGER.error(errorMessages);
                throw new GeneralException(new CustomError(Constants.INTERNAL_SERVER_ERROR_ERROR_CODE, errorMessages));
            } else {
                BigDecimal realizedProfits = calculateRealizedProfitsForSellTransaction(buyTransactions, unitsSold, unitSellingPrice, sellingFees);
                totalStockRealizedProfits = totalStockRealizedProfits.add(realizedProfits);
            }
        }
        LOGGER.info("Realized Profits: {} ${}", displayCurrency, totalStockRealizedProfits.stripTrailingZeros());
        LOGGER.info("");
        return totalStockRealizedProfits;
    }

    private BigDecimal getRateForTransaction(Transaction transaction, String displayCurrency) {
        String transactionCurrency = transaction.getCurrency();
        if (!transactionCurrency.equals(displayCurrency)) {
            String rateName = transactionCurrency + "/" + displayCurrency;
            Optional<Rate> optionalRate = rateRepository.findByRateNameIgnoreCase(rateName);
            if (optionalRate.isPresent()) {
                return new BigDecimal(optionalRate.get().getRate());
            } else {
                List<String> errorMessages = Collections.singletonList(String.format(INVALID_RATE, rateName));
                LOGGER.error(errorMessages);
                throw new NotFoundException(new CustomError(Constants.NOT_FOUND_ERROR_CODE, errorMessages));
            }
        }
        return BigDecimal.ONE;
    }

    private BigDecimal calculateRealizedProfitsForSellTransaction(List<Transaction> buyTransactions, BigDecimal unitsSold, BigDecimal unitSellingPrice, BigDecimal sellingFees) {
        BigDecimal totalUnitsBought = BigDecimal.ZERO;
        BigDecimal totalBuyingCost = BigDecimal.ZERO;
        for (Transaction buyTransaction : buyTransactions) {
            totalUnitsBought = totalUnitsBought.add(new BigDecimal(buyTransaction.getUnits()));
            BigDecimal rate = getRateForTransaction(buyTransaction, buyTransaction.getCurrency());
            BigDecimal unitsBought = new BigDecimal(buyTransaction.getUnits());
            BigDecimal unitBuyingPrice = new BigDecimal(buyTransaction.getUnitPrice()).multiply(rate);
            BigDecimal buyingFees = new BigDecimal(buyTransaction.getFees()).multiply(rate);
            totalBuyingCost = totalBuyingCost.add(unitsBought.multiply(unitBuyingPrice)).add(buyingFees);
        }
        BigDecimal averageBuyingPrice = totalUnitsBought.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : totalBuyingCost.divide(totalUnitsBought, 15, RoundingMode.HALF_UP);
        return unitsSold.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : ((((unitsSold.multiply(unitSellingPrice)).subtract(sellingFees)).divide(unitsSold, 15, RoundingMode.HALF_UP)).subtract(averageBuyingPrice)).multiply(unitsSold);
    }

    @Transactional
    @Override
    public void calculateRealizedProfits(Long userId) {
        LOGGER.info(Constants.ASTERISK);
        LOGGER.info(Constants.REALIZED_PROFITS);
        LOGGER.info(Constants.ASTERISK);
        String displayCurrency = getUserDisplayCurrency(userId);
        List<Long> stockIds = stockRepository.findAllStockIdsByUserId(userId);
        if (stockIds.isEmpty()) {
            LOGGER.info(String.format(NO_STOCKS_FOUND_FOR_USER_WITH_ID, userId));
            return;
        }
        List<Optional<Stock>> optionalStocks = stockRepository.findByStockIds(stockIds);
        if (optionalStocks.isEmpty()) {
            return;
        }
        processStocksRealizedProfits(userId, optionalStocks);
        BigDecimal totalRealizedProfits = calculateTotalRealizedProfits(userId, stockIds);
        LOGGER.info("Total Realized Profits: {} ${}", displayCurrency, totalRealizedProfits.stripTrailingZeros());
    }

    private void processStocksRealizedProfits(Long userId, List<Optional<Stock>> optionalStocks) {
        for (Optional<Stock> optionalStock : optionalStocks) {
            if (optionalStock.isPresent()) {
                Stock stock = optionalStock.get();
                Long stockId = stock.getStockId();
                BigDecimal realizedProfits = calculateRealizedProfitsByStock(userId, stockId);
                saveOrUpdateRealizedProfits(userId, stockId, realizedProfits);
            } else {
                List<String> errorMessages = Collections.singletonList(INVALID_STOCK);
                LOGGER.error(errorMessages);
                throw new NotFoundException(new CustomError(Constants.NOT_FOUND_ERROR_CODE, errorMessages));
            }
        }
    }

    private void saveOrUpdateRealizedProfits(Long userId, Long stockId, BigDecimal realizedProfits) {
        if (statisticRepository.existsByUserIdAndStockId(userId, stockId)) {
            statisticRepository.updateRealizedProfits(realizedProfits, userId, stockId);
        } else {
            Statistic statistic = new Statistic();
            statistic.setUserId(userId);
            statistic.setStockId(stockId);
            statistic.setRealizedProfits(String.valueOf(realizedProfits));
            statisticRepository.save(statistic);
        }
    }

    private BigDecimal calculateTotalRealizedProfits(Long userId, List<Long> stockIds) {
        return stockIds.stream().map(stockId -> statisticRepository.getRealizedProfits(userId, stockId)).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal calculateUnrealizedProfitsByStock(Long userId, Long stockId) {
        Stock stock = getStockById(stockId);
        String displayCurrency = getUserDisplayCurrency(userId);
        LOGGER.info(String.format(STOCK_TICKER, stock.getStockTicker()));
        String baseCurrency = stock.getBaseCurrency();
        BigDecimal rate = getExchangeRate(baseCurrency, displayCurrency);
        BigDecimal lastPrice = stockRepository.findLastPriceByStockId(stockId).multiply(rate);
        BigDecimal stockCost = statisticRepository.getCost(userId, stockId);
        BigDecimal units = statisticRepository.getStockUnits(userId, stockId);
        BigDecimal averageBuyingPrice = calculateAverageBuyingPrice(stockCost, units);
        BigDecimal unrealizedProfits = (lastPrice.subtract(averageBuyingPrice)).multiply(units);
        LOGGER.info("Unrealized Profits: {} ${}", displayCurrency, unrealizedProfits.stripTrailingZeros());
        LOGGER.info("");
        return unrealizedProfits;
    }

    private BigDecimal calculateAverageBuyingPrice(BigDecimal stockCost, BigDecimal units) {
        return units.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : stockCost.divide(units, 15, RoundingMode.HALF_UP);
    }

    @Transactional
    @Override
    public void calculateUnrealizedProfits(Long userId) {
        LOGGER.info(Constants.ASTERISK);
        LOGGER.info(Constants.UNREALIZED_PROFITS);
        LOGGER.info(Constants.ASTERISK);
        User user = getUserById(userId);
        String displayCurrency = user.getDisplayCurrency();
        List<Long> stockIds = stockRepository.findAllStockIdsByUserId(userId);
        if (stockIds.isEmpty()) {
            LOGGER.info(String.format(NO_STOCKS_FOUND_FOR_USER_WITH_ID, userId));
            return;
        }
        processUnrealizedProfitsForStocks(userId, stockIds);
        BigDecimal totalUnrealizedProfits = calculateTotalUnrealizedProfits(userId, stockIds);
        LOGGER.info("Total Unrealized Profits: {} ${}", displayCurrency, totalUnrealizedProfits.stripTrailingZeros());
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
            List<String> errorMessages = Collections.singletonList(String.format(NO_USER_FOUND_WITH_ID, userId));
            LOGGER.error(errorMessages);
            return new NotFoundException(new CustomError(Constants.NOT_FOUND_ERROR_CODE, errorMessages));
        });
    }

    private void processUnrealizedProfitsForStocks(Long userId, List<Long> stockIds) {
        List<Stock> stocks = getStocksByIds(stockIds);
        for (Stock stock : stocks) {
            Long stockId = stock.getStockId();
            BigDecimal unrealizedProfits = calculateUnrealizedProfitsByStock(userId, stockId);
            if (statisticRepository.existsByUserIdAndStockId(userId, stockId)) {
                statisticRepository.updateUnrealizedProfits(unrealizedProfits, userId, stockId);
            } else {
                saveNewStatistic(userId, stockId, unrealizedProfits);
            }
        }
    }

    private List<Stock> getStocksByIds(List<Long> stockIds) {
        return stockRepository.findByStockIds(stockIds).stream().flatMap(Optional::stream).toList();
    }

    private void saveNewStatistic(Long userId, Long stockId, BigDecimal unrealizedProfits) {
        Statistic statistic = new Statistic();
        statistic.setUserId(userId);
        statistic.setStockId(stockId);
        statistic.setUnrealizedProfits(String.valueOf(unrealizedProfits));
        statisticRepository.save(statistic);
    }

    private BigDecimal calculateTotalUnrealizedProfits(Long userId, List<Long> stockIds) {
        return stockIds.stream().map(stockId -> statisticRepository.getUnrealizedProfits(userId, stockId)).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal calculateTotalDividendsEarnedByStock(Long userId, Long stockId) {
        Stock stock = getStockById(stockId);
        LOGGER.info(String.format(STOCK_TICKER, stock.getStockTicker()));
        String earliestDate = transactionRepository.getEarliestTransactionDate(userId, stockId);
        String baseCurrency = stock.getBaseCurrency();
        List<Dividend> dividends = dividendRepository.getRelevantDividends(stockId, earliestDate);
        BigDecimal totalDividendsEarned = calculateDividendsForStock(userId, stockId, dividends, baseCurrency);
        LOGGER.info(String.format("Total Dividends Earned: %s $%.2f", baseCurrency, totalDividendsEarned.stripTrailingZeros()));
        LOGGER.info("");
        return totalDividendsEarned;
    }

    private BigDecimal calculateDividendsForStock(Long userId, Long stockId, List<Dividend> dividends, String baseCurrency) {
        return dividends.stream().map(dividend -> calculateDividendForExDate(userId, stockId, dividend, baseCurrency)).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateDividendForExDate(Long userId, Long stockId, Dividend dividend, String baseCurrency) {
        String exDate = dividend.getExDate();
        BigDecimal totalUnits = calculateTotalUnitsOwnedOnGivenDate(userId, stockId, exDate);
        BigDecimal dividendsEarnedExDate = totalUnits.multiply(new BigDecimal(dividend.getPayout()));
        LOGGER.info(String.format("Dividends Earned on ex Date %s: %s $%s", exDate, baseCurrency, dividendsEarnedExDate.stripTrailingZeros()));
        return dividendsEarnedExDate;
    }

    @Override
    @Transactional
    public void calculateTotalDividendsEarned(Long userId) {
        LOGGER.info(Constants.ASTERISK);
        LOGGER.info(Constants.DIVIDENDS);
        LOGGER.info(Constants.ASTERISK);
        String displayCurrency = getUserDisplayCurrency(userId);
        List<Long> stockIds = getStockIdsByUserId(userId);
        if (!stockIds.isEmpty()) {
            List<Stock> dividendStocks = getDividendStocks(stockIds);
            for (Stock stock : dividendStocks) {
                Long stockId = stock.getStockId();
                String baseCurrency = stockRepository.findBaseCurrencyByStockId(stockId);
                BigDecimal dividendsEarned = calculateTotalDividendsEarnedByStock(userId, stockId);
                BigDecimal conversionRate = getExchangeRate(baseCurrency, displayCurrency);
                BigDecimal convertedDividends = dividendsEarned.multiply(conversionRate);
                updateOrSaveStatisticWithDividends(userId, stockId, convertedDividends);
            }
        } else {
            LOGGER.info(String.format(NO_STOCKS_FOUND_FOR_USER_WITH_ID, userId));
        }
        BigDecimal totalDividends = stockIds.stream().map(stockId -> statisticRepository.getDividends(userId, stockId)).reduce(BigDecimal.ZERO, BigDecimal::add);
        LOGGER.info("Total Dividends Earned: {} ${}", displayCurrency, totalDividends.stripTrailingZeros());
        LOGGER.info(Constants.ASTERISK);
    }

    private List<Long> getStockIdsByUserId(Long userId) {
        return stockRepository.findAllStockIdsByUserId(userId);
    }

    private List<Stock> getDividendStocks(List<Long> stockIds) {
        List<Optional<Stock>> optionalStocks = stockRepository.findDividendStocksByIds(stockIds);
        return optionalStocks.stream().filter(Optional::isPresent).map(Optional::get).toList();
    }

    private void updateOrSaveStatisticWithDividends(Long userId, Long stockId, BigDecimal convertedDividends) {
        if (statisticRepository.existsByUserIdAndStockId(userId, stockId)) {
            statisticRepository.updateDividends(convertedDividends, userId, stockId);
        } else {
            Statistic statistic = new Statistic();
            statistic.setUserId(userId);
            statistic.setStockId(stockId);
            statistic.setDividendsEarned(String.valueOf(convertedDividends));
            statisticRepository.save(statistic);
        }
    }

    @Override
    public BigDecimal calculateTotalProfitsByStock(Long userId, Long stockId) {
        String displayCurrency = getUserDisplayCurrency(userId);
        Stock stock = getStockById(stockId);
        LOGGER.info(String.format(STOCK_TICKER, stock.getStockTicker()));
        Statistic statistic = getStatisticByUserIdAndStockId(userId, stockId);
        BigDecimal totalProfits = calculateProfitsFromStatistic(statistic);
        LOGGER.info("Total Profits: {} ${}", displayCurrency, totalProfits.stripTrailingZeros());
        LOGGER.info("");
        return totalProfits;
    }

    private Statistic getStatisticByUserIdAndStockId(Long userId, Long stockId) {
        return statisticRepository.findByUserIdAndStockId(userId, stockId).orElseThrow(() -> {
            List<String> errorMessages = Collections.singletonList(String.format("Error retrieving statistic for stock ID %d and user ID %d", stockId, userId));
            LOGGER.error(errorMessages);
            return new NotFoundException(new CustomError(Constants.NOT_FOUND_ERROR_CODE, errorMessages));
        });
    }

    private BigDecimal calculateProfitsFromStatistic(Statistic statistic) {
        BigDecimal realizedProfits = new BigDecimal(statistic.getRealizedProfits());
        BigDecimal unrealizedProfits = new BigDecimal(statistic.getUnrealizedProfits());
        BigDecimal dividendsEarned = Optional.ofNullable(statistic.getDividendsEarned()).map(BigDecimal::new).orElse(BigDecimal.ZERO);
        return realizedProfits.add(unrealizedProfits).add(dividendsEarned);
    }

    @Transactional
    @Override
    public void calculateTotalProfits(Long userId) {
        LOGGER.info(Constants.ASTERISK);
        LOGGER.info(Constants.TOTAL_PROFITS);
        LOGGER.info(Constants.ASTERISK);
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            handleUserFound(optionalUser.get(), userId);
        } else {
            handleUserNotFound(userId);
        }
    }

    private void handleUserFound(User user, Long userId) {
        String displayCurrency = user.getDisplayCurrency();
        List<Long> stockIds = stockRepository.findAllStockIdsByUserId(userId);
        if (!stockIds.isEmpty()) {
            processStocks(userId, stockIds, displayCurrency);
        } else {
            handleNoStocksFound(userId);
        }
    }

    private void handleUserNotFound(Long userId) {
        List<String> errorMessages = Collections.singletonList(String.format(NO_USER_FOUND_WITH_ID, userId));
        LOGGER.error(errorMessages);
        throw new NotFoundException(new CustomError(Constants.NOT_FOUND_ERROR_CODE, errorMessages));
    }

    private void processStocks(Long userId, List<Long> stockIds, String displayCurrency) {
        List<Optional<Stock>> optionalStocks = stockRepository.findByStockIds(stockIds);
        if (!optionalStocks.isEmpty()) {
            processOptionalStocks(userId, optionalStocks);
            calculateAndLogOverallProfits(userId, stockIds, displayCurrency);
        }
    }

    private void handleNoStocksFound(Long userId) {
        List<String> errorMessages = Collections.singletonList(String.format(NO_STOCKS_FOUND_FOR_USER_WITH_ID, userId));
        LOGGER.info(errorMessages);
    }

    private void processOptionalStocks(Long userId, List<Optional<Stock>> optionalStocks) {
        for (Optional<Stock> optionalStock : optionalStocks) {
            optionalStock.ifPresentOrElse(stock -> processValidStock(userId, stock), this::handleInvalidStock);
        }
    }

    private void processValidStock(Long userId, Stock stock) {
        Long stockId = stock.getStockId();
        BigDecimal totalProfits = calculateTotalProfitsByStock(userId, stockId);
        if (statisticRepository.existsByUserIdAndStockId(userId, stockId)) {
            statisticRepository.updateTotalProfits(totalProfits, userId, stockId);
        } else {
            saveNewStatistic(userId, stockId, totalProfits);
        }
    }

    private void handleInvalidStock() {
        List<String> errorMessages = Collections.singletonList(INVALID_STOCK);
        LOGGER.error(errorMessages);
        throw new NotFoundException(new CustomError(Constants.NOT_FOUND_ERROR_CODE, errorMessages));
    }

    private void calculateAndLogOverallProfits(Long userId, List<Long> stockIds, String displayCurrency) {
        BigDecimal overallProfits = stockIds.stream().map(stockId -> statisticRepository.getTotalProfits(userId, stockId)).reduce(BigDecimal.ZERO, BigDecimal::add);
        LOGGER.info("Total Profits: {} ${}", displayCurrency, overallProfits.stripTrailingZeros());
    }

    @Transactional
    @Override
    public void updateStatisticsForUser(Long userId) {
        LOGGER.info("");
        LOGGER.info(Constants.ASTERISK);
        LOGGER.info(String.format("Statistics for user id: %d", userId));
        calculateTotalUnits(userId);
        calculateTotalCost(userId);
        calculateTotalValue(userId);
        calculateRealizedProfits(userId);
        calculateUnrealizedProfits(userId);
        calculateTotalDividendsEarned(userId);
        calculateTotalProfits(userId);
    }

    @Transactional
    @Override
    public void updateStatisticsForAllUsers() {
        List<Long> userIds = userRepository.findAllUserIds();
        for (Long userId : userIds) {
            updateStatisticsForUser(userId);
        }
    }

    @Transactional
    @Override
    public void updateTotalProfitsForUser(Long userId) {
        calculateTotalProfits(userId);
    }

    @Transactional
    @Override
    public void updateTotalProfitsForAllUsers() {
        List<Long> userIds = userRepository.findAllUserIds();
        for (Long userId : userIds) {
            updateTotalProfitsForUser(userId);
        }
    }
}