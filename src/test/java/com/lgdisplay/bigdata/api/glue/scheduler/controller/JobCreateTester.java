package com.lgdisplay.bigdata.api.glue.scheduler.controller;


        import org.apache.http.HttpEntity;
        import org.apache.http.HttpResponse;
        import org.apache.http.NameValuePair;
        import org.apache.http.client.HttpClient;
        import org.apache.http.client.entity.UrlEncodedFormEntity;
        import org.apache.http.client.methods.HttpPost;
        import org.apache.http.impl.client.HttpClients;
        import org.apache.http.message.BasicNameValuePair;
        import org.json.JSONObject;
        import org.springframework.http.ResponseEntity;
        import org.springframework.web.client.RestTemplate;

        import java.io.IOException;
        import java.io.InputStream;
        import java.io.UnsupportedEncodingException;
        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.List;
        import java.util.Map;

public class JobCreateTester {

    public static void main(String args[]) throws IOException {

        RestTemplate restTemplate=new RestTemplate();
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("userName", "admin");
        paramsMap.put("jobName", "sample1");

        //job 등록
        //String jobSchedulerUrl = "http://localhost:8889/service/scheduler/job";
        //job kill
        //String jobSchedulerUrl = "http://localhost:8889/service/scheduler/job/kill/1619438925298";
        //job 삭제
        //String jobSchedulerUrl = "http://localhost:8889/service/scheduler/job/1619438878973";


        //job 등록
        //ResponseEntity<String> responseEntity = restTemplate.postForEntity(jobSchedulerUrl, paramsMap, String.class);
        //System.out.println(responseEntity.getBody());
        //job kill
        //restTemplate.delete(jobSchedulerUrl, paramsMap);
        //job 삭제
        //restTemplate.delete(jobSchedulerUrl, paramsMap);


    }

}
