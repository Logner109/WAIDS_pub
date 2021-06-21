package stu.fei.ids.process;

import org.springframework.http.converter.HttpMessageConversionException;
import stu.fei.ids.database.DatabaseRequests;
import stu.fei.ids.item.Request;
import stu.fei.ids.item.ScoreResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDateTime;
import java.util.Hashtable;

public class Parser {

    private final Hashtable<String, String> method;//Keys: method, path, version
    private final Hashtable<String , String> requestHeaders;
    private final Hashtable<String, String> messageBody;
    private Hashtable<String, String> path_params;

    public Parser(){
        this.messageBody = new Hashtable<>(); // Param : Value
        this.requestHeaders = new Hashtable<>(); // Header : Value
        this.method = new Hashtable<>(); // Method : Value, Path : Value
        this.path_params = new Hashtable<>();
    }

    public Request parse(String message, String ip, ScoreResult score_result) throws IOException {

        //System.out.println(message);
        BufferedReader reader = new BufferedReader(new StringReader(message));

        String method = reader.readLine();
        appendMethod(method);

        path_params = parse_path(this.method.get("path"));

        String header = reader.readLine();
        while(header != null && header.length() > 0){
            appendHeaderParameter(header);
            header = reader.readLine();
        }

        if(this.method.get("method") != null && this.method.get("method").equals("POST")){
            String bodyLine = reader.readLine();
            while(bodyLine != null){
                appendMessageBody(bodyLine);
                bodyLine = reader.readLine();
            }
        }

        //print_request();

        return new Request(this.method.get("method"), this.method.get("path"),
                this.method.get("version"), this.requestHeaders, this.messageBody,
                path_params, ip, DatabaseRequests.date_to_string(LocalDateTime.now()),
                score_result.getFinal_score(), score_result.getTags());
    }

    public Hashtable<String, String> prepare(String input){
        Hashtable<String, String> result = new Hashtable<>();
        int r_idx = input.indexOf("&r=") + 3;

        if(r_idx > 2){
            result.put("ip", input.substring(2, r_idx - 3));
        }else
            result.put("ip", "null");

        result.put("request", input.substring(r_idx));

        return result;
    }

    private void appendHeaderParameter(String header) throws HttpMessageConversionException{
        int idx = header.indexOf(":");
        if(idx == -1){
            throw new HttpMessageConversionException("Invalid Header Parameter :" + header);
        }
        requestHeaders.put(header.substring(0, idx), header.substring(idx+1));
    }

    private void appendMethod(String line){
        int first = line.indexOf(" ");
        if(first != -1){
            this.method.put("method", line.substring(0, first));
            int last = 0;
            for(int i = 0; i < line.length(); i++){
                if(line.charAt(i) == ' ')
                    last = i;
            }
            if(first+1 < last){
                this.method.put("path", line.substring(first + 1, last));
                this.method.put("version", line.substring(last + 1));
            }
        }else{
            this.method.put("path", "null");
            this.method.put("version", "null");
        }


    }

    private void appendMessageBody(String bodyLine){
        String[] params = bodyLine.split("&");
        for(String s : params){
            int idx = s.indexOf("=");
            if(idx > -1){
                String p = s.substring(0, idx);
                String v = s.substring(idx+1);
                this.messageBody.put(p, v);
            }
        }
    }

    private void print_request(){
        System.out.println("-------------------------------HEADERS-------------------------------");
        for(String s : this.method.keySet()){
            System.out.println(s + this.method.get(s));
        }
        for (String s : requestHeaders.keySet()){
            System.out.println(s + requestHeaders.get(s));
        }

        if(this.method.get("method").equals("POST")){
            System.out.println("----------BODY------------");
            for(String s : messageBody.keySet()){
                System.out.println(s + " : " + messageBody.get(s));
            }
        }
        System.out.println("----------------------------------------------------------------------");
    }

    private Hashtable<String, String> parse_path(String path){
        Hashtable<String, String> result = new Hashtable<>();
        int idx = path.indexOf("?");
        if(idx != -1 && path.length() > 0){
            String suffix = path.substring(idx+1);
            String[] params = suffix.split("&");
            for(String s : params){
                idx = s.indexOf("=");
                if(idx != -1)
                    result.put(s.substring(0, idx), s.substring(idx+1));
            }
        }
        return result;
    }

    public String cut_by_length(String request){
        if(request.indexOf("POST") == 0){
            int idx = request.indexOf("Content-Length: ") + 16;
            StringBuilder len = new StringBuilder();
            while(request.charAt(idx) != '\r'){
                len.append(request.charAt(idx));
                idx++;
            }
            int idx_body = request.indexOf("\r\n\r\n") + 4;
            String r;
            int i = idx_body + Integer.parseInt(len.toString());
            if (i <= request.length() && i != -1){
                r = request.substring(0, i);
                return r;
            }
            return request;

        }
        int idx2 = request.indexOf("\r\n\r\n");
        if(idx2 != -1)
            return request.substring(0, idx2);
        return request;
    }
}
