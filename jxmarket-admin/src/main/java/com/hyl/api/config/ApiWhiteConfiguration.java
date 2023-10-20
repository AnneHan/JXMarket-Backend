package com.hyl.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * api白色白名单配置
 *
 * @author AnneHan
 * @date 2023-09-15
 */
@ConfigurationProperties(prefix = "api-url")
@Component
public class ApiWhiteConfiguration {

    private Set<String> whiteList;

    public Set<String> getWhiteList() {
        return new HashSet<>(whiteList);
    }

    public void setWhiteList(Set<String> whiteList) {
        this.whiteList = new HashSet<>(whiteList);
    }

}
