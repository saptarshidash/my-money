
package com.crio.warmup.stock.portfolio;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.exception.StockQuoteServiceException;
import com.crio.warmup.stock.quotes.StockQuotesService;
import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.web.client.RestTemplate;

public class PortfolioManagerImpl implements PortfolioManager {

  private RestTemplate restTemplate;
  private StockQuotesService stockQuotesService;

  private static final String API_KEY = "0bb47f878bcc4ab3e021c1a1452f3375eeb53f26";

  // Caution: Do not delete or modify the constructor, or else your build will
  // break!
  // This is absolutely necessary for backward compatibility
  @Deprecated
  protected PortfolioManagerImpl(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public PortfolioManagerImpl(StockQuotesService stockQuotesService) {
    this.stockQuotesService = stockQuotesService;
  }

  // Returns a comperator to sort the annualized returns list
  private Comparator<AnnualizedReturn> getComparator() {
    return Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
  }

  /**
   * Gets the candle data for a particular stock.
   * @param symbol Stock symbol for which candle will be fetched.
   * @param from Date from which candle data will be fecthed
   * @param to Date upto which candle data will be fetched
   * @return Returns a list of candle data in between specified date range.
   * @throws StockQuoteServiceException Throws exception in case some error in stock service.
   */
  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws StockQuoteServiceException{

    List<Candle> candles = stockQuotesService.getStockQuote(symbol, from, to);
    System.out.println();
    return candles;

  }

  // Used to build the api endpoint
  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
    String uriTemplate = "https://api.tiingo.com/tiingo/daily/" + symbol + "/prices?" + "startDate=" + startDate
        + "&endDate=" + endDate + "&token=" + API_KEY;

    return uriTemplate;
  }

  @Override
  public List<AnnualizedReturn> calculateAnnualizedReturn(List<PortfolioTrade> portfolioTrades, LocalDate endDate)
      throws StockQuoteServiceException {

    List<Candle> candles = new ArrayList<>();
    List<AnnualizedReturn> annualizedReturns = new ArrayList<>();

    for (PortfolioTrade trade : portfolioTrades) {
      candles = getStockQuote(trade.getSymbol(), trade.getPurchaseDate(), endDate);
      double buyPrice = candles.get(0).getOpen();
      double sellPrice = candles.get(candles.size() - 1).getClose();
      annualizedReturns.add(calculateSingleAnnualizedReturn(trade, endDate, buyPrice, sellPrice));
    }

    annualizedReturns.sort(getComparator());
    return annualizedReturns;

  }

  // Utility method to calculate annualized return for a single portfolio trade object
  private AnnualizedReturn calculateSingleAnnualizedReturn(PortfolioTrade trade, LocalDate endDate, Double buyPrice,
      Double sellPrice) {

    Double totalReturn = (sellPrice - buyPrice) / buyPrice;
   
    double totalYears = (ChronoUnit.DAYS.between(trade.getPurchaseDate(), endDate) / (double) 365);

    Double annualizedReturn = Math.pow((1 + totalReturn), (1 / totalYears)) - 1;
    return new AnnualizedReturn(trade.getSymbol(), annualizedReturn, totalReturn);
  }

  @Override
  public List<AnnualizedReturn> calculateAnnualizedReturnParallel(List<PortfolioTrade> portfolioTrades,
      LocalDate endDate, int numThreads) throws StockQuoteServiceException {

    // create a executor service
    ExecutorService executor = Executors.newFixedThreadPool(numThreads);

    List<Future<AnnualizedReturn>> calls = new ArrayList<>();
    for (PortfolioTrade trade : portfolioTrades) {
      calls.add(executor.submit(new Callable<AnnualizedReturn>() {

        @Override
        public AnnualizedReturn call() throws Exception {
          List<Candle> candles = getStockQuote(trade.getSymbol(), trade.getPurchaseDate(), endDate);
          double buyPrice = candles.get(0).getOpen();
          double sellPrice = candles.get(candles.size() - 1).getClose();
          return calculateSingleAnnualizedReturn(trade, endDate, buyPrice, sellPrice);
        }
      }));
    }
    List<AnnualizedReturn> returns = new ArrayList<>();
    for (Future<AnnualizedReturn> future : calls) {
      try {
        returns.add(future.get());
      } catch (InterruptedException | ExecutionException e) {
        e.printStackTrace();
        throw new StockQuoteServiceException(e.getMessage());
      }
        }

        executor.shutdown();

        returns.sort(getComparator());

    return returns;
  }


}
