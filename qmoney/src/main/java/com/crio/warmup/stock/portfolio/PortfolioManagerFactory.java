
package com.crio.warmup.stock.portfolio;

import com.crio.warmup.stock.quotes.StockQuoteServiceFactory;
import com.crio.warmup.stock.quotes.StockQuotesService;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerFactory {

   /**
    * Global access point in factory which is used to create a concrete PortfolioManager instance.
    * @param restTemplate Instance of RestTemplate which will be used to create PortfolioManager.
    * @return Returns a concrete implementation of PortfolioManager interface.
    */
   public static PortfolioManager getPortfolioManager(RestTemplate restTemplate){
     return new PortfolioManagerImpl(restTemplate);
   } 

   /**
    * Overloaded access point in factory which is used to create a concrete PortfolioManager.
    * This method uses StockQuoteServiceFactory in order to create a concrete implementation of
    * a stock quote service (e.g Alphavantage, Tiingo) and used that stockquote service to create
    * a concrete PortfolioManager instance
    * @param provider Name of the provider for which stockquote service will be instantiated.
    * @return Returns a concrete PortfolioManager instance.
    */
   public static PortfolioManager getPortfolioManager(String provider,
     RestTemplate restTemplate) {

      StockQuoteServiceFactory factory = StockQuoteServiceFactory.INSTANCE;
      StockQuotesService stockQuotesService = factory.getService(provider, restTemplate);

      return new PortfolioManagerImpl(stockQuotesService);
   }

}
