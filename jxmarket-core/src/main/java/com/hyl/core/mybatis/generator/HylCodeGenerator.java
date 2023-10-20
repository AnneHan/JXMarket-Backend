package com.hyl.core.mybatis.generator;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.hyl.core.mybatis.mapper.HylBaseMapper;
import com.hyl.core.entity.BaseEntity;

import java.util.Collections;

public class HylCodeGenerator {
    public static void main(String[] args) {

        FastAutoGenerator.create("jdbc:mysql://localhost:3308/jx_market?useSSL=false&useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai",
                        "anne", "anne")
                .globalConfig(builder -> {
                    builder.author("AnneHan")
                            .enableSwagger()
                            .dateType(DateType.TIME_PACK)
                            .fileOverride()
                            .outputDir("C://test");
                })
                .packageConfig(builder -> {
                    builder.parent("com.hyl.api")
                            .moduleName("system")
                            .pathInfo(Collections.singletonMap(OutputFile.mapperXml, "C://test"));
                })

                .strategyConfig(builder -> builder
                        // 设置需要生成的表名
                        .addInclude("m_purchase_detail")
                        .entityBuilder()
                        .superClass(BaseEntity.class)
                        .disableSerialVersionUID()
                        .enableChainModel()
                        .enableLombok()
                        .enableTableFieldAnnotation()
                        .idType(IdType.AUTO)
                        .versionColumnName("version")
                        .versionPropertyName("version")
                        .logicDeleteColumnName("del_flag")
                        .logicDeletePropertyName("delFlag")
                        .addIgnoreColumns("id", "create_time", "update_time", "create_by", "update_by")
                        .formatFileName("%sEntity")
                        .controllerBuilder()
                        .formatFileName("%sController")
                        .enableRestStyle()
                        .mapperBuilder()
                        .superClass(HylBaseMapper.class)
                        .formatMapperFileName("%sMapper")
                        .formatXmlFileName("%sXml"))
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }
}
