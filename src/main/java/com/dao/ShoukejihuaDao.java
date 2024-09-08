package com.dao;

import com.entity.ShoukejihuaEntity;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;

import org.apache.ibatis.annotations.Param;
import com.entity.view.ShoukejihuaView;

/**
 * 授课计划 Dao 接口
 *
 * @author 
 */
public interface ShoukejihuaDao extends BaseMapper<ShoukejihuaEntity> {

   List<ShoukejihuaView> selectListView(Pagination page,@Param("params")Map<String,Object> params);

}
