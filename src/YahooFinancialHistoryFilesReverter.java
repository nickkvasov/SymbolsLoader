
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Nick
 */
public class YahooFinancialHistoryFilesReverter {
    
    public static File revert(String symbolName, File file) throws IOException{
        List<String> lines = Files.readAllLines(file.toPath());
        System.out.println("lines:" + lines.size());
        BufferedWriter writer = Files.newBufferedWriter(file.toPath(), StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        writer.append("Name").append(",").append(lines.get(0));
        for(int i = lines.size()-1;i>0;i--){
            writer.append(symbolName).append(",").append(lines.get(i));
            writer.append('\n');
        }
        writer.flush();
        return file;
    }
    
}
