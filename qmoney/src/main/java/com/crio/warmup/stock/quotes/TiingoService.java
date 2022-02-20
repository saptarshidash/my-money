
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

  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
    String uriTemplate = "https://api.tiingo.com/tiingo/daily/"+symbol+"/prices?"
         + "startDate="+startDate+"&endDate="+endDate+"&token="+TIINGO_KEY;

     return uriTemplate;
}

}
