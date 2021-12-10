
package com.crio.warmup.stock;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.dto.TotalReturnsDto;
import com.crio.warmup.stock.exception.StockQuoteServiceException;
import com.crio.warmup.stock.log.UncaughtExceptionHandler;
import com.crio.warmup.stock.portfolio.PortfolioManager;
import com.crio.warmup.stock.portfolio.PortfolioManagerFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.apache.logging.log4j.ThreadContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerApplication {

  // token
  public static final String token = "0bb47f878bcc4ab3e021c1a1452f3375eeb53f26";

  // TODO: CRIO_TASK_MODULE_JSON_PARSING
  // Task:
  // - Read the json file provided in the argument[0], The file is available in
  // the classpath.
  // - Go through all of the trades in the given file,
  // - Prepare the list of all symbols a portfolio has.
  // - if "trades.json" has trades like
  // [{ "symbol": "MSFT"}, { "symbol": "AAPL"}, { "symbol": "GOOGL"}]
  // Then you should return ["MSFT", "AAPL", "GOOGL"]
  // Hints:
  // 1. Go through two functions provided - #resolveFileFromResources() and
  // #getObjectMapper
  // Check if they are of any help to you.
  // 2. Return the list of all symbols in the same order as provided in json.

  // Note:
  // 1. There can be few unused imports, you will need to fix them to make the
  // build pass.
  // 2. You can use "./gradlew build" to check if your code builds successfully.

  public static List<String> mainReadFile(String[] args) throws IOException, URISyntaxException {

    File tradeFile = resolveFileFromResources(args[0]);
    ObjectMapper mapper = getObjectMapper();
    PortfolioTrade[] trades = mapper.readValue(tradeFile, PortfolioTrade[].class);

    // extract symbols
    List<String> symbols = new ArrayList<>();

    for (PortfolioTrade trade : trades) {
      symbols.add(trade.getSymbol());
    }

    return symbols;

  }

  private static void printJsonObject(Object object) throws IOException {
    Logger logger = Logger.getLogger(PortfolioManagerApplication.class.getCanonicalName());
    ObjectMapper mapper = new ObjectMapper();
    logger.info(mapper.writeValueAsString(object));
  }

  private static String readFileAsString(String file)
      throws JsonParseException, JsonMappingException, IOException, URISyntaxException {
    File tradeFile = resolveFileFromResources(file);
    ObjectMapper mapper = getObjectMapper();
    PortfolioTrade[] trades = mapper.readValue(tradeFile, PortfolioTrade[].class);
    String content = mapper.writeValueAsString(trades);
    return content;
  }

  private static File resolveFileFromResources(String filename) throws URISyntaxException {
    return Paths.get(Thread.currentThread().getContextClassLoader().getResource(filename).toURI()).toFile();
  }

  private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }

  // TODO: CRIO_TASK_MODULE_JSON_PARSING
  // Follow the instructions provided in the task documentation and fill up the
  // correct values for
  // the variables provided. First value is provided for your reference.
  // A. Put a breakpoint on the first line inside mainReadFile() which says
  // return Collections.emptyList();
  // B. Then Debug the test #mainReadFile provided in
  // PortfoliomanagerApplicationTest.java
  // following the instructions to run the test.
  // Once you are able to run the test, perform following tasks and record the
  // output as a
  // String in the function below.
  // Use this link to see how to evaluate expressions -
  // https://code.visualstudio.com/docs/editor/debugging#_data-inspection
  // 1. evaluate the value of "args[0]" and set the value
  // to the variable named valueOfArgument0 (This is implemented for your
  // reference.)
  // 2. In the same window, evaluate the value of expression below and set it
  // to resultOfResolveFilePathArgs0
  // expression ==> resolveFileFromResources(args[0])
  // 3. In the same window, evaluate the value of expression below and set it
  // to toStringOfObjectMapper.
  // You might see some garbage numbers in the output. Dont worry, its expected.
  // expression ==> getObjectMapper().toString()
  // 4. Now Go to the debug window and open stack trace. Put the name of the
  // function you see at
  // second place from top to variable functionNameFromTestFileInStackTrace
  // 5. In the same window, you will see the line number of the function in the
  // stack trace window.
  // assign the same to lineNumberFromTestFileInStackTrace
  // Once you are done with above, just run the corresponding test and
  // make sure its working as expected. use below command to do the same.
  // ./gradlew test --tests PortfolioManagerApplicationTest.testDebugValues

  public static List<String> debugOutputs() {

    String valueOfArgument0 = "trades.json";
    String resultOfResolveFilePathArgs0 = "trades.json";
    String toStringOfObjectMapper = "ObjectMapper";
    String functionNameFromTestFileInStackTrace = "mainReadFile";
    String lineNumberFromTestFileInStackTrace = "";

    return Arrays.asList(new String[] { valueOfArgument0, resultOfResolveFilePathArgs0, toStringOfObjectMapper,
        functionNameFromTestFileInStackTrace, lineNumberFromTestFileInStackTrace });
  }

  // TODO: CRIO_TASK_MODULE_REST_API
  // Find out the closing price of each stock on the end_date and return the list
  // of all symbols in ascending order by its close value on end date.

  // Note:
  // 1. You may have to register on Tiingo to get the api_token.
  // 2. Look at args parameter and the module instructions carefully.
  // 2. You can copy relevant code from #mainReadFile to parse the Json.
  // 3. Use RestTemplate#getForObject in order to call the API,
  // and deserialize the results in List<Candle>

  // Note:
  // Remember to confirm that you are getting same results for annualized returns
  // as in Module 3.
  public static List<String> mainReadQuotes(String[] args) throws IOException, URISyntaxException {
    LocalDate endDate = LocalDate.parse(args[1]); // 2nd arg contains end date passed from terminal
    // get user trade details from the json file
    List<PortfolioTrade> tradeDetails = readTradesFromJson(args[0]);

    List<TotalReturnsDto> returnsDtos = new ArrayList<>();

    // hit tinglo endpoint
    RestTemplate restTemplate = new RestTemplate();

    for (PortfolioTrade trade : tradeDetails) {
      String url = prepareUrl(trade, endDate, token);

      TiingoCandle[] candles = restTemplate.getForObject(url, TiingoCandle[].class);

      TiingoCandle endDateCandle = candles[candles.length - 1];
      // store close date price with symbol
      returnsDtos.add(new TotalReturnsDto(trade.getSymbol(), endDateCandle.getClose()));

    }

    // sort the symbols based on close price
    Collections.sort(returnsDtos, new Comparator<TotalReturnsDto>() {

      @Override
      public int compare(TotalReturnsDto arg0, TotalReturnsDto arg1) {
        // TODO Auto-generated method stub

        if (arg0.getClosingPrice() > arg1.getClosingPrice())
          return 1;
        else if (arg0.getClosingPrice() < arg1.getClosingPrice())
          return -1;
        else
          return 0;

      }
    });

    // collect the symbols from sorted list
    List<String> sortedSymbols = new ArrayList<>();
    for (TotalReturnsDto returnsDto : returnsDtos) {
      sortedSymbols.add(returnsDto.getSymbol());
    }

    return sortedSymbols;
  }

  // TODO:
  // After refactor, make sure that the tests pass by using these two commands
  // ./gradlew test --tests PortfolioManagerApplicationTest.readTradesFromJson
  // ./gradlew test --tests PortfolioManagerApplicationTest.mainReadFile
  public static List<PortfolioTrade> readTradesFromJson(String filename) throws IOException, URISyntaxException {

    ObjectMapper mapper = getObjectMapper();
    List<PortfolioTrade> trades = mapper.readValue(resolveFileFromResources(filename),
        new TypeReference<List<PortfolioTrade>>() {
        });

    return trades;
  }

  // TODO:
  // Build the Url using given parameters and use this function in your code to
  // call the API.
  public static String prepareUrl(PortfolioTrade trade, LocalDate endDate, String token) {
    String BASE_URL = "https://api.tiingo.com/tiingo/daily/" + trade.getSymbol() + "/prices?";
    String endPoint = BASE_URL + "startDate=" + trade.getPurchaseDate() + "&endDate=" + endDate + "&token=" + token;
    return endPoint;
  }

  public static String getToken() {
    return token;
  }

  // Note:
  // 1. You may need to copy relevant code from #mainReadQuotes to parse the Json.
  // 2. Remember to get the latest quotes from Tiingo API.

  // TODO: CRIO_TASK_MODULE_CALCULATIONS
  // Now that you have the list of PortfolioTrade and their data, calculate
  // annualized returns
  // for the stocks provided in the Json.
  // Use the function you just wrote #calculateAnnualizedReturns.
  // Return the list of AnnualizedReturns sorted by annualizedReturns in
  // descending order.

  // Note:
  // 1. You may need to copy relevant code from #mainReadQuotes to parse the Json.
  // 2. Remember to get the latest quotes from Tiingo API.

  // TODO:
  // Ensure all tests are passing using below command
  // ./gradlew test --tests ModuleThreeRefactorTest

  static Double getOpeningPriceOnStartDate(List<Candle> candles) {
    return candles.get(0).getOpen();
  }

  public static Double getClosingPriceOnEndDate(List<Candle> candles) {
    return candles.get(candles.size() - 1).getClose();
  }

  public static List<Candle> fetchCandles(PortfolioTrade trade, LocalDate endDate, String token) {
    RestTemplate restTemplate = new RestTemplate();

    String url = prepareUrl(trade, endDate, token);
    List<Candle> candles = restTemplate
        .exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<TiingoCandle>>() {
        }).getBody().stream().collect(Collectors.toList());

    return candles;
  }

  public static List<AnnualizedReturn> mainCalculateSingleReturn(String[] args) throws IOException, URISyntaxException {

    List<AnnualizedReturn> returns = new ArrayList<>();
    LocalDate endDate = LocalDate.parse(args[1]);

    File tradeFile = resolveFileFromResources(args[0]);

    ObjectMapper mapper = getObjectMapper();
    PortfolioTrade[] trades = mapper.readValue(tradeFile, PortfolioTrade[].class);

    for (PortfolioTrade trade : trades) {
      List<Candle> candles = fetchCandles(trade, endDate, token);
      Double buyPrice = getOpeningPriceOnStartDate(candles);
      Double sellPrice = getClosingPriceOnEndDate(candles);
      returns.add(calculateAnnualizedReturns(endDate, trade, buyPrice, sellPrice));
    }

    Collections.sort(returns, new Comparator<AnnualizedReturn>() {

      @Override
      public int compare(AnnualizedReturn arg0, AnnualizedReturn arg1) {
        // TODO Auto-generated method stub
        if (arg0.getAnnualizedReturn() > arg1.getAnnualizedReturn())
          return -1;
        else if (arg0.getAnnualizedReturn() < arg1.getAnnualizedReturn())
          return 1;
        return 0;
      }

    });

    return returns;
  }

  // TODO: CRIO_TASK_MODULE_CALCULATIONS
  // Return the populated list of AnnualizedReturn for all stocks.
  // Annualized returns should be calculated in two steps:
  // 1. Calculate totalReturn = (sell_value - buy_value) / buy_value.
  // 1.1 Store the same as totalReturns
  // 2. Calculate extrapolated annualized returns by scaling the same in years
  // span.
  // The formula is:
  // annualized_returns = (1 + total_returns) ^ (1 / total_num_years) - 1
  // 2.1 Store the same as annualized_returns
  // Test the same using below specified command. The build should be successful.
  // ./gradlew test --tests
  // PortfolioManagerApplicationTest.testCalculateAnnualizedReturn

  public static AnnualizedReturn calculateAnnualizedReturns(LocalDate endDate, PortfolioTrade trade, Double buyPrice,
      Double sellPrice) {
    Double totalReturn = (sellPrice - buyPrice) / buyPrice;
    // double years = trade.getPurchaseDate().until(endDate, ChronoUnit.DAYS) /
    // 365d;
    double totalYears = (ChronoUnit.DAYS.between(trade.getPurchaseDate(), endDate) / (double) 365);

    Double annualizedReturn = Math.pow((1 + totalReturn), (1 / totalYears)) - 1;
    System.out.println();
    return new AnnualizedReturn(trade.getSymbol(), annualizedReturn, totalReturn);
  }

  // TODO: CRIO_TASK_MODULE_REFACTOR
  // Once you are done with the implementation inside PortfolioManagerImpl and
  // PortfolioManagerFactory, create PortfolioManager using
  // PortfolioManagerFactory.
  // Refer to the code from previous modules to get the List<PortfolioTrades> and
  // endDate, and
  // call the newly implemented method in PortfolioManager to calculate the
  // annualized returns.

  // Note:
  // Remember to confirm that you are getting same results for annualized returns
  // as in Module 3.

  public static List<AnnualizedReturn> mainCalculateReturnsAfterRefactor(String[] args)
      throws JsonParseException, JsonMappingException, IOException, URISyntaxException, StockQuoteServiceException
       {
       String file = "trades.json";
       LocalDate endDate = LocalDate.parse("2019-12-12");
       String contents = readFileAsString(file);
       ObjectMapper objectMapper = getObjectMapper();
       PortfolioTrade[] portfolioTrades = objectMapper.readValue(contents, PortfolioTrade[].class);
       // create instance of PortfolioManagar using factory method
       // create instance of rest template and pass it to the factory method
       RestTemplate restTemplate = new RestTemplate();
       PortfolioManager portfolioManager = PortfolioManagerFactory.getPortfolioManager("tiingo",restTemplate);
       return portfolioManager.calculateAnnualizedReturn(Arrays.asList(portfolioTrades), endDate);
      
  }



  public static void main(String[] args) throws Exception {
    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
    ThreadContext.put("runId", UUID.randomUUID().toString());

    printJsonObject(mainCalculateReturnsAfterRefactor(args));
  }
}

