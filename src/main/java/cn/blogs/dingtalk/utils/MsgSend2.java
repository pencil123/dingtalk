package cn.blogs.dingtalk.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class MsgSend2 {
    @Value("${dingtalk.url}")
    public String WEBHOOK_TOKEN;

    private Logger logger = LoggerFactory.getLogger(MsgSend2.class);

    public String send(JSONObject message) {
        String returnValue = "这是默认返回值，接口调用失败";
        CloseableHttpClient httpclient = HttpClients.createDefault();

        HttpPost httpPost = new HttpPost(WEBHOOK_TOKEN);

        logger.info(message.toJSONString());
        StringEntity requestEntity = new StringEntity(message.toJSONString(),"utf-8");
        requestEntity.setContentEncoding("UTF-8");
        httpPost.setHeader("Content-type", "application/json");
        httpPost.setEntity(requestEntity);

        try{
            HttpResponse response  = httpclient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
            returnValue = EntityUtils.toString(entity);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        logger.info("return info : {}",returnValue);
        try {
            httpclient.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return returnValue;
    }
}
