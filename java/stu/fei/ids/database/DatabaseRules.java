package stu.fei.ids.database;

import stu.fei.ids.item.Rule;

import java.sql.*;
import java.util.List;
import java.util.Map;

public class DatabaseRules {

    //Rule methods
    public static void insert_rule(String regex, String tag, boolean status, String description){

        try{
            Class.forName("org.sqlite.JDBC");
            Connection c = DriverManager.getConnection("jdbc:sqlite:/home/marek/IDS/ids.db");
            //Create query
            String query = "INSERT INTO rules (REGEX, TAG, STATUS, DESCRIPTION)" +
                           " VALUES (?, ?, ?, ?)";
            //Create prepared insert statement
            PreparedStatement preparedStatement = c.prepareStatement(query);
            preparedStatement.setString(1, regex);
            preparedStatement.setString(2, tag);
            preparedStatement.setBoolean(3, status);
            preparedStatement.setString(4, description);
            //Execute query
            preparedStatement.execute();
            //Iterate through the result
            c.close();
        }catch(Exception e){
            System.out.println("DB Service error : " + e);
        }
    }

    public static void get_rules(List<Rule> rules, Map<String, String> queryParams){
        try{
            Class.forName("org.sqlite.JDBC");
            Connection c = DriverManager.getConnection("jdbc:sqlite:/home/marek/IDS/ids.db");
            //Create query
            String query = create_statement(queryParams);
            //Create Java statement
            PreparedStatement preparedStatement = c.prepareStatement(query);

            int cnt = 1;
            if(queryParams.size() > 0){
                if(queryParams.get("tag").length() > 0){
                    preparedStatement.setString(cnt, '%' + queryParams.get("tag") + '%');
                    cnt++;
                }
                if(queryParams.get("status").length() > 0){
                    preparedStatement.setBoolean(cnt, queryParams.get("status").equals("On"));
                }

            }

            //Execute query, get Java result
            ResultSet rs = preparedStatement.executeQuery();
            //Iterate through the result
            while(rs.next()){
                int id = rs.getInt("ID");
                String regex = rs.getString("REGEX");
                String tag = rs.getString("TAG");
                boolean status = rs.getBoolean("STATUS");
                String description = rs.getString("DESCRIPTION");
                Rule rule = new Rule(id, regex, tag, status, description);
                rules.add(rule);
            }
            c.close();
        }catch(Exception e){
            System.out.println("DB Service error : " + e);
        }
    }

    public static void remove_rule(int id){
        try{
            Class.forName("org.sqlite.JDBC");
            Connection c = DriverManager.getConnection("jdbc:sqlite:/home/marek/IDS/ids.db");
            //Create query
            String query = "DELETE FROM rules WHERE ID = ?";
            //Create prepared insert statement
            PreparedStatement preparedStatement = c.prepareStatement(query);
            preparedStatement.setInt(1, id);
            //Execute query
            preparedStatement.executeUpdate();
            //Iterate through the result
            c.close();
        }catch(Exception e){
            System.out.println("DB Service error : " + e);
        }
    }

    public static void change_status(int id, boolean turnOn){
        try{
            Class.forName("org.sqlite.JDBC");
            Connection c = DriverManager.getConnection("jdbc:sqlite:/home/marek/IDS/ids.db");
            //Create query
            String query = "UPDATE rules SET STATUS = ? WHERE ID = ?";
            //Create prepared insert statement
            PreparedStatement preparedStatement = c.prepareStatement(query);
            preparedStatement.setBoolean(1, turnOn);
            preparedStatement.setInt(2, id);
            //Execute query
            preparedStatement.executeUpdate();
            //Iterate through the result
            c.close();
        }catch(Exception e){
            System.out.println("DB Service error : " + e);
        }
    }

    public static Rule get_rule_by_id(int id){
        try{
            Class.forName("org.sqlite.JDBC");
            Connection c = DriverManager.getConnection("jdbc:sqlite:/home/marek/IDS/ids.db");
            //Create query
            String query = "SELECT * FROM rules WHERE ID = ?";
            //Create Java statement
            PreparedStatement preparedStatement = c.prepareStatement(query);
            //Execute query, get Java result
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            //Iterate through the result
            if(rs.next()){
                int ID = rs.getInt("ID");
                String regex = rs.getString("REGEX");
                String tag = rs.getString("TAG");
                boolean status = rs.getBoolean("STATUS");
                String description = rs.getString("DESCRIPTION");
                Rule rule = new Rule(ID, regex, tag, status, description);
                c.close();
                return rule;
            }
            c.close();

        }catch(Exception e){
            System.out.println("DB Service error : " + e);
        }
        return null;
    }

    private static String create_statement(Map<String, String> queryParams){
        StringBuilder query = new StringBuilder();
        boolean hasPrevious = false;
        query.append("SELECT * FROM rules ");

        if(queryParams.get("tag").length() > 0){
            hasPrevious = true;
            query.append("WHERE TAG LIKE ? ");
        }
        if(queryParams.get("status").length() > 0){
            if(hasPrevious)
                query.append("AND STATUS = ? ");
            else{
                query.append("WHERE STATUS = ? ");
            }
        }

        return query.toString();
    }
}
