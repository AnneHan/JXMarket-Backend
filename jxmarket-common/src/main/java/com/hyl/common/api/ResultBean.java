package com.hyl.common.api;

import com.hyl.common.constants.HttpConstant;
import com.hyl.common.enums.ResponseCodeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ResourceBundle;


/**
 * Resp结果封装
 *
 * @author AnneHan
 * @date 2023-09-15
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "接口返回对象", description = "接口返回对象")
public class ResultBean<T> implements Serializable {

    private static final ResourceBundle RESOURCEBUNDLE = ResourceBundle.getBundle("message");

    private static final long serialVersionUID = 1L;

    /**
     * 成功标志
     */
    @ApiModelProperty(value = "成功标志")
    private String success = HttpConstant.SC_SUCCESSFUL;

    @ApiModelProperty(value = "语言", example = "zh_cn", required = true)
    private String language;

    /**
     * 返回处理消息
     */
    @ApiModelProperty(value = "返回处理消息")
    private String msg;

    /**
     * 返回代码
     */
    @ApiModelProperty(value = "返回代码")
    private String code;

    /**
     * 返回数据对象 data
     */
    @ApiModelProperty(value = "返回数据对象")
    private T result;

    /**
     * 时间戳
     */
    @ApiModelProperty(value = "时间戳")
    private long timestamp = System.currentTimeMillis();


    public ResultBean(T data, String success, ResponseCodeEnum responseCode, String language) {
        this.language = language;
        this.code = responseCode.getCode();
        this.msg = formatMsg(responseCode, language);
        this.result = data;
        this.success = success;
    }

    private static String formatMsg(ResponseCodeEnum responseCode, String language) {
        if (StringUtils.isNotBlank(language) && language.equals("en")) {
            return RESOURCEBUNDLE.getString(responseCode.getMessage() + "_E");
        } else {
            return RESOURCEBUNDLE.getString(responseCode.getMessage() + "_C");
        }
    }

    public static <T> ResultBean<T> ok(ResponseCodeEnum responseCodeEnum, String language) {
        ResultBean<T> r = new ResultBean<>();
        r.setSuccess(HttpConstant.SC_SUCCESSFUL);
        r.setCode(HttpConstant.SC_OK_200);
        r.setMsg(formatMsg(responseCodeEnum, language));
        r.setLanguage(language);
        return r;
    }

    public static <T> ResultBean<T> ok(T data, ResponseCodeEnum responseCodeEnum, String language) {
        ResultBean<T> r = new ResultBean<>();
        r.setSuccess(HttpConstant.SC_SUCCESSFUL);
        r.setCode(HttpConstant.SC_OK_200);
        r.setResult(data);
        r.setLanguage(language);
        r.setMsg(formatMsg(responseCodeEnum, language));
        return r;
    }


    public static <T> ResultBean<T> error(T data, ResponseCodeEnum responseCodeEnum, String language) {
        ResultBean<T> r = new ResultBean<>();
        r.setSuccess(HttpConstant.SC_FAILED);
        r.setCode(responseCodeEnum.getCode());
        r.setLanguage(language);
        r.setMsg(formatMsg(responseCodeEnum, language));
        r.setResult(data);
        return r;
    }

    public static ResultBean<Void> error(ResponseCodeEnum responseCodeEnum, String language) {
        ResultBean<Void> r = new ResultBean<>();
        r.setSuccess(HttpConstant.SC_FAILED);
        r.setCode(responseCodeEnum.getCode());
        r.setMsg(formatMsg(responseCodeEnum, language));
        r.setLanguage(language);
        return r;
    }


    public static ResultBean<Void> error(String msg,String enMsg,ResponseCodeEnum responseCodeEnum, String language) {
        ResultBean<Void> r = new ResultBean<>();
        r.setSuccess(HttpConstant.SC_FAILED);
        r.setCode(responseCodeEnum.getCode());
        if (StringUtils.isNotBlank(language) && language.equals("en")) {
            r.setMsg(enMsg);
        }else {
            r.setMsg(msg);
        }
        r.setLanguage(language);
        return r;
    }

}
