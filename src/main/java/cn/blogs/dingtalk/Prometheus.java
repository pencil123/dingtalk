package cn.blogs.dingtalk;

import com.alibaba.fastjson.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.servlet.http.HttpServletRequest;
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

  @RequestMapping(value = "/receive", method = RequestMethod.POST)
  public void alterMsgRev(HttpServletRequest request){
    JSONObject jsonParam = editJSON(getJSONParam(request));
    MsgSend.send(jsonParam);
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
      System.out.println(jsonParam.toJSONString());
    } catch (Exception e) {
      e.printStackTrace();
    }
    return jsonParam;
  }

  public JSONObject editJSON(JSONObject json) {
    JSONObject message = new JSONObject();
    JSONObject text = new JSONObject();
    message.put("msgtype","text");
    JSONObject alert = (JSONObject) json.getJSONArray("alerts").get(0);
    text.put("content",alert.getJSONObject("annotations").getString("message"));
    message.put("text",text);
    return  message;
  }

}



