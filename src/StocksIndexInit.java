
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
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
public class StocksIndexInit {
    
    public static void prepare() throws IOException, ParseException{
        String fileName = "StocksIndexMappings.json";
        InputStream is = Files.newInputStream((new File(SymbolsLoader.initDir + fileName)).toPath(), StandardOpenOption.READ);//ClassLoader.getSystemResourceAsStream(fileName);
        Reader in = new InputStreamReader(is);
        JSONParser parser = new JSONParser();
        JSONObject spdrInit = (JSONObject) parser.parse(in);

        JSONArray mappings = (JSONArray) spdrInit.get("mappings");
        
        System.out.println("mappings:" + mappings.size());
        
        InputStream is1 = Files.newInputStream((new File(SymbolsLoader.initDir + SymbolsLoader.stockGroupsInitFile)).toPath(), StandardOpenOption.READ); //ClassLoader.getSystemResourceAsStream(SymbolsLoader.stockGroupsInitFile_template);
        Reader in1 = new InputStreamReader(is1);
        JSONParser parser1 = new JSONParser();
        JSONObject init = (JSONObject) parser.parse(in1);

        JSONArray portfolios = (JSONArray) init.get("portfolios");
        System.out.println("portfolios:" + portfolios.size());
        
        for(int i = 0; i < mappings.size(); i++){
            JSONObject mapping = (JSONObject) mappings.get(i);
            String index = (String) mapping.get("index");
            JSONArray stocks = (JSONArray) mapping.get("stocks");
            System.out.println("!!!!!!stocks:" + stocks);
            for(int j = 0; j < portfolios.size(); j++){
                JSONObject portfolio = (JSONObject) portfolios.get(j);
                String isymbol = (String) portfolio.get("isymbol");
                JSONArray symbols = (JSONArray) portfolio.get("symbols");
                if(isymbol.equalsIgnoreCase(index)){
                    symbols.addAll(stocks);
                }
            }
        }

        String inits = init.toJSONString();
        BufferedWriter writer = Files.newBufferedWriter((new File(SymbolsLoader.initDir + SymbolsLoader.stockGroupsInitFile)).toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        writer.append(inits);
        writer.flush();
        
    }
    
}
