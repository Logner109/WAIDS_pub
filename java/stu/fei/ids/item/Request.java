package stu.fei.ids.item;

import lombok.Data;

import java.util.Hashtable;
import java.util.List;

@Data
public class Request {

    private int id;
    private String method;
    private String path;
    private String version;
    private Hashtable<String, String> path_params;
    private Hashtable<String, String> headers;
    private Hashtable<String, String> body;
    private double score;
    private List<String> tags;
    private String ip;
    private String created;

    public Request(String method, String path, String version, Hashtable<String, String> headers,
                   Hashtable<String, String> body, Hashtable<String, String> path_params,
                   String ip, String created, double score, List<String> tags){
        this.method = method;
        this.path = path;
        this.version = version;
        this.headers = headers;
        this.body = body;
        this.score = score;
        this.tags = tags;
        this.path_params = path_params;
        this.ip = ip;
        this.created = created;
    }

    public Request(int id, String method, String path, String version, Hashtable<String, String> path_params,
                   Hashtable<String, String> headers, Hashtable<String, String> body, double score,
                   List<String> tags, String ip, String created){
        this.id = id;
        this.method = method;
        this.path = path;
        this.version = version;
        this.path_params = path_params;
        this.headers = headers;
        this.body = body;
        this.score = score;
        this.tags = tags;
        this.ip = ip;
        this.created = created;
    }
}
