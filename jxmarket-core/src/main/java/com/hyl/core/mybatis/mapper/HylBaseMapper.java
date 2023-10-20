package com.hyl.core.mybatis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HylBaseMapper<T> extends BaseMapper<T>  {
    /**
     * 删除表所有数据
     * @return 影响行数
     */
    int deleteAll();

    /**
     * 批量插入
     * @param list 插入对象
     * @return 成功影响行数数
     */
    int insertAllBatch(List<T> list);
}
