
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Nick
 */
public class IndividualStocksIndexInit {
    
    public static void prepare() throws IOException, ParseException{
        String fileName = "IndividualStocksInit.json";
        InputStream is = Files.newInputStream((new File(SymbolsLoader.initDir + fileName)).toPath(), StandardOpenOption.READ);//ClassLoader.getSystemResourceAsStream(fileName);
        Reader in = new InputStreamReader(is);
        JSONParser parser = new JSONParser();
        JSONObject spdrInit = (JSONObject) parser.parse(in);

        JSONArray stocks = (JSONArray) spdrInit.get("stocks");

        String prefix = (String)spdrInit.get("prefix");
        
        System.out.println("stocks:" + stocks.size());


        
        InputStream is1 = Files.newInputStream((new File(SymbolsLoader.initDir + SymbolsLoader.stockGroupsInitFile)).toPath(), StandardOpenOption.READ); //ClassLoader.getSystemResourceAsStream(SymbolsLoader.stockGroupsInitFile_template);
        Reader in1 = new InputStreamReader(is1);
        JSONParser parser1 = new JSONParser();
        JSONObject init = (JSONObject) parser.parse(in1);

        JSONArray portfolios = (JSONArray) init.get("portfolios");
        System.out.println("portfolios:" + portfolios.size());
        
        for(int i = 0; i < stocks.size(); i++){
            JSONObject stock = (JSONObject) stocks.get(i);
            
            String symbol = (String) stock.get("stock");
            
            JSONArray indexes = (JSONArray) stock.get("indexes");
            
            Map portfolio = new HashMap();
            
            portfolio.put("isymbol", symbol);
            portfolio.put("iprefix", prefix);
            portfolio.put("symbols", indexes.toArray());
            
            portfolios.add(portfolio);
        }
        
        
        String inits = init.toJSONString();
        BufferedWriter writer = Files.newBufferedWriter((new File(SymbolsLoader.initDir + SymbolsLoader.stockGroupsInitFile)).toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        writer.append(inits);
        writer.flush();
        
    }
    
}
