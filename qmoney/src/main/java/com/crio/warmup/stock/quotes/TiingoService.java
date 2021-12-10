
package com.crio.warmup.stock.quotes;

import static com.crio.warmup.stock.PortfolioUtil.TIINGO_KEY;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.crio.warmup.stock.PortfolioUtil;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.exception.StockQuoteServiceException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

public class TiingoService implements StockQuotesService {

  private RestTemplate restTemplate;

  protected TiingoService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Override
  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to) throws  StockQuoteServiceException {
    String url = buildUri(symbol, from, to);
    String data;
    List<Candle> candles = new ArrayList<>();

    try{
      data = restTemplate.getForObject(url, String.class);
      ObjectMapper mapper = PortfolioUtil.getObjectMapper();
      candles = mapper.readValue(data, new TypeReference<List<TiingoCandle>>() {
      }).stream().collect(Collectors.toList());

      System.out.println("Candle size - "+candles.size());
    } catch(Exception e){
      e.printStackTrace();
      throw new StockQuoteServiceException(e.getMessage());
    }


    return candles;

    

  }


  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Implement getStockQuote method below that was also declared in the interface.

  // Note:
  // 1. You can move the code from PortfolioManagerImpl#getStockQuote inside newly created method.
  // 2. Run the tests using command below and make sure it passes.
  //    ./gradlew test --tests TiingoServiceTest


  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Write a method to create appropriate url to call the Tiingo API.
  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
    String uriTemplate = "https://api.tiingo.com/tiingo/daily/"+symbol+"/prices?"
         + "startDate="+startDate+"&endDate="+endDate+"&token="+TIINGO_KEY;

     return uriTemplate;
}






  // TODO: CRIO_TASK_MODULE_EXCEPTIONS
  //  1. Update the method signature to match the signature change in the interface.
  //     Start throwing new StockQuoteServiceException when you get some invalid response from
  //     Tiingo, or if Tiingo returns empty results for whatever reason, or you encounter
  //     a runtime exception during Json parsing.
  //  2. Make sure that the exception propagates all the way from
  //     PortfolioManager#calculateAnnualisedReturns so that the external user's of our API
  //     are able to explicitly handle this exception upfront.

  //CHECKSTYLE:OFF


}
