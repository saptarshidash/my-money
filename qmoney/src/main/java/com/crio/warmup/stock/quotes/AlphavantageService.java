
package com.crio.warmup.stock.quotes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.crio.warmup.stock.PortfolioUtil.ALPHAV_KEY;

import com.crio.warmup.stock.dto.AlphavantageCandle;
import com.crio.warmup.stock.dto.AlphavantageDailyResponse;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.exception.StockQuoteServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.web.client.RestTemplate;

public class AlphavantageService implements StockQuotesService {

  private RestTemplate restTemplate;


  public AlphavantageService(RestTemplate restTemplate){
    this.restTemplate = restTemplate;
  }

  @Override
  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to) throws StockQuoteServiceException {

    String uri = buildUri(symbol);

    List<Candle> candles = new ArrayList<>();

    try{

      AlphavantageDailyResponse response = restTemplate.getForObject(uri, AlphavantageDailyResponse.class);
      // filtering dates which falls in range
      for(LocalDate date: response.getCandles().keySet()){
        if((date.isAfter(from) && date.isBefore(to)) || date.isEqual(from) || date.isEqual(to)){
          response.getCandles().get(date).setDate(date); // setting date for the candle
          candles.add(response.getCandles().get(date));
        }
      }

    } catch(Exception e){
      throw new StockQuoteServiceException(e.getMessage());
    }

    Collections.reverse(candles);

    return candles;
  }


  // Utility method to build uri for alphavantage
  private String buildUri(String symbol){
    return "https://www.alphavantage.co/query?"
    +"function=TIME_SERIES_DAILY_ADJUSTED&symbol="+symbol+"&outputsize=full&apikey="+ALPHAV_KEY;

  }


}

