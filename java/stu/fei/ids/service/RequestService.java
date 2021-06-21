package stu.fei.ids.service;

import org.springframework.stereotype.Service;
import stu.fei.ids.database.DatabaseRequests;
import stu.fei.ids.item.Request;
import stu.fei.ids.item.ScoreResult;
import stu.fei.ids.process.Parser;
import stu.fei.ids.process.ScoreEngine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

@Service
public class RequestService {

    public Hashtable<String, String> pre_parse(String input){
        Parser parser = new Parser();
        return parser.prepare(input);
    }

    public Request parse_message(String message, String ip, ScoreResult score_result) throws IOException {
        Parser parser = new Parser();
        return parser.parse(message, ip, score_result);
    }

    public ScoreResult score_request(String request){
        ScoreEngine scoreEngine = new ScoreEngine();
        return scoreEngine.score(request);
    }

    public void add_request_toDB(Request request){
        DatabaseRequests.insert_request(request);
    }

    public List<Request> get_traffic(Map<String, String> query, int page){
        List<Request> selected_traffic = new ArrayList<>();
        DatabaseRequests.get_requests(selected_traffic, query, page);
        return selected_traffic;
    }

    public String content_length(String request){
        Parser parser = new Parser();
        return parser.cut_by_length(request);
    }

}
