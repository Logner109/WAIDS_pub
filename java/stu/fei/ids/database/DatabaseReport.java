package stu.fei.ids.database;

import stu.fei.ids.item.Report;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

public class DatabaseReport {
    public static void get_reports(List<Report> reportList){
        try{

            Class.forName("org.sqlite.JDBC");
            Connection c = DriverManager.getConnection("jdbc:sqlite:/home/marek/IDS/ids.db");
            //Create query
            String query = "SELECT * FROM reports";
            //Create Java statement
            Statement stm = c.createStatement();
            //Execute query, get Java result
            ResultSet rs = stm.executeQuery(query);
            //Iterate through the result
            while(rs.next()){
                int id = rs.getInt("id");
                String created = rs.getString("created");
                String frequency = rs.getString("frequency");
                String perform_time = rs.getString("perform_time");
                String format = rs.getString("format");
                String receiver = rs.getString("receiver_email");
                Report report = new Report(id, frequency, perform_time, format, receiver, created);
                reportList.add(report);
            }
            c.close();
        }catch(Exception e){
            System.out.println("DB Service error : " + e);
        }
    }

    public static void insert_report(String frequency, String perform_time, String format, String receiver_email){
        try{
            Class.forName("org.sqlite.JDBC");
            Connection c = DriverManager.getConnection("jdbc:sqlite:/home/marek/IDS/ids.db");
            //Create query
            String query = "INSERT INTO reports (frequency, created, perform_time, format, " +
                    "receiver_email, report_query_map, last_performed, perform)" +
                    " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            //Create prepared insert statement
            PreparedStatement preparedStatement = c.prepareStatement(query);
            preparedStatement.setString(1, frequency);
            preparedStatement.setString(2, DatabaseRequests.date_to_string(LocalDateTime.now()));
            preparedStatement.setString(3, perform_time);
            preparedStatement.setString(4, format);
            preparedStatement.setString(5, receiver_email);
            preparedStatement.setString(6, null);
            preparedStatement.setString(7, null);
            preparedStatement.setString(8, null);
            //Execute query
            preparedStatement.execute();
            //Iterate through the result
            c.close();
        }catch(Exception e){
            System.out.println("DB Service error : " + e);
        }
    }

    public static void remove_report(int id){
        try{
            Class.forName("org.sqlite.JDBC");
            Connection c = DriverManager.getConnection("jdbc:sqlite:/home/marek/IDS/ids.db");
            //Create query
            String query = "DELETE FROM reports WHERE id = ?";
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

    public static void update_reports(){
        //TODO
    }
}
