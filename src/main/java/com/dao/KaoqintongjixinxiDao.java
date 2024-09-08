package com.dao;

import com.entity.KaoqintongjixinxiEntity;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;

import org.apache.ibatis.annotations.Param;
import com.entity.view.KaoqintongjixinxiView;

/**
 * 班级考勤信息 Dao 接口
 *
 * @author 
 */
public interface KaoqintongjixinxiDao extends BaseMapper<KaoqintongjixinxiEntity> {

   List<KaoqintongjixinxiView> selectListView(Pagination page,@Param("params")Map<String,Object> params);

}
