package cn.blogs.dingtalk.utils;

import cn.blogs.dingtalk.controller.Prometheus;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author lyzhang
 * @since 2019/12/2 16:04
 */
@Component
public class MsgSend {

  @Value("${dingtalk.url}")
  public String WEBHOOK_TOKEN;

  private Logger logger = LoggerFactory.getLogger(MsgSend.class);

  public  boolean send(JSONObject message) {
    PrintWriter out = null;
    BufferedReader in = null;
    String result = "";
    logger.info(WEBHOOK_TOKEN);
    try {
      URL realUrl = new URL(WEBHOOK_TOKEN);
      // 打开和URL之间的连接
      URLConnection conn = realUrl.openConnection();
      // 设置通用的请求属性 请求头
      conn.setRequestProperty("accept", "*/*");
      conn.setRequestProperty("connection", "Keep-Alive");
      conn.setRequestProperty("user-agent",
                              "Fiddler");
      conn.setRequestProperty("Content-type", "application/json;charset=UTF-8");

      // 发送POST请求必须设置如下两行
      conn.setDoOutput(true);
      conn.setDoInput(true);
      // 获取URLConnection对象对应的输出流
      out = new PrintWriter(conn.getOutputStream());
      // 发送请求参数
      logger.info(message.toJSONString());
      out.print(JSON.toJSONString(message));
      // flush输出流的缓冲
      out.flush();
      // 定义BufferedReader输入流来读取URL的响应
      in = new BufferedReader(
          new InputStreamReader(conn.getInputStream()));
      String line;
      while ((line = in.readLine()) != null) {
        result += line;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    //使用finally块来关闭输出流、输入流
    finally {
      try {
        if (out != null) {
          out.close();
        }
        if (in != null) {
          in.close();
        }
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }
    return true;
  }

}
