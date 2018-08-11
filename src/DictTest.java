import org.ansj.app.keyword.KeyWordComputer;
import org.ansj.app.keyword.Keyword;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.ansj.library.DicLibrary;
import org.ansj.splitWord.analysis.ToAnalysis;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by WIWANG on 2017/4/29.
 */
public class DictTest {

    public static void main(String[] args) {
        Connection conn = null;
        try {
//            for (Map.Entry<String, String[]> entry:DicLibrary.get("dic").toMap().entrySet()){
//                System.out.println(entry.getKey());
//            }

//            // db parameters
//            String url = "jdbc:sqlite:d:/workspace/taxfts.db";
//            // create a connection to the database
//            conn = DriverManager.getConnection(url);
//
//            System.out.println("Connection to SQLite has been established.");


            String str = "我想知道，请问纳税人办税免税流程和一些具体的办理方法税控经营性领用停购";
            System.out.println(NlpAnalysis.parse(str));






//            DicLibrary.insert(DicLibrary.DEFAULT,"纳税人办税","userDefine",1000);
            System.out.println(ToAnalysis.parse(str));

//            KeyWordComputer kwc = new KeyWordComputer(5);
//            Collection<Keyword> result = kwc.computeArticleTfidf(str,str);
//            System.out.println(result);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }  finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}
