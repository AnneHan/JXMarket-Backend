package com.hyl.api.util;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 过滤集合中的空属性
 * @author AnneHan
 * @date 2023-09-15
 */
public class ParseMapForFilterNotNull {

    /**
     * 过滤对象中的空属性对象
     * @param map
     * @return
     */
    public static Map<String, Object>  parseMapForFilter (Map<String, Object> map){
        return  Optional.ofNullable(map).map(
                (v) -> {
                    Map params = v.entrySet().stream()
                            .filter((e) -> rules(e.getValue()))
                            .collect(Collectors.toMap(
                                    (e) -> (String) e.getKey(),
                                    (e) -> e.getValue()
                            ));
                    return params;
                }
        ).orElse(null);
    }

    private static boolean rules(Object object) {
        if (object instanceof String && "".equals(object)) {
            return false;
        }
        if (null == object) {
            return false;
        }
        return true;
    }
}
