import org.ansj.dic.LearnTool;
import org.ansj.domain.Nature;
import org.ansj.domain.NewWord;
import org.ansj.splitWord.analysis.IndexAnalysis;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.nlpcn.commons.lang.util.IOUtil;

import java.io.UnsupportedEncodingException;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by WIWANG on 2017/4/29.
 * This class is initially created for building up ansj Dic. But later I decided to use key_hot_tax_word instead a dic because
 * 1,.android can not load the dic automatically
 * 2. xf accepts a words only file
 */

public class DictGenerator {

    public static void main(String[] args) {

        try {
            LearnTool learnTool = new LearnTool() ;

            HashMap<String, Double> loadMap = IOUtil.loadMap("learnTool.snap", IOUtil.UTF8, String.class, Double.class);
            for (Map.Entry<String, Double> entry : loadMap.entrySet()) {
                learnTool.addTerm(new NewWord(entry.getKey(), Nature.NW, entry.getValue())) ;
                learnTool.active(entry.getKey()) ;
            }

            NlpAnalysis nlpAnalysis = new NlpAnalysis().setLearnTool(learnTool);

            // db parameters
            String url = "jdbc:sqlite:d:/workspace/taxfts.db";
            // create a connection to the database
            Connection conn = DriverManager.getConnection(url);

            System.out.println("Connection to SQLite has been established.");
            String sql = "select BSZNBT, BSZNYWGS from bszn12366";
            Statement stmt  = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);
//            while (rs.next()) {
//                String title = rs.getString("BSZNBT");
//                String content = rs.getString("BSZNYWGS");
//                nlpAnalysis.parseStr(title);
//                nlpAnalysis.parseStr(content);
//
//            }

            //System.out.println(learnTool.getTopTree(10));

            /**
             * 将训练结果序列写入到硬盘中
             */
            List<Map.Entry<String, Double>> topTree = learnTool.getTopTree(0);
            StringBuilder sb_snap = new StringBuilder();
            StringBuilder sb_dict = new StringBuilder();
            for (Map.Entry<String, Double> entry : topTree) {
                sb_snap.append(entry.getKey() + "\t" + entry.getValue()+"\n");
                //sb_dict.append(entry.getKey() + "\t" + "tax"+"\t"+"1000"+"\n");
                sb_dict.append(entry.getKey() + "\n");
            }
            IOUtil.Writer("learnTool.snap", IOUtil.UTF8, sb_snap.toString());
            IOUtil.Writer("tax.dic", IOUtil.UTF8, sb_dict.toString());
            //sb. = null;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


    }
}
