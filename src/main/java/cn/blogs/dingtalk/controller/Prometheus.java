package cn.blogs.dingtalk.controller;

import cn.blogs.dingtalk.utils.MsgSend;
import cn.blogs.dingtalk.utils.MsgSend2;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

  @RequestMapping(value = "/receive", method = RequestMethod.POST)
  public String alterMsgRev(HttpServletRequest request){
    editJSON(getJSONParam(request));
    return "Success";

  }

  public JSONObject getJSONParam(HttpServletRequest request){
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

  public boolean editJSON(JSONObject json) {
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



