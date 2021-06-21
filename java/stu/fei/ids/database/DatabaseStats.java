package stu.fei.ids.database;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class DatabaseStats {

    public static Map<String, String> get_stats1(){
        Map<String, String> stats_1 = new HashMap<>();

        //STATS 1
        try{

            Class.forName("org.sqlite.JDBC");
            Connection c = DriverManager.getConnection("jdbc:sqlite:/home/marek/IDS/ids.db");
            String query = "SELECT * FROM stats_1";
            Statement statement = c.createStatement();
            ResultSet rs = statement.executeQuery(query);

            int row = 0;
            while(rs.next()){
                 stats_1.put("s1_attack_type" + row, rs.getString("attack_type"));
                 Integer i = rs.getInt("count");
                 stats_1.put("s1_count" + row, i.toString());
                 row++;
            }
            c.close();
        }catch(Exception e){
            System.out.println("DB Service error : " + e);
        }
        return stats_1;



    }

    public static Map<String, String> get_stats2(){
        Map<String, String> stats_2 = new HashMap<>();
        //STATS 2
        try{

            Class.forName("org.sqlite.JDBC");
            Connection c = DriverManager.getConnection("jdbc:sqlite:/home/marek/IDS/ids.db");
            String query = "SELECT * FROM stats_2";
            Statement statement = c.createStatement();
            ResultSet rs = statement.executeQuery(query);

            if(rs.next()){
                Integer i = rs.getInt("all");
                Integer i2 = rs.getInt("suspicious");
                stats_2.put("s2_all", i.toString());
                stats_2.put("s2_suspicious", i2.toString());
            }
            c.close();
        }catch(Exception e){
            System.out.println("DB Service error : " + e);
        }
        return stats_2;
    }

    public static Map<String, String> get_stats3(){
        Map<String, String> stats_3 = new HashMap<>();
        //STATS 3
        try{

            Class.forName("org.sqlite.JDBC");
            Connection c = DriverManager.getConnection("jdbc:sqlite:/home/marek/IDS/ids.db");
            String query = "SELECT * FROM stats_3";
            Statement statement = c.createStatement();
            ResultSet rs = statement.executeQuery(query);

            int row = 0;
            while (rs.next()){
                Integer i = rs.getInt("count");

                stats_3.put("s3_count" + row, i.toString());
                stats_3.put("s3_source_ip" + row, rs.getString("source_ip"));
                row++;
            }
            c.close();
        }catch(Exception e){
            System.out.println("DB Service error : " + e);
        }
        return stats_3;
    }
}
