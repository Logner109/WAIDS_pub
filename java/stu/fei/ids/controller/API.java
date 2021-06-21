package stu.fei.ids.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import stu.fei.ids.item.Request;
import stu.fei.ids.item.ScoreResult;
import stu.fei.ids.service.RequestService;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Hashtable;


@Controller
public class API {
    private final RequestService requestService;

    @Autowired
    public API(RequestService requestService){
        this.requestService = requestService;
    }

    //Receive POST request on http://localhost:8080/api/process
    @PostMapping(value = "/api/process")
    public String message(@RequestBody String requestBody) throws IOException{
        //Decode request
        try{
            requestBody = java.net.URLDecoder.decode(requestBody, StandardCharsets.UTF_8.name());
        }catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        //Split body for ip and request
        Hashtable<String, String> pre_parsed = requestService.pre_parse(requestBody);
        //Check content length
        String req = pre_parsed.get("request");
        req = requestService.content_length(req);
        //Score request as one string
        ScoreResult score_result = requestService.score_request(req);
        //Create request object
        Request request = requestService.parse_message(req, pre_parsed.get("ip"), score_result);
        //Add request to database
        requestService.add_request_toDB(request);

        return "api";
    }

    //@RequestGET(produses.media type JSON value)
    //ResponseEntity<?> asd

}
