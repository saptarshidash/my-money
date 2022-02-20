
package com.crio.warmup.stock.portfolio;

import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.exception.StockQuoteServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface PortfolioManager {

  /**
   * Method is used to calculate anualized returns of given portfolio trades concurrently.
   * @param portfolioTrades List of Portfolio trades.
   * @param endDate Upto which date annualized return is calculated.
   * @param numThreads Number of threads in pool which will be reused to perform api call and calculation.
   * @return Returns a list of annualized returns
   * @throws StockQuoteServiceException Throws exception in case stock quote service is not responding
   */
  List<AnnualizedReturn> calculateAnnualizedReturnParallel(
      List<PortfolioTrade> portfolioTrades,
      LocalDate endDate, int numThreads) throws InterruptedException,
      StockQuoteServiceException, ExecutionException;

  //CHECKSTYLE:OFF

 /**
   * Method is used to calculate anualized returns of given portfolio trades sequentially.
   * @param portfolioTrades List of Portfolio trades.
   * @param endDate Upto which date annualized return is calculated.
   * @param numThreads Number of threads in pool which will be reused to perform api call and calculation.
   * @return Returns a list of annualized returns
   * @throws StockQuoteServiceException Throws exception in case stock quote service is not responding
   */
  List<AnnualizedReturn> calculateAnnualizedReturn(List<PortfolioTrade> portfolioTrades,
      LocalDate endDate)
      throws StockQuoteServiceException, JsonProcessingException
  ;
}

