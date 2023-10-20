package com.hyl.api.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.hyl.api.config.SingUrlFilterConfig;
import com.hyl.api.util.file.FileUtil;
import com.hyl.common.constants.GlobalConstant;
import com.hyl.common.utils.ApplicationUtils;
import com.hyl.common.utils.HmacSha256Util;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * 非法字符过滤器（防SQL注入，防XSS漏洞）
 *
 * @author AnneHan
 * @date 2023-09-15
 */
public class XssFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(XssFilter.class);


    @Override
    @SuppressWarnings("unchecked")
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        XssRequestServletHandleWrapper requestWrapper = null;
        String body2 = "{}";
        SingUrlFilterConfig singUrlFilterConfig = Objects.requireNonNull(ApplicationUtils.getBean("singUrlFilterConfig", SingUrlFilterConfig.class));
        Set<String> whiteList = singUrlFilterConfig.getUrls();
        String enctype = request.getContentType();
        if (request instanceof HttpServletRequest) {
            String from;
            //表单提交
            if (StringUtils.isNotBlank(enctype) && enctype.contains(GlobalConstant.HTTP_REQUEST_HEADER_MULTIPART_FORM_DATA)) {
                CommonsMultipartResolver commonsMultipartResolver = ApplicationUtils.getBean("multipartResolver", CommonsMultipartResolver.class);
                assert commonsMultipartResolver != null;
                MultipartHttpServletRequest multipartHttpServletRequest = commonsMultipartResolver.resolveMultipart((HttpServletRequest) request);
                validateUploadFileType(multipartHttpServletRequest);
                requestWrapper = new XssRequestServletHandleWrapper(multipartHttpServletRequest);
                Map<String, String[]> parameterMap = requestWrapper.getParameterMap();
                body2 = XssRequestServletHandleWrapper.getParamString(parameterMap);
                String url = getRequestServlet(requestWrapper);
                from = requestWrapper.getHeader("from");
                logger.info("接口{}来源{}请求表单参数:\n{}", url, from, format(body2));
                chain.doFilter(requestWrapper, httpServletResponse);
                return;
            }
            requestWrapper = new XssRequestServletHandleWrapper((HttpServletRequest) request);
            from = requestWrapper.getHeader("from");
            String url = getRequestServlet(requestWrapper);
            Map<String, String[]> parameterMap = requestWrapper.getParameterMap();
            if (null != parameterMap && !parameterMap.isEmpty()) {
                body2 = XssRequestServletHandleWrapper.getParamString(parameterMap);
                logger.info("接口{}来源{}请求表单参数:\n{}", url, from, format(body2));
            }
            String body = requestWrapper.getBody();
            if (StringUtils.isNotBlank(body) && !"{}".equals(body)) {
                logger.info("接口{}来源{}请求json参数:\n{}", url, from, format(body));
            }
            if (!whiteList.contains(url) && singUrlFilterConfig.isEnable()) {
                String timestamp = requestWrapper.getHeader("timestamp");
                String nonce = requestWrapper.getHeader("nonce");
                String signature = requestWrapper.getHeader("signature");
                if (StringUtils.isEmpty(body)) {
                    body = "{}";
                } else {
                    Map<String, Object> jsonMap = JSONObject.parseObject(body, Map.class);
                    TreeMap<String, Object> treeMap = new TreeMap<>(jsonMap);
                    body = JSONObject.toJSONString(treeMap);
                }

                if (null == timestamp || Long.parseLong(timestamp) < (System.currentTimeMillis() - 60 * 60 * 24 * 1000L)) {
                    logger.error("接口{},签名参数:{},时间戳有误,请求失败", url, body + body2);
                    errorResponse(httpServletResponse, "您的签名有误,请求失败", "401");
                    return;
                }
                logger.info(body + body2);
                boolean flg = HmacSha256Util.verifySignature(signature, timestamp, nonce, body + body2);
                System.out.println(HmacSha256Util.signature(timestamp, nonce, body + body2));
                if (!flg) {
                    logger.error("接口{},签名参数:{},签名{}有误", url, body + body2, signature);
                    errorResponse(httpServletResponse, "您的签名有误,请求失败", "401");
                    return;
                }
            }
        }
        if (requestWrapper == null) {
            chain.doFilter(request, httpServletResponse);
        } else {
            chain.doFilter(requestWrapper, httpServletResponse);
        }
    }


    private String getRequestServlet(HttpServletRequest req) {
        String pathInfo = req.getPathInfo() == null ? "" : req.getPathInfo();
        return req.getServletPath() + pathInfo;
    }


    private void validateUploadFileType(MultipartHttpServletRequest multipartRequest) {
        List<MultipartFile> multipartFiles = multipartRequest.getFiles("file");
        if (!multipartFiles.isEmpty()) {
            printFileInfo(multipartFiles);
        } else {
            List<MultipartFile> multipartFiles2 = multipartRequest.getFiles("files");
            printFileInfo(multipartFiles2);
        }
    }

    /**
     * 打印文件大小
     */
    private void printFileInfo(List<MultipartFile> multipartFiles) {
        long totalSize = 0L;
        if (!multipartFiles.isEmpty()) {
            for (MultipartFile multipartFile : multipartFiles) {
                logger.info("文件{}的大小为: {}！\n", multipartFile.getOriginalFilename(), FileUtil.formatFileSize(multipartFile.getSize()));
                totalSize += multipartFile.getSize();
            }
            logger.info("本次文件请求的总大小为:\n {}！", FileUtil.formatFileSize(totalSize));
        } else {
            logger.info("文件上传为空！");
        }
    }


    public void errorResponse(HttpServletResponse response, String message, String code) throws IOException {
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type,X-Requested-With,accept,Origin,Access-Control-Request-Method,Access-Control-Request-Headers,token");
        response.setContentType("application/json; charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("{\"success\":\"f\",\"code\":\"" + code + "\",\"msg\":\"" + message + "\"}");
        out.flush();
        out.close();
    }

    @Override
    public void destroy() {
    }

    @Override
    public void init(FilterConfig filterConfig) {
        //filter 无法直接获取@value的值，因为程序刚刚启动的时候 filter就被加载了 这个时候配置文件还没有被加载到
        /*ServletContext servletContext = filterConfig.getServletContext();
        WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        signatureOpen = Boolean.valueOf(ctx.getEnvironment().getProperty("signature.open"));*/
    }

    private String format(String jsonStr) {
        int level = 0;
        char c;
        StringBuilder jsonForMatStr = new StringBuilder();
        for (int i = 0; i < jsonStr.length(); i++) {
            c = jsonStr.charAt(i);
            if (level > 0 && '\n' == jsonForMatStr.charAt(jsonForMatStr.length() - 1)) {
                jsonForMatStr.append(getLevelStr(level));
            }
            switch (c) {
                case '{':
                case '[':
                    jsonForMatStr.append(c).append("\n");
                    level++;
                    break;
                case ',':
                    jsonForMatStr.append(c).append("\n");
                    break;
                case '}':
                case ']':
                    jsonForMatStr.append("\n");
                    level--;
                    jsonForMatStr.append(getLevelStr(level));
                    jsonForMatStr.append(c);
                    break;
                default:
                    jsonForMatStr.append(c);
                    break;
            }
        }
        return jsonForMatStr.toString();
    }

    private String getLevelStr(int level) {
        StringBuilder levelStr = new StringBuilder();
        for (int levelI = 0; levelI < level; levelI++) {
            levelStr.append("\t");
        }
        return levelStr.toString();
    }

}
