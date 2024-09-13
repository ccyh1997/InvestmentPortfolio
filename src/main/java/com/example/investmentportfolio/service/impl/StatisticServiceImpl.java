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
import java.util.*;

@Service
public class StatisticServiceImpl implements StatisticService {
    private static final Logger LOGGER = LogManager.getLogger(StatisticServiceImpl.class);
    public static final String STOCK_TICKER = "Stock Ticker: %s";
    public static final String INVALID_STOCK = "Invalid stock";
    public static final String INVALID_RATE = "Invalid rate: %s";
    public static final String NO_USER_FOUND_WITH_ID = "No user found with id: %d";
    public static final String NO_STATISTIC_FOUND_WITH_ID = "No statistic found with id: %d";
    public static final String NO_STOCKS_FOUND_FOR_USER_WITH_ID = "No stocks found for user with id: %d";
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
            List<String> errorMessages = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .toList();
            LOGGER.error(errorMessages);
            throw new ValidationException(new CustomError(ErrorConstants.BAD_REQUEST_ERROR_CODE, errorMessages));
        } else {
            Statistic statistic = statisticMapper.convertToEntity(statisticDto);
            Optional<Long> optionalUserId = userRepository.findIdByUsername(statisticDto.getUsername().toUpperCase());
            if (optionalUserId.isPresent()) {
                statistic.setUserId(optionalUserId.get());
            } else {
                List<String> errorMessages = Collections.singletonList(String.format("No user found with username: %s", statisticDto.getUsername()));
                LOGGER.error(errorMessages);
                throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
            }
            Optional<Long> optionalExchangeId = exchangeRepository.findIdByExchange(statisticDto.getExchange().toUpperCase());
            if (optionalExchangeId.isEmpty()) {
                List<String> errorMessages = Collections.singletonList(String.format("No exchange found with name: %s", statisticDto.getExchange()));
                LOGGER.error(errorMessages);
                throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
            }
            Optional<Long> optionalStockId = stockRepository.findIdByTickerAndExchangeId(statisticDto.getStockTicker().toUpperCase(), optionalExchangeId.get());
            if (optionalStockId.isPresent()) {
                statistic.setStockId(optionalStockId.get());
            } else {
                List<String> errorMessages = Collections.singletonList(String.format("Stock ticker %s cannot be found in exchange: %s", statisticDto.getStockTicker(), statisticDto.getExchange()));
                LOGGER.error(errorMessages);
                throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
            }
            if (statisticRepository.existsByUserIdAndStockId(statistic.getUserId(), statistic.getStockId())) {
                List<String> errorMessages = Collections.singletonList("A statistic for this user with the same ticker and exchange already exists.");
                LOGGER.error(errorMessages);
                throw new AlreadyExistsException(new CustomError(ErrorConstants.BAD_REQUEST_ERROR_CODE, errorMessages));
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
            return statistics.stream()
                    .map(statistic -> {
                        Optional<User> optionalUser = userRepository.findById(statistic.getUserId());
                        optionalUser.ifPresent(user -> statistic.setUsername(String.valueOf(user.getUsername())));
                        Optional<Stock> optionalStock = stockRepository.findById(statistic.getStockId());
                        optionalStock.ifPresent(stock -> statistic.setStockTicker(stock.getStockTicker()));
                        Optional<String> optionalExchange = stockRepository.findExchangeByStockId(statistic.getStockId());
                        optionalExchange.ifPresent(statistic::setExchange);
                        return statisticMapper.convertToDto(statistic);
                    })
                    .toList();
        } else {
            List<String> errorMessages = Collections.singletonList("No statistic(s) found.");
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
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
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
    }

    @Override
    public List<StatisticDto> getStatisticsByUserId(Long userId) {
        List<Statistic> statistics = statisticRepository.findByUserId(userId);
        if (!statistics.isEmpty()) {
            return statistics.stream()
                    .map(statistic -> {
                        Optional<User> optionalUser = userRepository.findById(statistic.getUserId());
                        optionalUser.ifPresent(user -> statistic.setUsername(String.valueOf(user.getUsername())));
                        Optional<Stock> optionalStock = stockRepository.findById(statistic.getStockId());
                        optionalStock.ifPresent(stock -> statistic.setStockTicker(stock.getStockTicker()));
                        Optional<String> optionalExchange = stockRepository.findExchangeByStockId(statistic.getStockId());
                        optionalExchange.ifPresent(statistic::setExchange);
                        return statisticMapper.convertToDto(statistic);
                    })
                    .toList();
        } else {
            List<String> errorMessages = Collections.singletonList(String.format("No statistics found for user id: %d", userId));
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
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
                throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
            }
            Optional<Long> optionalExchangeId = exchangeRepository.findIdByExchange(statisticDto.getExchange().toUpperCase());
            if (optionalExchangeId.isEmpty()) {
                List<String> errorMessages = Collections.singletonList(String.format("No exchange found with name: %s", statisticDto.getExchange()));
                LOGGER.error(errorMessages);
                throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
            }
            Optional<Long> optionalStockId = stockRepository.findIdByTickerAndExchangeId(statisticDto.getStockTicker().toUpperCase(), optionalExchangeId.get());
            if (optionalStockId.isPresent()) {
                updatedStatistic.setStockId(optionalStockId.get());
            } else {
                List<String> errorMessages = Collections.singletonList(String.format("Stock ticker %s cannot be found in exchange: %s", statisticDto.getStockTicker(), statisticDto.getExchange()));
                LOGGER.error(errorMessages);
                throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
            }
            statisticRepository.save(updatedStatistic);
            return statisticMapper.convertToDto(updatedStatistic);
        } else {
            List<String> errorMessages = Collections.singletonList(String.format(NO_STATISTIC_FOUND_WITH_ID, statisticId));
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
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
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
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
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
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
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
    }

    @Override
    public BigDecimal calculateTotalUnitsOwnedOnGivenDate(Long userId, Long stockId, String date) {
        List<Transaction> transactions = transactionRepository.findByUserIdAndStockIdAndDate(userId, stockId, date);
        String stockTicker = stockRepository.findStockTickerByStockId(stockId);
        BigDecimal totalUnits = transactions.stream()
                .map(transaction -> {
                    BigDecimal units = new BigDecimal(transaction.getUnits());
                    return Objects.equals(transaction.getTransactionType().trim(), "Buy") ? units : units.negate();
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        LOGGER.info("Total Units for {}: {}", stockTicker, totalUnits);
        return totalUnits;
    }

    @Override
    @Transactional
    public void calculateTotalUnits(Long userId) {
        LOGGER.info(Constants.ASTERISK);
        LOGGER.info(Constants.UNITS);
        LOGGER.info(Constants.ASTERISK);
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            List<Long> stockIds = stockRepository.findAllStockIdsByUserId(userId);
            if (!stockIds.isEmpty()) {
                for (Long stockId : stockIds) {
                    BigDecimal stockUnits = calculateTotalUnitsOwnedOnGivenDate(userId, stockId, LocalDate.now().toString());
                    if (statisticRepository.existsByUserIdAndStockId(userId, stockId)) {
                        statisticRepository.updateUnits(stockUnits, userId, stockId);
                    } else {
                        Statistic statistic = new Statistic();
                        statistic.setUserId(userId);
                        statistic.setStockId(stockId);
                        statistic.setTotalUnits(String.valueOf(stockUnits));
                        statisticRepository.save(statistic);
                    }
                }
            } else {
                List<String> errorMessages = Collections.singletonList(String.format(NO_STOCKS_FOUND_FOR_USER_WITH_ID, userId));
                LOGGER.info(errorMessages);
            }
        } else {
            List<String> errorMessages = Collections.singletonList(String.format(NO_USER_FOUND_WITH_ID, userId));
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
    }

    @Override
    public BigDecimal calculateTotalCostByStock(Long userId, Long stockId) {
        BigDecimal totalCost = BigDecimal.ZERO;
        String displayCurrency;
        String currency;
        Optional<Stock> stock = stockRepository.findById(stockId);
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            displayCurrency = optionalUser.get().getDisplayCurrency();
        } else {
            List<String> errorMessages = Collections.singletonList(String.format(NO_USER_FOUND_WITH_ID, userId));
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
        if (stock.isPresent()) {
            LOGGER.info("Stock Ticker: {}", stock.get().getStockTicker());
            List<Transaction> transactions = transactionRepository.getBuyTransactionsByStock(userId, stockId);
            if (!transactions.isEmpty()) {
                for (Transaction transaction : transactions) {
                    currency = transaction.getCurrency();
                    BigDecimal rate = BigDecimal.ONE;
                    if (!currency.equals(displayCurrency)) {
                        String rateName = currency + "/" + displayCurrency;
                        Optional<Rate> optionalRate = rateRepository.findByRateNameIgnoreCase(rateName);
                        if (optionalRate.isPresent()) {
                            rate = new BigDecimal(optionalRate.get().getRate());
                        } else {
                            List<String> errorMessages = Collections.singletonList(String.format(INVALID_RATE, rateName));
                            LOGGER.error(errorMessages);
                            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
                        }
                    }
                    BigDecimal unitPrice = new BigDecimal(transaction.getUnitPrice());
                    BigDecimal units = new BigDecimal(transaction.getUnits());
                    BigDecimal fees = new BigDecimal(transaction.getFees());
                    totalCost = totalCost.add(unitPrice.multiply(units).multiply(rate)).add(fees);
                }
            } else {
                List<String> errorMessages = Collections.singletonList(String.format("No buy transactions found with ticker %s for user id: %d", stock.get().getStockTicker(), userId));
                LOGGER.error(errorMessages);
                throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
            }
            LOGGER.info("Cost: {} ${}", displayCurrency, totalCost.stripTrailingZeros());
            LOGGER.info("");
            return totalCost;
        } else {
            List<String> errorMessages = Collections.singletonList(INVALID_STOCK);
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
    }

    @Transactional
    @Override
    public void calculateTotalCost(Long userId) {
        LOGGER.info(Constants.ASTERISK);
        LOGGER.info(Constants.COST);
        LOGGER.info(Constants.ASTERISK);
        BigDecimal totalCost;
        String displayCurrency;
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            displayCurrency = optionalUser.get().getDisplayCurrency();
            List<Long> stockIds = stockRepository.findAllStockIdsByUserId(userId);
            if (!stockIds.isEmpty()) {
                List<Optional<Stock>> optionalStocks = stockRepository.findByStockIds(stockIds);
                if (!optionalStocks.isEmpty()) {
                    for (Optional<Stock> optionalStock : optionalStocks) {
                        if (optionalStock.isPresent()) {
                            Stock stock = optionalStock.get();
                            Long stockId = stock.getStockId();
                            BigDecimal totalStockCost = calculateTotalCostByStock(userId, stockId);
                            if (statisticRepository.existsByUserIdAndStockId(userId, stockId)) {
                                statisticRepository.updateCost(totalStockCost, userId, stockId);
                            } else {
                                Statistic statistic = new Statistic();
                                statistic.setUserId(userId);
                                statistic.setStockId(stockId);
                                statistic.setTotalCost(String.valueOf(totalStockCost));
                                statisticRepository.save(statistic);
                            }
                        } else {
                            List<String> errorMessages = Collections.singletonList(INVALID_STOCK);
                            LOGGER.error(errorMessages);
                            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
                        }
                    }
                }
            } else {
                List<String> errorMessages = Collections.singletonList(String.format(NO_STOCKS_FOUND_FOR_USER_WITH_ID, userId));
                LOGGER.info(errorMessages);
            }
            totalCost = stockIds.stream()
                    .map(stockId -> statisticRepository.getCost(userId, stockId))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            LOGGER.info("Total Cost: {} ${}", displayCurrency, totalCost.stripTrailingZeros());
        } else {
            List<String> errorMessages = Collections.singletonList(String.format(NO_USER_FOUND_WITH_ID, userId));
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
    }

    @Override
    public BigDecimal calculateTotalValueByStock(Long userId, Long stockId) {
        Optional<Stock> stock = stockRepository.findById(stockId);
        if (stock.isPresent()) {
            LOGGER.info(String.format(STOCK_TICKER, stock.get().getStockTicker()));
            BigDecimal stockUnits = statisticRepository.getStockUnits(userId, stockId);
            BigDecimal lastPrice = stockRepository.findLastPriceByStockId(stockId);
            BigDecimal totalValue = stockUnits.multiply(lastPrice);
            String baseCurrency = stock.get().getBaseCurrency();
            LOGGER.info("Value: {} ${}", baseCurrency, totalValue.stripTrailingZeros());
            LOGGER.info("");
            return totalValue;
        } else {
            List<String> errorMessages = Collections.singletonList(INVALID_STOCK);
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
    }

    @Transactional
    @Override
    public void calculateTotalValue(Long userId) {
        LOGGER.info(Constants.ASTERISK);
        LOGGER.info(Constants.VALUE);
        LOGGER.info(Constants.ASTERISK);
        BigDecimal totalValue;
        String displayCurrency;
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            displayCurrency = optionalUser.get().getDisplayCurrency();
            List<Long> stockIds = stockRepository.findAllStockIdsByUserId(userId);
            if (!stockIds.isEmpty()) {
                List<Optional<Stock>> optionalStocks = stockRepository.findByStockIds(stockIds);
                if (!optionalStocks.isEmpty()) {
                    for (Optional<Stock> optionalStock : optionalStocks) {
                        if (optionalStock.isPresent()) {
                            Stock stock = optionalStock.get();
                            Long stockId = stock.getStockId();
                            String baseCurrency = stockRepository.findBaseCurrencyByStockId(stockId);
                            BigDecimal totalStockValue = calculateTotalValueByStock(userId, stockId);
                            BigDecimal rate = BigDecimal.ONE;
                            if (!baseCurrency.equals(displayCurrency)) {
                                String rateName = baseCurrency + "/" + displayCurrency;
                                Optional<Rate> optionalRate = rateRepository.findByRateNameIgnoreCase(rateName);
                                if (optionalRate.isPresent()) {
                                    rate = new BigDecimal(optionalRate.get().getRate());
                                } else {
                                    List<String> errorMessages = Collections.singletonList(String.format(INVALID_RATE, rateName));
                                    LOGGER.error(errorMessages);
                                    throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
                                }
                            }
                            if (statisticRepository.existsByUserIdAndStockId(userId, stockId)) {
                                statisticRepository.updateValue(totalStockValue.multiply(rate), userId, stockId);
                            } else {
                                Statistic statistic = new Statistic();
                                statistic.setUserId(userId);
                                statistic.setStockId(stockId);
                                statistic.setTotalValue(String.valueOf(totalStockValue.multiply(rate)));
                                statisticRepository.save(statistic);
                            }
                        } else {
                            List<String> errorMessages = Collections.singletonList(INVALID_STOCK);
                            LOGGER.error(errorMessages);
                            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
                        }
                    }
                }
            } else {
                List<String> errorMessages = Collections.singletonList(String.format(NO_STOCKS_FOUND_FOR_USER_WITH_ID, userId));
                LOGGER.info(errorMessages);
            }
            totalValue = stockIds.stream()
                    .map(stockId -> statisticRepository.getValue(userId, stockId))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            LOGGER.info("Total Value: {} ${}", displayCurrency, totalValue.stripTrailingZeros());
        } else {
            List<String> errorMessages = Collections.singletonList(String.format(NO_USER_FOUND_WITH_ID, userId));
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
    }

    @Override
    public BigDecimal calculateRealizedProfitsByStock(Long userId, Long stockId) {
        Optional<Stock> stock = stockRepository.findById(stockId);
        Optional<User> optionalUser = userRepository.findById(userId);
        BigDecimal totalStockRealizedProfits = BigDecimal.ZERO;
        String displayCurrency;
        if (optionalUser.isPresent()) {
            displayCurrency = optionalUser.get().getDisplayCurrency();
        } else {
            List<String> errorMessages = Collections.singletonList(String.format(NO_USER_FOUND_WITH_ID, userId));
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
        if (stock.isPresent()) {
            LOGGER.info(String.format(STOCK_TICKER, stock.get().getStockTicker()));
            List<Transaction> sellTransactions = transactionRepository.getSellTransactionsByStock(userId, stockId);
            if (sellTransactions.isEmpty()) {
                LOGGER.info("Realized Profits: {} ${}", displayCurrency, BigDecimal.ZERO);
                LOGGER.info("");
                return BigDecimal.ZERO;
            } else {
                for (Transaction sellTransaction : sellTransactions) {
                    String sellTransactionDate = sellTransaction.getTransactionDate();
                    String sellTransactionCurrency = sellTransaction.getCurrency();
                    BigDecimal rate = BigDecimal.ONE;
                    if (!sellTransactionCurrency.equals(displayCurrency)) {
                        String rateName = sellTransactionCurrency + "/" + displayCurrency;
                        Optional<Rate> optionalRate = rateRepository.findByRateNameIgnoreCase(rateName);
                        if (optionalRate.isPresent()) {
                            rate = new BigDecimal(optionalRate.get().getRate());
                        } else {
                            List<String> errorMessages = Collections.singletonList(String.format(INVALID_RATE, rateName));
                            LOGGER.error(errorMessages);
                            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
                        }
                    }
                    BigDecimal unitsSold = new BigDecimal(sellTransaction.getUnits());
                    BigDecimal unitSellingPrice = new BigDecimal(sellTransaction.getUnitPrice()).multiply(rate);
                    BigDecimal sellingFees = new BigDecimal(sellTransaction.getFees()).multiply(rate);
                    List<Transaction> buyTransactions = transactionRepository.getBuyTransactionsByUserIdAndStockIdAndDate(userId, stockId, sellTransactionDate);
                    if (buyTransactions.isEmpty()) {
                        List<String> errorMessages = Collections.singletonList("There should be a buy transaction before a sell transaction.");
                        LOGGER.error(errorMessages);
                        throw new GeneralException(new CustomError(ErrorConstants.INTERNAL_SERVER_ERROR_ERROR_CODE, errorMessages));
                    } else {
                        BigDecimal totalUnitsBought = BigDecimal.ZERO;
                        BigDecimal totalBuyingCost = BigDecimal.ZERO;
                        for (Transaction buyTransaction : buyTransactions) {
                            totalUnitsBought = totalUnitsBought.add(new BigDecimal(buyTransaction.getUnits()));
                            String buyTransactionCurrency = buyTransaction.getCurrency();
                            if (!buyTransactionCurrency.equals(displayCurrency)) {
                                String rateName = sellTransactionCurrency + "/" + displayCurrency;
                                Optional<Rate> optionalRate = rateRepository.findByRateNameIgnoreCase(rateName);
                                if (optionalRate.isPresent()) {
                                    rate = new BigDecimal(optionalRate.get().getRate());
                                } else {
                                    List<String> errorMessages = Collections.singletonList(String.format(INVALID_RATE, rateName));
                                    LOGGER.error(errorMessages);
                                    throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
                                }
                            }
                            BigDecimal unitsBought = new BigDecimal(buyTransaction.getUnits());
                            BigDecimal unitBuyingPrice = new BigDecimal(buyTransaction.getUnitPrice()).multiply(rate);
                            BigDecimal buyingFees = new BigDecimal(buyTransaction.getFees()).multiply(rate);
                            totalBuyingCost = totalBuyingCost.add(unitsBought.multiply(unitBuyingPrice)).add(buyingFees);
                        }
                        BigDecimal averageBuyingPrice = totalUnitsBought.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : totalBuyingCost.divide(totalUnitsBought, 15, RoundingMode.HALF_UP);
                        BigDecimal realizedProfits = unitsSold.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : ((((unitsSold.multiply(unitSellingPrice)).subtract(sellingFees)).divide(unitsSold, 15, RoundingMode.HALF_UP)).subtract(averageBuyingPrice)).multiply(unitsSold);
                        totalStockRealizedProfits = totalStockRealizedProfits.add(realizedProfits);

                    }
                }
            }
            LOGGER.info("Realized Profits: {} ${}", displayCurrency, totalStockRealizedProfits.stripTrailingZeros());
            LOGGER.info("");
            return totalStockRealizedProfits;
        } else {
            List<String> errorMessages = Collections.singletonList(INVALID_STOCK);
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
    }

    @Transactional
    @Override
    public void calculateRealizedProfits(Long userId) {
        LOGGER.info(Constants.ASTERISK);
        LOGGER.info(Constants.REALIZED_PROFITS);
        LOGGER.info(Constants.ASTERISK);
        BigDecimal totalRealizedProfits;
        String displayCurrency;
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            displayCurrency = optionalUser.get().getDisplayCurrency();
            List<Long> stockIds = stockRepository.findAllStockIdsByUserId(userId);
            if (!stockIds.isEmpty()) {
                List<Optional<Stock>> optionalStocks = stockRepository.findByStockIds(stockIds);
                if (!optionalStocks.isEmpty()) {
                    for (Optional<Stock> optionalStock : optionalStocks) {
                        if (optionalStock.isPresent()) {
                            Stock stock = optionalStock.get();
                            Long stockId = stock.getStockId();
                            BigDecimal realizedProfits = calculateRealizedProfitsByStock(userId, stockId);
                            if (statisticRepository.existsByUserIdAndStockId(userId, stockId)) {
                                statisticRepository.updateRealizedProfits(realizedProfits, userId, stockId);
                            } else {
                                Statistic statistic = new Statistic();
                                statistic.setUserId(userId);
                                statistic.setStockId(stockId);
                                statistic.setRealizedProfits(String.valueOf(realizedProfits));
                                statisticRepository.save(statistic);
                            }
                        } else {
                            List<String> errorMessages = Collections.singletonList(INVALID_STOCK);
                            LOGGER.error(errorMessages);
                            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
                        }
                    }
                }
            } else {
                List<String> errorMessages = Collections.singletonList(String.format(NO_STOCKS_FOUND_FOR_USER_WITH_ID, userId));
                LOGGER.info(errorMessages);
            }
            totalRealizedProfits = stockIds.stream()
                    .map(stockId -> statisticRepository.getRealizedProfits(userId, stockId))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            LOGGER.info("Total Realized Profits: {} ${}", displayCurrency, totalRealizedProfits.stripTrailingZeros());
        } else {
            List<String> errorMessages = Collections.singletonList(String.format(NO_USER_FOUND_WITH_ID, userId));
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
    }

    @Override
    public BigDecimal calculateUnrealizedProfitsByStock(Long userId, Long stockId) {
        Optional<Stock> stock = stockRepository.findById(stockId);
        Optional<User> optionalUser = userRepository.findById(userId);
        String displayCurrency;
        if (optionalUser.isPresent()) {
            displayCurrency = optionalUser.get().getDisplayCurrency();
        } else {
            List<String> errorMessages = Collections.singletonList(String.format(NO_USER_FOUND_WITH_ID, userId));
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
        if (stock.isPresent()) {
            LOGGER.info(String.format(STOCK_TICKER, stock.get().getStockTicker()));
            String baseCurrency = stock.get().getBaseCurrency();
            BigDecimal rate = BigDecimal.ONE;
            if (!baseCurrency.equals(displayCurrency)) {
                String rateName = baseCurrency + "/" + displayCurrency;
                Optional<Rate> optionalRate = rateRepository.findByRateNameIgnoreCase(rateName);
                if (optionalRate.isPresent()) {
                    rate = new BigDecimal(optionalRate.get().getRate());
                } else {
                    List<String> errorMessages = Collections.singletonList(String.format(INVALID_RATE, rateName));
                    LOGGER.error(errorMessages);
                    throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
                }
            }
            BigDecimal lastPrice = (stockRepository.findLastPriceByStockId(stockId)).multiply(rate);
            BigDecimal stockCost = statisticRepository.getCost(userId, stockId);
            BigDecimal units = statisticRepository.getStockUnits(userId, stockId);
            BigDecimal averageBuyingPrice = units.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : stockCost.divide(units, 15, RoundingMode.HALF_UP);
            BigDecimal unrealizedProfits = (lastPrice.subtract(averageBuyingPrice)).multiply(units);
            LOGGER.info("Unrealized Profits: {} ${}", displayCurrency, unrealizedProfits.stripTrailingZeros());
            LOGGER.info("");
            return unrealizedProfits;
        } else {
            List<String> errorMessages = Collections.singletonList(INVALID_STOCK);
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
    }

    @Transactional
    @Override
    public void calculateUnrealizedProfits(Long userId) {
        LOGGER.info(Constants.ASTERISK);
        LOGGER.info(Constants.UNREALIZED_PROFITS);
        LOGGER.info(Constants.ASTERISK);
        BigDecimal totalUnrealizedProfits;
        String displayCurrency;
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            displayCurrency = optionalUser.get().getDisplayCurrency();
            List<Long> stockIds = stockRepository.findAllStockIdsByUserId(userId);
            if (!stockIds.isEmpty()) {
                List<Optional<Stock>> optionalStocks = stockRepository.findByStockIds(stockIds);
                if (!optionalStocks.isEmpty()) {
                    for (Optional<Stock> optionalStock : optionalStocks) {
                        if (optionalStock.isPresent()) {
                            Stock stock = optionalStock.get();
                            Long stockId = stock.getStockId();
                            BigDecimal unrealizedProfits = calculateUnrealizedProfitsByStock(userId, stockId);
                            if (statisticRepository.existsByUserIdAndStockId(userId, stockId)) {
                                statisticRepository.updateUnrealizedProfits(unrealizedProfits, userId, stockId);
                            } else {
                                Statistic statistic = new Statistic();
                                statistic.setUserId(userId);
                                statistic.setStockId(stockId);
                                statistic.setUnrealizedProfits(String.valueOf(unrealizedProfits));
                                statisticRepository.save(statistic);
                            }
                        } else {
                            List<String> errorMessages = Collections.singletonList(INVALID_STOCK);
                            LOGGER.error(errorMessages);
                            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
                        }
                    }
                }
            } else {
                List<String> errorMessages = Collections.singletonList(String.format(NO_STOCKS_FOUND_FOR_USER_WITH_ID, userId));
                LOGGER.info(errorMessages);
            }
            totalUnrealizedProfits = stockIds.stream()
                    .map(stockId -> statisticRepository.getUnrealizedProfits(userId, stockId))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            LOGGER.info("Total Unrealized Profits: {} ${}", displayCurrency, totalUnrealizedProfits.stripTrailingZeros());
        } else {
            List<String> errorMessages = Collections.singletonList(String.format(NO_USER_FOUND_WITH_ID, userId));
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
    }

    @Override
    public BigDecimal calculateTotalDividendsEarnedByStock(Long userId, Long stockId) {
        Optional<Stock> stock = stockRepository.findById(stockId);
        if (stock.isPresent()) {
            LOGGER.info(String.format(STOCK_TICKER, stock.get().getStockTicker()));
            String earliestDate = transactionRepository.getEarliestTransactionDate(userId, stockId);
            String baseCurrency = stock.get().getBaseCurrency();
            List<Dividend> dividends = dividendRepository.getRelevantDividends(stockId, earliestDate);
            BigDecimal dividendsEarned = dividends.stream()
                    .map(dividend -> {
                        String exDate = dividend.getExDate();
                        BigDecimal totalUnits = calculateTotalUnitsOwnedOnGivenDate(userId, stockId, exDate);
                        BigDecimal dividendsEarnedExDate = totalUnits.multiply(new BigDecimal(dividend.getPayout()));
                        LOGGER.info(String.format("Dividends Earned on ex Date %s: %s $%s", exDate, baseCurrency, dividendsEarnedExDate.stripTrailingZeros()));
                        return dividendsEarnedExDate;
                    })
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            LOGGER.info(String.format("Dividends Earned: %s $%.2f", baseCurrency, dividendsEarned.stripTrailingZeros()));
            LOGGER.info("");
            return dividendsEarned;
        } else {
            List<String> errorMessages = Collections.singletonList(INVALID_STOCK);
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
    }

    @Override
    @Transactional
    public void calculateTotalDividendsEarned(Long userId) {
        LOGGER.info(Constants.ASTERISK);
        LOGGER.info(Constants.DIVIDENDS);
        LOGGER.info(Constants.ASTERISK);
        BigDecimal totalDividends;
        String displayCurrency;
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            displayCurrency = optionalUser.get().getDisplayCurrency();
            List<Long> stockIds = stockRepository.findAllStockIdsByUserId(userId);
            if (!stockIds.isEmpty()) {
                List<Optional<Stock>> optionalStocks = stockRepository.findDividendStocksByIds(stockIds);
                if (!optionalStocks.isEmpty()) {
                    for (Optional<Stock> optionalStock : optionalStocks) {
                        if (optionalStock.isPresent()) {
                            Stock stock = optionalStock.get();
                            Long stockId = stock.getStockId();
                            String baseCurrency = stockRepository.findBaseCurrencyByStockId(stockId);
                            BigDecimal dividendsEarned = calculateTotalDividendsEarnedByStock(userId, stockId);
                            BigDecimal rate = BigDecimal.ONE;
                            if (!baseCurrency.equals(displayCurrency)) {
                                String rateName = baseCurrency + "/" + displayCurrency;
                                Optional<Rate> optionalRate = rateRepository.findByRateNameIgnoreCase(rateName);
                                if (optionalRate.isPresent()) {
                                    rate = new BigDecimal(optionalRate.get().getRate());
                                } else {
                                    List<String> errorMessages = Collections.singletonList(String.format(INVALID_RATE, rateName));
                                    LOGGER.error(errorMessages);
                                    throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
                                }
                            }
                            if (statisticRepository.existsByUserIdAndStockId(userId, stockId)) {
                                statisticRepository.updateDividends(dividendsEarned.multiply(rate), userId, stockId);
                            } else {
                                Statistic statistic = new Statistic();
                                statistic.setUserId(userId);
                                statistic.setStockId(stockId);
                                statistic.setDividendsEarned(String.valueOf(dividendsEarned.multiply(rate)));
                                statisticRepository.save(statistic);
                            }
                        } else {
                            List<String> errorMessages = Collections.singletonList(INVALID_STOCK);
                            LOGGER.error(errorMessages);
                            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
                        }
                    }
                }
            } else {
                List<String> errorMessages = Collections.singletonList(String.format(NO_STOCKS_FOUND_FOR_USER_WITH_ID, userId));
                LOGGER.info(errorMessages);
            }
            totalDividends = stockIds.stream()
                    .map(stockId -> statisticRepository.getDividends(userId, stockId))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            LOGGER.info("Total Dividends Earned: {} ${}", displayCurrency, totalDividends.stripTrailingZeros());
        } else {
            List<String> errorMessages = Collections.singletonList(String.format(NO_USER_FOUND_WITH_ID, userId));
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
    }

    @Override
    public BigDecimal calculateTotalProfitsByStock(Long userId, Long stockId) {
        Optional<Stock> stock = stockRepository.findById(stockId);
        Optional<User> optionalUser = userRepository.findById(userId);
        String displayCurrency;
        BigDecimal totalProfits = BigDecimal.ZERO;
        if (optionalUser.isPresent()) {
            displayCurrency = optionalUser.get().getDisplayCurrency();
        } else {
            List<String> errorMessages = Collections.singletonList(String.format(NO_USER_FOUND_WITH_ID, userId));
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
        if (stock.isPresent()) {
            LOGGER.info(String.format(STOCK_TICKER, stock.get().getStockTicker()));
            Optional<Statistic> optionalStatistic = statisticRepository.findByUserIdAndStockId(userId, stockId);
            if (optionalStatistic.isPresent()) {
                Statistic statistic = optionalStatistic.get();
                BigDecimal realizedProfits = new BigDecimal(statistic.getRealizedProfits());
                BigDecimal unrealizedProfits = new BigDecimal(statistic.getUnrealizedProfits());
                BigDecimal dividendsEarned = Optional.ofNullable(statistic.getDividendsEarned()).map(BigDecimal::new).orElse(BigDecimal.ZERO);
                totalProfits = totalProfits.add(realizedProfits).add(unrealizedProfits).add(dividendsEarned);
            } else {
                List<String> errorMessages = Collections.singletonList(String.format("Error retrieving statistic for stock ticker %s and user id %d", stock.get().getStockTicker(), userId));
                LOGGER.error(errorMessages);
                throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
            }
            LOGGER.info("Total Profits: {} ${}", displayCurrency, totalProfits.stripTrailingZeros());
            LOGGER.info("");
            return totalProfits;
        } else {
            List<String> errorMessages = Collections.singletonList(INVALID_STOCK);
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
    }

    @Transactional
    @Override
    public void calculateTotalProfits(Long userId) {
        LOGGER.info(Constants.ASTERISK);
        LOGGER.info(Constants.TOTAL_PROFITS);
        LOGGER.info(Constants.ASTERISK);
        BigDecimal overallProfits;
        String displayCurrency;
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            displayCurrency = optionalUser.get().getDisplayCurrency();
            List<Long> stockIds = stockRepository.findAllStockIdsByUserId(userId);
            if (!stockIds.isEmpty()) {
                List<Optional<Stock>> optionalStocks = stockRepository.findByStockIds(stockIds);
                if (!optionalStocks.isEmpty()) {
                    for (Optional<Stock> optionalStock : optionalStocks) {
                        if (optionalStock.isPresent()) {
                            Stock stock = optionalStock.get();
                            Long stockId = stock.getStockId();
                            BigDecimal totalProfits = calculateTotalProfitsByStock(userId, stockId);
                            if (statisticRepository.existsByUserIdAndStockId(userId, stockId)) {
                                statisticRepository.updateTotalProfits(totalProfits, userId, stockId);
                            } else {
                                Statistic statistic = new Statistic();
                                statistic.setUserId(userId);
                                statistic.setStockId(stockId);
                                statistic.setTotalProfits(String.valueOf(totalProfits));
                                statisticRepository.save(statistic);
                            }
                        } else {
                            List<String> errorMessages = Collections.singletonList(INVALID_STOCK);
                            LOGGER.error(errorMessages);
                            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
                        }
                    }
                }
            } else {
                List<String> errorMessages = Collections.singletonList(String.format(NO_STOCKS_FOUND_FOR_USER_WITH_ID, userId));
                LOGGER.info(errorMessages);
            }
            overallProfits = stockIds.stream()
                    .map(stockId -> statisticRepository.getTotalProfits(userId, stockId))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            LOGGER.info("Total Profits: {} ${}", displayCurrency, overallProfits.stripTrailingZeros());
            LOGGER.info(Constants.ASTERISK);
        } else {
            List<String> errorMessages = Collections.singletonList(String.format(NO_USER_FOUND_WITH_ID, userId));
            LOGGER.error(errorMessages);
            throw new NotFoundException(new CustomError(ErrorConstants.NOT_FOUND_ERROR_CODE, errorMessages));
        }
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