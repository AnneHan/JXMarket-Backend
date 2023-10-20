package com.hyl.api.interceptor;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.hyl.api.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 从请求体中获取参数请求包装类
 *
 * @author AnneHan
 * @date 2023-09-15
 */
public class XssRequestServletHandleWrapper extends HttpServletRequestWrapper {
  private static final Logger logger =
      LoggerFactory.getLogger(XssRequestServletHandleWrapper.class);

  private String body;

  private Map<String, String[]> params;

  public XssRequestServletHandleWrapper(HttpServletRequest request) {
    super(request);
    this.params = request.getParameterMap();
    body = getInputStreamToString(request);
  }

  public static String getInputStreamToString(HttpServletRequest request) {
    StringBuilder stringBuilder = new StringBuilder();
    try (Reader bufferedReader =
        new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8)) {
      char[] charBuffer = new char[128];
      int bytesRead;
      while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
        stringBuilder.append(charBuffer, 0, bytesRead);
      }
      return stringBuilder.toString();
    } catch (IOException e) {
      logger.error("输入流关闭异常:{}", e.getMessage());
    }
    return null;
  }

  @Override
  public ServletInputStream getInputStream() {
    final ByteArrayInputStream byteArrayInputStream =
        new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8));
    return new ServletInputStream() {
      @Override
      public boolean isFinished() {
        return false;
      }

      @Override
      public boolean isReady() {
        return false;
      }

      @Override
      public void setReadListener(ReadListener readListener) {}

      @Override
      public int read() {
        return byteArrayInputStream.read();
      }
    };
  }

  @Override
  public BufferedReader getReader() {
    return new BufferedReader(new InputStreamReader(this.getInputStream(), StandardCharsets.UTF_8));
  }

  public String getBody() {
    return this.body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public Map<String, String[]> getParams() {
    return params;
  }

  public void setParams(Map<String, String[]> params) {
    this.params = params;
  }

  public static String getParamString(Map<String, String[]> parameterMap) {
    Map<String, Object> reqMap = Maps.newHashMap();
    Set<Map.Entry<String, String[]>> entry = parameterMap.entrySet();
    for (Map.Entry<String, String[]> me : entry) {
      String key = me.getKey();
      String value = me.getValue()[0];
      reqMap.put(key, value);
    }
    TreeMap<String,Object> treeMap = new TreeMap<>(reqMap);
    return JsonUtil.jsonToString(JsonUtil.mapToJson(treeMap));
  }

  @Override
  public String getHeader(String name) {
    String value = super.getHeader(name);
    if (value == null) {
      return null;
    }
    return cleanXSS(value);
  }

  @Override
  public String getParameter(String name) {
    String[] values = params.get(name);
    if (values == null || values.length == 0) {
      return null;
    }
    return values[0];
  }

  @Override
  public String[] getParameterValues(String name) {
    return params.get(name);
  }

  @Override
  public Enumeration<String> getParameterNames() {
    Vector<String> vector = new Vector<>(params.keySet());
    return vector.elements();
  }

  private boolean isJsonStr(String str) {
    try {
      JSONObject.parseObject(str);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  private String cleanXSS(String valueP) {
    String value = valueP.replace("<", "&lt;").replace(">", "&gt;");
    value = value.replaceAll("<", "& lt;").replaceAll(">", "& gt;");
    value = value.replaceAll("\\(", "& #40;").replaceAll("\\)", "& #41;");
    value = value.replaceAll("eval\\((.*)\\)", "");
    value = value.replace("script", "");
    value = value.replace("alert", "");
    value = value.replace("onClick", "");
    value = value.replace("window.", "");
    value = value.replace("document.", "");
    return value;
  }

  private boolean formatJsonValue(String json, Map<String, Object> resultMap) {
    if (!isJsonStr(json)) {
      return true;
    }
    JSONObject jsonObject = JSONObject.parseObject(json);
    String key;
    Object value;
    for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
      key = entry.getKey();
      value = entry.getValue();
      if (null == value) {
        resultMap.put(key, "");
      } else if (value instanceof JSONArray) {
        List<Map<String, Object>> tempList = new ArrayList<>();
        Map<String, Object> tempMap;
        String jsons = value.toString().substring(1, value.toString().lastIndexOf("]"));
        jsons = jsons.replaceAll("}\\s?,\\s?\\{", "}@@|@@{");
        String[] splitArray = jsons.split("@@\\|@@");
        for (String str : splitArray) {
          tempMap = Maps.newHashMap();
          formatJsonValue(str, tempMap);
          tempList.add(tempMap);
        }
        resultMap.put(key, tempList);
      } else if (value instanceof JSONObject) {
        formatJsonValue(value.toString(), resultMap);
      } else {
        resultMap.put(key, value);
      }
    }
    return false;
  }
}
