/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.json.simple.*;
import org.json.simple.parser.*;

/**
 *
 * @author Nick
 */
public class SymbolsLoader {

    public static final String iMain = "SPY";
    public static final String iMainName = "SPDR S&P 500";
    
    public static final String initDir = "./init/";
    
    
    public static final String stockGroupsInitFile = "StockGroupsInit.json";

    public static final String stockGroupsInitFile_template = "StockGroupsInit_template.json";
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException, ParseException, InvalidFormatException {
        Calendar today = new GregorianCalendar();
        Calendar back5Y = new GregorianCalendar();back5Y.roll(Calendar.YEAR, -5);
        System.out.println("date from:" + back5Y.getTime() + " date to:" + today.getTime());

        String yahooFinanceUrl = "http://chart.finance.yahoo.com/table.csv?g=d&ignore=.csv&a=" + back5Y.get(Calendar.MONTH)+"&b="+back5Y.get(Calendar.DAY_OF_MONTH)+"&c="+back5Y.get(Calendar.YEAR)+"&d=" + today.get(Calendar.MONTH)+"&e="+today.get(Calendar.DAY_OF_MONTH)+"&f="+today.get(Calendar.YEAR);
        System.out.println("yahooPart:" + yahooFinanceUrl);

        
        SPDRFundsStockGroupsInit.prepare();
        
        // StocksIndexInit.prepare();
        
        IndividualStocksIndexInit.prepare();
        
        //System.exit(0);
        
        
        InputStream is = Files.newInputStream((new File(initDir+stockGroupsInitFile)).toPath(), StandardOpenOption.READ);// ClassLoader.getSystemResourceAsStream(stockGroupsInitFile);
        Reader in = new InputStreamReader(is);
        JSONParser parser = new JSONParser();
        JSONObject init = (JSONObject) parser.parse(in);


        
        System.out.println("init:" + init.get("name"));

        String dirRoot = (String) init.get("directoryRoot");
        System.out.println("dirRoot:" + dirRoot);
        
        cleanUpRoot(dirRoot);
        
        downloadYahooFinanceHistoryForSymbol(yahooFinanceUrl, "!", iMain, iMainName, dirRoot);

        
        JSONArray portfolios = (JSONArray) init.get("portfolios");
        System.out.println("portfolios:" + portfolios.size());
        for(int i = 0; i <portfolios.size();i++){
            JSONObject portfolio = (JSONObject)portfolios.get(i);
            String portfolioName = (String) portfolio.get("name");
            System.out.println("portfolioName:" + portfolioName);
            
            
            
            String isymbol = (String) portfolio.get("isymbol");
            String iprefix = "";
            iprefix = (String) portfolio.get("iprefix");
            if(iprefix==null){
                iprefix="";
            }

            System.out.println("2nd iNdex symbol:" + isymbol);
            String dirPortfolio = dirRoot+"\\" + iprefix + isymbol;

            downloadYahooFinanceHistoryForSymbol(yahooFinanceUrl,"_", isymbol, isymbol, dirPortfolio);
            
            JSONArray symbols = (JSONArray) portfolio.get("symbols");
            System.out.println("symbols:" + symbols.size());
            
            for(int j = 0; j < symbols.size(); j++){
                String symbol = (String)symbols.get(j);
                System.out.println("symbol:" + symbol);
                
                downloadYahooFinanceHistoryForSymbol(yahooFinanceUrl, "", symbol, symbol, dirPortfolio);
                    
                //String yahooFinanceDownloadURL = 
                //HttpDownloadUtility.downloadFile(dirRoot, );
            }
            
            
        }
            
        
    }

    
    
    public static void cleanUpRoot(String dirRoot) throws IOException {
        //cleanup all
        File dirRootFile = new File(dirRoot);
        Files.walkFileTree(dirRootFile.toPath(),
                new FileVisitor<Path>(){
                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                        return FileVisitResult.CONTINUE;
                    }
                    
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Files.deleteIfExists(file);
                        return FileVisitResult.CONTINUE;
                    }
                    
                    @Override
                    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                        return FileVisitResult.CONTINUE;
                    }
                    
                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        Files.deleteIfExists(dir);
                        return FileVisitResult.CONTINUE;
                    }
                });
    }

    public static void downloadYahooFinanceHistoryForSymbol(String yahooFinanceUrl, String filePrefix, String symbol, String symbolName, String dir) throws IOException {
        String yahooHistorySymbol = yahooFinanceUrl+"&s=" + symbol;
        File file = HttpDownloadUtility.downloadFile(yahooHistorySymbol, dir, filePrefix + symbol+".csv");
        if(file!=null){
            YahooFinancialHistoryFilesReverter.revert(symbolName, file);
        }
    }
    
}
