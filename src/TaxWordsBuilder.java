import com.sun.imageio.plugins.common.InputStreamAdapter;
import org.ansj.domain.Result;
import org.ansj.splitWord.analysis.ToAnalysis;

import java.io.*;
import java.sql.*;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by WIWANG on 2017/5/20.
 * TaxInQuery
 */
public class TaxWordsBuilder {
    public static void main(String[] args) {
        Connection conn = null;
        try {

            //convert ansj dic to keywords
//            BufferedReader bf = new BufferedReader(new FileReader("tax.dic"));
//            HashSet<String> keywords = new HashSet<String>(400);
//            String s;
//            while ((s = bf.readLine()) != null) {
//                String[] words=s.split("\\s");
//                //System.out.print(words[0]+words[1]+words[2]);
//                keywords.add(words[0]);
//
//
//            }


            BufferedReader bf = new BufferedReader(new FileReader("keywords.txt"));
            HashSet<String> keywords = new HashSet<String>(400);
            String existing_word;
            while ((existing_word = bf.readLine()) != null) {
                keywords.add(existing_word);
            }


            // create a connection to the database and run query
            String url = "jdbc:sqlite:../taxfts.db";
            conn = DriverManager.getConnection(url);
            System.out.println("Connection to SQLite has been established.");
            String sql = "select JHZT from bszn12366";
            Statement stmt  = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);
            // loop through the result set
            while (rs.next()) {

                String[] words=(rs.getString("JHZT")).replace((char) 12288, ' ').split("\\s");
                for(String word:words){
                 if(word.length()>2){
                     if(word.length()<6)keywords.add(word);

                     //grind to smaller parts
                     String[] splitted  = ToAnalysis.parse(word).toStringWithOutNature().split(",");
                     for(String atom_keyword:splitted) if (atom_keyword.length()>1) keywords.add(atom_keyword);
                 }
                }

            }
            BufferedWriter out = new BufferedWriter(new FileWriter("keywords_before_freqscan"));

            Iterator it = keywords.iterator(); // why capital "M"?
            while(it.hasNext()) {
                out.write(it.next()+"\n");
            }

 //  generates a raw_text for crawler
//            sql = "select BSZNBT,BSZNZCYJ from bszn12366";
//            rs=stmt.executeQuery(sql);
//            BufferedWriter raw_text = new BufferedWriter(new FileWriter("raw_text"));
//            while (rs.next()){
//                raw_text.write(rs.getString("BSZNBT")+" "+rs.getString("BSZNZCYJ")+"\n");
//            }




            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
