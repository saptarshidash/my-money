
package com.crio.warmup.stock.portfolio;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
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

  // TODO: CRIO_TASK_MODULE_REFACTOR
  // 1. Now we want to convert our code into a module, so we will not call it from
  // main anymore.
  // Copy your code from Module#3
  // PortfolioManagerApplication#calculateAnnualizedReturn
  // into #calculateAnnualizedReturn function here and ensure it follows the
  // method signature.
  // 2. Logic to read Json file and convert them into Objects will not be required
  // further as our
  // clients will take care of it, going forward.

  // Note:
  // Make sure to exercise the tests inside PortfolioManagerTest using command
  // below:
  // ./gradlew test --tests PortfolioManagerTest

  // CHECKSTYLE:OFF

  public PortfolioManagerImpl(StockQuotesService stockQuotesService) {
    this.stockQuotesService = stockQuotesService;
  }

  private Comparator<AnnualizedReturn> getComparator() {
    return Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
  }

  // CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_REFACTOR
  // Extract the logic to call Tiingo third-party APIs to a separate function.
  // Remember to fill out the buildUri function and use that.

  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to) throws JsonProcessingException {

    List<Candle> candles = stockQuotesService.getStockQuote(symbol, from, to);
    System.out.println();
    return candles;
     
  }

  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
       String uriTemplate = "https://api.tiingo.com/tiingo/daily/"+symbol+"/prices?"
            + "startDate="+startDate+"&endDate="+endDate+"&token="+API_KEY;

        return uriTemplate;
  }

  @Override
  public List<AnnualizedReturn> calculateAnnualizedReturn(List<PortfolioTrade> portfolioTrades, LocalDate endDate) {
    // TODO Auto-generated method stub

    return portfolioTrades.stream().map(trade ->{

      List<Candle> candles = null;
      try {
        candles = getStockQuote(trade.getSymbol(), trade.getPurchaseDate(), endDate);
      } catch (JsonProcessingException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

      Double buyPrice = candles.get(0).getOpen();
      Double sellPrice = candles.get(candles.size() - 1).getClose();

      return calculateSingleAnnualizedReturn(trade, endDate, buyPrice, sellPrice);
    }).sorted(getComparator())
      .collect(Collectors.toList());
  }

  private AnnualizedReturn calculateSingleAnnualizedReturn(PortfolioTrade trade, 
      LocalDate endDate, Double buyPrice, Double sellPrice){

           Double totalReturn = (sellPrice - buyPrice) / buyPrice;
           //double years = trade.getPurchaseDate().until(endDate, ChronoUnit.DAYS) / 365d;
           double totalYears =  (ChronoUnit.DAYS.between(trade.getPurchaseDate(), endDate) / (double)365);

           Double annualizedReturn = Math.pow((1 + totalReturn), (1 / totalYears)) - 1;
           return new AnnualizedReturn(trade.getSymbol(), annualizedReturn, totalReturn);
  }


  // ¶TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Modify the function #getStockQuote and start delegating to calls to
  //  stockQuoteService provided via newly added constructor of the class.
  //  You also have a liberty to completely get rid of that function itself, however, make sure
  //  that you do not delete the #getStockQuote function.

}
