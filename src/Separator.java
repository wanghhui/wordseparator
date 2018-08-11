import org.ansj.app.keyword.KeyWordComputer;
import org.ansj.app.keyword.Keyword;
import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.library.DicLibrary;
import org.ansj.splitWord.Analysis;
import org.ansj.splitWord.analysis.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.sql.*;
import java.util.Collection;
import java.util.List;

/**
 * Created by WIWANG on 2017/4/20.
 */
public class Separator {
    public static void process12366(Connection conn) throws SQLException, IOException {
        String sql = "select BSZNID, BSZNBT, BSZNYWGS from bszn12366 where rowid";
        String sqlinsert = "INSERT INTO taxqafts2(ftsid,title,content) VALUES(?,?,?)";

             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql);

            // loop through the result set
            while (rs.next()) {
                PreparedStatement pstmt = conn.prepareStatement(sqlinsert);
                pstmt.setString(1,rs.getString("BSZNID"));
                pstmt.setString(2,parse(rs.getString("BSZNBT")));
                pstmt.setString(3,parse(rs.getString("BSZNYWGS")));
                pstmt.executeUpdate();

            }
    }
    public static String parse(String str) throws IOException {
        Analysis udf = new DicAnalysis(new StringReader(str));
        Term term = null ;
        StringBuffer result = new StringBuffer();
        while((term=udf.next())!=null){
            result.append(term.getName()+" ");
        }
        return result.toString();
    }
    public static void main(String[] args) {
        Connection conn = null;
        try {
            BufferedReader bf = new BufferedReader(new FileReader("keywords_newgen.txt"));
            String s;
            while ((s = bf.readLine()) != null) {

                DicLibrary.insert(DicLibrary.DEFAULT,s,"tax",1000000 );
            }
            // db parameters
            String url = "jdbc:sqlite:d:/workspace/taxfts.db";
            // create a connection to the database
            conn = DriverManager.getConnection(url);

            System.out.println("Connection to SQLite has been established.");

            process12366(conn);


//
//            String str = "我想知道，请问纳税人办税免税流程和一些具体的办理方法特许权使用费";
//
//            StringBuffer matchingclause = new StringBuffer(15);
//            for(Term term : DicAnalysis.parse(str)){
//                if(term.natrue().natureStr.equalsIgnoreCase("tax"))matchingclause.append(term.getName()+" ");
//            }
//            System.out.println(matchingclause);
//
//
//
//
//            KeyWordComputer kwc = new KeyWordComputer(8);
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
