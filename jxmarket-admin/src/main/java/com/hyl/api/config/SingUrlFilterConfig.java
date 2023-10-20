package com.hyl.api.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@ConfigurationProperties(prefix = "white")
@Getter
@Setter
@ToString
public class SingUrlFilterConfig {

    private Set<String> urls;

    private boolean enable;

}
