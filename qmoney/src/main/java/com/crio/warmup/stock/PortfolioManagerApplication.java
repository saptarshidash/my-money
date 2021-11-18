package com.crio.warmup.stock;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.dto.TotalReturnsDto;
import com.crio.warmup.stock.log.UncaughtExceptionHandler;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.apache.logging.log4j.ThreadContext;
import org.springframework.web.client.RestTemplate;


public class PortfolioManagerApplication {

  // token
  public static final String token = "0bb47f878bcc4ab3e021c1a1452f3375eeb53f26";

  // TODO: CRIO_TASK_MODULE_JSON_PARSING
  //  Task:
  //       - Read the json file provided in the argument[0], The file is available in the classpath.
  //       - Go through all of the trades in the given file,
  //       - Prepare the list of all symbols a portfolio has.
  //       - if "trades.json" has trades like
  //         [{ "symbol": "MSFT"}, { "symbol": "AAPL"}, { "symbol": "GOOGL"}]
  //         Then you should return ["MSFT", "AAPL", "GOOGL"]
  //  Hints:
  //    1. Go through two functions provided - #resolveFileFromResources() and #getObjectMapper
  //       Check if they are of any help to you.
  //    2. Return the list of all symbols in the same order as provided in json.

  //  Note:
  //  1. There can be few unused imports, you will need to fix them to make the build pass.
  //  2. You can use "./gradlew build" to check if your code builds successfully.

  public static List<String> mainReadFile(String[] args) throws IOException, URISyntaxException {

     File tradeFile = resolveFileFromResources(args[0]);
     ObjectMapper mapper = getObjectMapper();
     PortfolioTrade[] trades =  mapper.readValue(tradeFile, PortfolioTrade[].class);

     // extract symbols
     List<String> symbols = new ArrayList<>();

     for(PortfolioTrade trade: trades){
       symbols.add(trade.getSymbol());
     }


     return symbols;

  }


  private static void printJsonObject(Object object) throws IOException {
    Logger logger = Logger.getLogger(PortfolioManagerApplication.class.getCanonicalName());
    ObjectMapper mapper = new ObjectMapper();
    logger.info(mapper.writeValueAsString(object));
  }

  private static File resolveFileFromResources(String filename) throws URISyntaxException {
    return Paths.get(
        Thread.currentThread().getContextClassLoader().getResource(filename).toURI()).toFile();
  }

  private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }


  // TODO: CRIO_TASK_MODULE_JSON_PARSING
  //  Follow the instructions provided in the task documentation and fill up the correct values for
  //  the variables provided. First value is provided for your reference.
  //  A. Put a breakpoint on the first line inside mainReadFile() which says
  //    return Collections.emptyList();
  //  B. Then Debug the test #mainReadFile provided in PortfoliomanagerApplicationTest.java
  //  following the instructions to run the test.
  //  Once you are able to run the test, perform following tasks and record the output as a
  //  String in the function below.
  //  Use this link to see how to evaluate expressions -
  //  https://code.visualstudio.com/docs/editor/debugging#_data-inspection
  //  1. evaluate the value of "args[0]" and set the value
  //     to the variable named valueOfArgument0 (This is implemented for your reference.)
  //  2. In the same window, evaluate the value of expression below and set it
  //  to resultOfResolveFilePathArgs0
  //     expression ==> resolveFileFromResources(args[0])
  //  3. In the same window, evaluate the value of expression below and set it
  //  to toStringOfObjectMapper.
  //  You might see some garbage numbers in the output. Dont worry, its expected.
  //    expression ==> getObjectMapper().toString()
  //  4. Now Go to the debug window and open stack trace. Put the name of the function you see at
  //  second place from top to variable functionNameFromTestFileInStackTrace
  //  5. In the same window, you will see the line number of the function in the stack trace window.
  //  assign the same to lineNumberFromTestFileInStackTrace
  //  Once you are done with above, just run the corresponding test and
  //  make sure its working as expected. use below command to do the same.
  //  ./gradlew test --tests PortfolioManagerApplicationTest.testDebugValues

  public static List<String> debugOutputs() {

     String valueOfArgument0 = "trades.json";
     String resultOfResolveFilePathArgs0 = "";
     String toStringOfObjectMapper = "";
     String functionNameFromTestFileInStackTrace = "";
     String lineNumberFromTestFileInStackTrace = "";


    return Arrays.asList(new String[]{valueOfArgument0, resultOfResolveFilePathArgs0,
        toStringOfObjectMapper, functionNameFromTestFileInStackTrace,
        lineNumberFromTestFileInStackTrace});
  }


  // Note:
  // 1. You may need to copy relevant code from #mainReadQuotes to parse the Json.
  // 2. Remember to get the latest quotes from Tiingo API.





  // TODO: CRIO_TASK_MODULE_REST_API
  //  Find out the closing price of each stock on the end_date and return the list
  //  of all symbols in ascending order by its close value on end date.

  // Note:
  // 1. You may have to register on Tiingo to get the api_token.
  // 2. Look at args parameter and the module instructions carefully.
  // 2. You can copy relevant code from #mainReadFile to parse the Json.
  // 3. Use RestTemplate#getForObject in order to call the API,
  //    and deserialize the results in List<Candle>


  // Note:
  // Remember to confirm that you are getting same results for annualized returns as in Module 3.
  public static List<String> mainReadQuotes(String[] args) throws IOException, URISyntaxException {
    LocalDate endDate = LocalDate.parse(args[1]); // 2nd arg contains end date passed from terminal
    // get user trade details from the json file
    List<PortfolioTrade> tradeDetails = readTradesFromJson(args[0]);

    List<TotalReturnsDto> returnsDtos = new ArrayList<>();

    // hit tinglo endpoint
    RestTemplate restTemplate = new RestTemplate();
    

    for(PortfolioTrade trade: tradeDetails){
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

      if(arg0.getClosingPrice() > arg1.getClosingPrice()) return 1;
      else if(arg0.getClosingPrice() < arg1.getClosingPrice()) return -1;
      else return 0;
      
		}
    });

    // collect the symbols from sorted list
    List<String> sortedSymbols = new ArrayList<>();
    for(TotalReturnsDto returnsDto: returnsDtos){
      sortedSymbols.add(returnsDto.getSymbol());
    }
    
    return sortedSymbols;
  }

  // TODO:
  //  After refactor, make sure that the tests pass by using these two commands
  //  ./gradlew test --tests PortfolioManagerApplicationTest.readTradesFromJson
  //  ./gradlew test --tests PortfolioManagerApplicationTest.mainReadFile
  public static List<PortfolioTrade> readTradesFromJson(String filename) throws IOException, URISyntaxException {
     
    ObjectMapper mapper = getObjectMapper();
    List<PortfolioTrade> trades = mapper.readValue(
      resolveFileFromResources(filename),
      new TypeReference<List<PortfolioTrade>>() {
      });

    return trades;
  }


  // TODO:
  //  Build the Url using given parameters and use this function in your code to call the API.
  public static String prepareUrl(PortfolioTrade trade, LocalDate endDate, String token) {
    String BASE_URL = "https://api.tiingo.com/tiingo/daily/" + trade.getSymbol() + "/prices?";
    String endPoint = BASE_URL+"startDate="+trade.getPurchaseDate()+"&endDate="+endDate+"&token="+token;
    return endPoint ;
  }


  public static void main(String[] args) throws Exception {
    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
    ThreadContext.put("runId", UUID.randomUUID().toString());
  
    printJsonObject(mainReadQuotes(args));
    


  }
}

