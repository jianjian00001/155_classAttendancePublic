package com.dao;

import com.entity.ClazzEntity;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;

import org.apache.ibatis.annotations.Param;
import com.entity.view.ClazzView;

/**
 * 班级 Dao 接口
 *
 * @author 
 */
public interface ClazzDao extends BaseMapper<ClazzEntity> {

   List<ClazzView> selectListView(Pagination page,@Param("params")Map<String,Object> params);

}
