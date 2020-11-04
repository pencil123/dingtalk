package cn.blogs.dingtalk.controller;

import cn.blogs.dingtalk.rabbitmq.BasicPublisher;
import cn.blogs.dingtalk.utils.MsgSend;
import cn.blogs.dingtalk.utils.MsgSend2;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lyzhang
 * @since 2019/11/29 15:43
 */
@RestController
@RequestMapping("/")
public class Prometheus {

  private Logger logger = LoggerFactory.getLogger(Prometheus.class);

  @Autowired
  private MsgSend2 msgSend;
  @Autowired
  private BasicPublisher basicPublisher;

  @RequestMapping(value = "/receive", method = RequestMethod.POST)
  public String alterMsgRev(HttpServletRequest request){
    editJSON(getJSONParam(request));
    return "Success";

  }

  @Autowired
  RabbitTemplate rabbitTemplate;  //使用RabbitTemplate,这提供了接收/发送等等方法

  @GetMapping("/sendDirectMessage")
  public String sendDirectMessage() {
    String messageId = String.valueOf(UUID.randomUUID());
    String messageData = "test message, hello!";
    String createTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    Map<String,Object> map=new HashMap<>();
    map.put("messageId",messageId);
    map.put("messageData",messageData);
    map.put("createTime",createTime);
    //将消息携带绑定键值：TestDirectRouting 发送到交换机TestDirectExchange
    rabbitTemplate.convertAndSend("TestDirectExchange", "TestDirectRouting", map);
    return "ok";
  }

  private JSONObject getJSONParam(HttpServletRequest request){
    JSONObject jsonParam = null;
    try {
      // 获取输入流
      BufferedReader
          streamReader = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));

      // 写入数据到Stringbuilder
      StringBuilder sb = new StringBuilder();
      String line = null;
      while ((line = streamReader.readLine()) != null) {
        sb.append(line);
      }
      jsonParam = JSONObject.parseObject(sb.toString());
      // 直接将json信息打印出来
      logger.info(jsonParam.toJSONString());
    } catch (Exception e) {
      e.printStackTrace();
    }
    return jsonParam;
  }

  private boolean editJSON(JSONObject json) {
    JSONObject messageSingle = new JSONObject();
    JSONObject messageLink = new JSONObject();

    JSONArray jsonArray = json.getJSONArray("alerts");
    for (int x = 0; x < jsonArray.size(); x++) {
      JSONObject alertMessage = (JSONObject) jsonArray.get(x);
      messageLink.put("text","JobName:" + alertMessage.getJSONObject("labels").getString("job") + " AlertName:" + alertMessage.getJSONObject("labels").getString("alertname"));
      messageLink.put("title",alertMessage.getJSONObject("annotations").getString("description"));
      messageLink.put("messageUrl",alertMessage.getString("generatorURL").replace("prometheus-k8s-1:9090","10.250.12.3:31000"));
      messageSingle.put("msgtype","link");
      messageSingle.put("link",messageLink);
      msgSend.send(messageSingle);
      messageLink.clear();
      messageSingle.clear();
    }
    return  true;
  }
}



