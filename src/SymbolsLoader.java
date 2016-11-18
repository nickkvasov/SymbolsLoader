/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import org.json.simple.*;
import org.json.simple.parser.*;

/**
 *
 * @author Nick
 */
public class SymbolsLoader {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {
        Calendar today = new GregorianCalendar();
        Calendar back5Y = new GregorianCalendar();back5Y.roll(Calendar.YEAR, -5);
        System.out.println("date from:" + back5Y.getTime() + " date to:" + today.getTime());

        //DateFormat fromPart = new SimpleDateFormat("a=YY&b=MM&c=DD");
        //System.out.println("fromPart:" + fromPart.format(back5Y));

        String yahooFinanceUrl = "http://chart.finance.yahoo.com/table.csv?g=d&ignore=.csv&a=" + back5Y.get(Calendar.MONTH)+"&b="+back5Y.get(Calendar.DAY_OF_MONTH)+"&c="+back5Y.get(Calendar.YEAR)+"&d=" + today.get(Calendar.MONTH)+"&e="+today.get(Calendar.DAY_OF_MONTH)+"&f="+today.get(Calendar.YEAR);
        System.out.println("yahooPart:" + yahooFinanceUrl);

        
        
        String fileName = "StockGroupsInit.json";
        InputStream is = ClassLoader.getSystemResourceAsStream(fileName);
        Reader in = new InputStreamReader(is);
        JSONParser parser = new JSONParser();
        JSONObject init = (JSONObject) parser.parse(in);


        
        System.out.println("init:" + init.get("name"));

        String dirRoot = (String) init.get("directoryRoot");
        System.out.println("dirRoot:" + dirRoot);
        
        JSONArray portfolios = (JSONArray) init.get("portfolios");
        System.out.println("portfolios:" + portfolios.size());
        for(int i = 0; i <portfolios.size();i++){
            JSONObject portfolio = (JSONObject)portfolios.get(i);
            String portfolioName = (String) portfolio.get("name");
            System.out.println("portfolioName:" + portfolioName);
            
            String isymbol = (String) portfolio.get("isymbol");
            System.out.println("2nd iNdex symbol:" + isymbol);
            
            
            JSONArray symbols = (JSONArray) portfolio.get("symbols");
            System.out.println("symbols:" + symbols.size());
            for(int j = 0; j < symbols.size(); j++){
                String symbol = (String)symbols.get(j);
                System.out.println("symbol:" + symbol);
                
                    
                //String yahooFinanceDownloadURL = 
                //HttpDownloadUtility.downloadFile(dirRoot, );
            }
            
            
        }
            
        
    }
    
}
