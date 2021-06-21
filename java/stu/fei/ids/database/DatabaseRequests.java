package stu.fei.ids.database;

import stu.fei.ids.item.Request;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DatabaseRequests {

    public static void get_requests(List<Request> requests, Map<String, String> queryParams, int page){
        try{
            Class.forName("org.sqlite.JDBC");
            Connection c = DriverManager.getConnection("jdbc:sqlite:/home/marek/IDS/ids.db");
            //Create query
            String query = create_statement(queryParams, page);
            //Create Java statement
            PreparedStatement preparedStatement = c.prepareStatement(query);

            //Prepare statement completion
            int cnt = 1;
            if(queryParams.size() > 0){
                if(queryParams.get("from").length() > 0){
                    preparedStatement.setString(cnt, queryParams.get("from"));
                    cnt++;
                }
                if(queryParams.get("to").length() > 0){
                    preparedStatement.setString(cnt, queryParams.get("to"));
                    cnt++;
                }
                if(queryParams.get("score").length() > 0){
                    preparedStatement.setDouble(cnt, Double.parseDouble(queryParams.get("score")));
                    cnt++;
                }
                if(queryParams.get("source_ip").length() > 0){
                    preparedStatement.setString(cnt, queryParams.get("source_ip"));
                    cnt++;
                }
                if(queryParams.get("suspicious").length() > 0){
                    preparedStatement.setBoolean(cnt, Boolean.parseBoolean(queryParams.get("suspicious")));
                    cnt++;
                }
                preparedStatement.setInt(cnt, (page - 1)*25);
            }
            //Execute query, get Java result
            ResultSet rs = preparedStatement.executeQuery();

            //Iterate through the result
            while(rs.next()){
                int id = rs.getInt("id");
                String created = rs.getString("created");
                String method = rs.getString("method");
                String path = rs.getString("path");
                Hashtable<String, String> path_params = string_to_table(rs.getString("path_params"));
                Hashtable<String, String> body_params = string_to_table(rs.getString("body_params"));
                Hashtable<String, String> headers_params = string_to_table(rs.getString("headers_params"));
                List<String> tags = string_to_list(rs.getString("tags"));
                String source_ip = rs.getString("source_ip");
                double score = rs.getDouble("score");
                String version = rs.getString("version");

                Request request = new Request(id, method, path, version, path_params, headers_params,
                        body_params, score, tags, source_ip, created);
                requests.add(request);

            }
            c.close();
        }catch(Exception e){
            System.out.println("DB Service error : " + e);
        }
    }

    public static void insert_request(Request request){
        try{
            Class.forName("org.sqlite.JDBC");
            Connection c = DriverManager.getConnection("jdbc:sqlite:/home/marek/IDS/ids.db");
            //Create query
            String query = "INSERT INTO requests (created, method, path, path_params, " +
                    "body_params, headers_params, tags, suspicious, score, " +
                    "source_ip, version)" +
                    " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            //Create prepared insert statement
            PreparedStatement preparedStatement = c.prepareStatement(query);
            preparedStatement.setString(1, request.getCreated());
            preparedStatement.setString(2, request.getMethod());
            preparedStatement.setString(3, request.getPath());
            preparedStatement.setString(4,
                        (request.getPath_params().size() > 0) ? table_to_string(request.getPath_params()) : null);
            preparedStatement.setString(5,
                    (request.getBody().size() > 0) ? table_to_string(request.getBody()) : null);
            preparedStatement.setString(6, table_to_string(request.getHeaders()));
            preparedStatement.setString(7,
                    (request.getTags().size() > 0) ? list_to_string(request.getTags()) : null);
            preparedStatement.setBoolean(8, request.getTags().size() > 0);
            preparedStatement.setDouble(9, request.getScore());
            preparedStatement.setString(10, request.getIp());
            preparedStatement.setString(11, request.getVersion());
            //Execute query
            preparedStatement.execute();
            c.close();
        }catch(Exception e){
            System.out.println("DB Service error : " + e);
        }
    }

    private static String list_to_string(List<String> list){
        StringBuilder retVal = new StringBuilder();
        for(String s : list){
            retVal.append(s);
            retVal.append(",");
        }
        return retVal.toString();
    }

    private static String table_to_string(Hashtable<String, String> table){
        StringBuilder retVal = new StringBuilder();
        for(String s : table.keySet()){
            retVal.append(s);
            retVal.append(":::");
            retVal.append(table.get(s));
            retVal.append("&&&");
        }
        return retVal.toString();
    }

    private static Hashtable<String, String> string_to_table(String string){
        Hashtable<String, String> ret = new Hashtable<>();
        if(string != null && string.length() > 0){
            String[] pairs = string.split("&&&");
            for(String pair : pairs){
                if(pair.length() > 0){
                    String[] values = pair.split(":::");
                    if(values.length == 1)
                        ret.put(values[0], "");
                    else
                        ret.put(values[0], values[1]);
                }
            }
        }
        return ret;
    }

    private static List<String> string_to_list(String string){
        List<String> ret = new ArrayList<>();
        if(string != null && string.length() > 0){
            String[] s = string.split(",");
            ret.addAll(Arrays.asList(s));
        }
        return ret;
    }

    public static String date_to_string(LocalDateTime date){
        String created = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(date);
        created = created.replace("T", " ");
        created = created.substring(0, 19);
        return created;
    }

    private static String create_statement(Map<String, String> queryParams, int page){
        StringBuilder query = new StringBuilder();
        boolean hasPrevious = false;
        query.append("SELECT * FROM requests ");

        if(queryParams.get("from").length() > 0){
            hasPrevious = true;
            query.append("WHERE datetime(created) >= ? ");
        }
        if(queryParams.get("to").length() > 0){
            if(hasPrevious)
                query.append("AND datetime(created) <= ? ");
            else{
                hasPrevious = true;
                query.append("WHERE datetime(created) <= ? ");
            }
        }
        if(queryParams.get("score").length() > 0){
            if(hasPrevious)
                query.append("AND score = ? ");
            else{
                hasPrevious = true;
                query.append("WHERE score = ? ");
            }
        }
        if(queryParams.get("source_ip").length() > 0){
            if(hasPrevious)
                query.append("AND source_ip = ? ");
            else{
                query.append("WHERE source_ip = ? ");
                hasPrevious = true;
            }

        }
        if(queryParams.get("suspicious").length() > 0){
            if(hasPrevious)
                query.append("AND suspicious = ?");
            else{
                query.append("WHERE suspicious = ?");
            }

        }

        if(page > 0){
            query.append("ORDER BY datetime(created) DESC LIMIT 25 OFFSET ?");
        }

        return query.toString();
    }
}
