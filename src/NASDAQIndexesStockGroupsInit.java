
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
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
public class NASDAQIndexesStockGroupsInit {

    public static void prepare() throws IOException, ParseException, InvalidFormatException {

        String fileName = "NASDAQIndexesInit.json";
        InputStream is = Files.newInputStream((new File(SymbolsLoader.initDir + fileName)).toPath(), StandardOpenOption.READ);//ClassLoader.getSystemResourceAsStream(fileName);
        Reader in = new InputStreamReader(is);
        JSONParser parser = new JSONParser();
        JSONObject spdrInit = (JSONObject) parser.parse(in);

        JSONArray indexes = (JSONArray) spdrInit.get("indexes");

        System.out.println("indexes: " + indexes.size());

        InputStream is1 = Files.newInputStream((new File(SymbolsLoader.initDir + SymbolsLoader.stockGroupsInitFile)).toPath(), StandardOpenOption.READ); //ClassLoader.getSystemResourceAsStream(SymbolsLoader.stockGroupsInitFile_template);
        Reader in1 = new InputStreamReader(is1);
        JSONParser parser1 = new JSONParser();
        JSONObject init = (JSONObject) parser.parse(in1);

        JSONArray portfolios = new JSONArray();
        
        for (int i = 0; i < indexes.size(); i++) {
            Map portfolio = new HashMap();

            String indexCode = (String) indexes.get(i);
            String nasdaqIndexHoldingsUrl = "https://finance.yahoo.com/quote/indexCode/components?p=indexCode";

            File spdrFundXLSFile = HttpDownloadUtility.downloadFile(nasdaqIndexHoldingsUrl, "./temp", indexCode + ".xls");

            FileInputStream file = new FileInputStream(spdrFundXLSFile);
            Workbook wb = WorkbookFactory.create(file);

            Sheet sheet = wb.getSheetAt(0);
            int rowsNum = sheet.getLastRowNum();
            System.out.println("rowsNum: " + rowsNum);

            List<String> fundSymbols = new ArrayList<String>();
            //skip header lines and go for data
            for (int j = 4; j < rowsNum; j++) {
                Row row = sheet.getRow(j);
                short rowSize = row.getLastCellNum();
                if (rowSize > 1) {
                    Cell cell1 = row.getCell(1);
                    String symbol = cell1.getStringCellValue();
                    Cell cell2 = row.getCell(2);
                    String weight = cell2.getStringCellValue();
                    System.out.println("celss: " + symbol + " : " + weight);
                    double numweight = Double.parseDouble(weight);
                    if (numweight > 0.9) {
                        fundSymbols.add(symbol);
                    }
                } else {
                    break;
                }
            }

            portfolio.put("isymbol", indexCode);
            portfolio.put("symbols", fundSymbols.toArray());
            portfolios.add(portfolio);

        }

/*
        JSONArray portfolios = (JSONArray) init.get("portfolios");
        if (portfolios != null) {
            init.remove("portfolios");
        } else {
            portfolios = new JSONArray();
            init.put("portfolios", portfolios);
        }
*/
        init.replace("portfolios", portfolios);
        String inits = init.toJSONString();
        BufferedWriter writer = Files.newBufferedWriter((new File(SymbolsLoader.initDir + SymbolsLoader.stockGroupsInitFile)).toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        writer.append(inits);
        writer.flush();

    }
}
