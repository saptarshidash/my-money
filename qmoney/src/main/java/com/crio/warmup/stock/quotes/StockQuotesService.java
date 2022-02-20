package com.crio.warmup.stock.quotes;

import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.exception.StockQuoteServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.time.LocalDate;
import java.util.List;

public interface StockQuotesService {



  /**
   * Used to get the candle data for a stock symbol, implementation differs based on the provider.
   * It can be alphavantage or tiingo or anything
   * @param symbol Symbol for which candle data will be fetched.
   * @param from Start date of the candle data.
   * @param to End data of the candle data
   * @return Returns a list of candle within specified date range.
   * @throws StockQuoteServiceException Throws exception when no response is given by stock services.
   */
  List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws  StockQuoteServiceException
  ;

}
