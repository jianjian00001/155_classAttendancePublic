
package com.controller;

import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.text.SimpleDateFormat;
import com.alibaba.fastjson.JSONObject;
import java.util.*;
import org.springframework.beans.BeanUtils;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.context.ContextLoader;
import javax.servlet.ServletContext;
import com.service.TokenService;
import com.utils.*;
import java.lang.reflect.InvocationTargetException;

import com.service.DictionaryService;
import org.apache.commons.lang3.StringUtils;
import com.annotation.IgnoreAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.entity.*;
import com.entity.view.*;
import com.service.*;
import com.utils.PageUtils;
import com.utils.R;
import com.alibaba.fastjson.*;

/**
 * 班级考勤信息
 * 后端接口
 * @author
 * @email
*/
@RestController
@Controller
@RequestMapping("/kaoqintongjixinxi")
public class KaoqintongjixinxiController {
    private static final Logger logger = LoggerFactory.getLogger(KaoqintongjixinxiController.class);

    @Autowired
    private KaoqintongjixinxiService kaoqintongjixinxiService;


    @Autowired
    private TokenService tokenService;
    @Autowired
    private DictionaryService dictionaryService;

    //级联表service
    @Autowired
    private ClazzService clazzService;

    @Autowired
    private YonghuService yonghuService;
    @Autowired
    private LaoshiService laoshiService;


    /**
    * 后端列表
    */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, HttpServletRequest request){
        logger.debug("page方法:,,Controller:{},,params:{}",this.getClass().getName(),JSONObject.toJSONString(params));
        String role = String.valueOf(request.getSession().getAttribute("role"));
        if(false)
            return R.error(511,"永不会进入");
        else if("学生".equals(role))
            params.put("yonghuId",request.getSession().getAttribute("userId"));
        else if("老师".equals(role))
            params.put("laoshiId",request.getSession().getAttribute("userId"));
        if(params.get("orderBy")==null || params.get("orderBy")==""){
            params.put("orderBy","id");
        }
        PageUtils page = kaoqintongjixinxiService.queryPage(params);

        //字典表数据转换
        List<KaoqintongjixinxiView> list =(List<KaoqintongjixinxiView>)page.getList();
        for(KaoqintongjixinxiView c:list){
            //修改对应字典表字段
            dictionaryService.dictionaryConvert(c, request);
        }
        return R.ok().put("data", page);
    }

    /**
    * 后端详情
    */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id, HttpServletRequest request){
        logger.debug("info方法:,,Controller:{},,id:{}",this.getClass().getName(),id);
        KaoqintongjixinxiEntity kaoqintongjixinxi = kaoqintongjixinxiService.selectById(id);
        if(kaoqintongjixinxi !=null){
            //entity转view
            KaoqintongjixinxiView view = new KaoqintongjixinxiView();
            BeanUtils.copyProperties( kaoqintongjixinxi , view );//把实体数据重构到view中

                //级联表
                ClazzEntity clazz = clazzService.selectById(kaoqintongjixinxi.getClazzId());
                if(clazz != null){
                    BeanUtils.copyProperties( clazz , view ,new String[]{ "id", "createTime", "insertTime", "updateTime"});//把级联的数据添加到view中,并排除id和创建时间字段
                    view.setClazzId(clazz.getId());
                }
            //修改对应字典表字段
            dictionaryService.dictionaryConvert(view, request);
            return R.ok().put("data", view);
        }else {
            return R.error(511,"查不到数据");
        }

    }

    /**
    * 后端保存
    */
    @RequestMapping("/save")
    public R save(@RequestBody KaoqintongjixinxiEntity kaoqintongjixinxi, HttpServletRequest request){
        logger.debug("save方法:,,Controller:{},,kaoqintongjixinxi:{}",this.getClass().getName(),kaoqintongjixinxi.toString());

        String role = String.valueOf(request.getSession().getAttribute("role"));
        if(false)
            return R.error(511,"永远不会进入");

        Wrapper<KaoqintongjixinxiEntity> queryWrapper = new EntityWrapper<KaoqintongjixinxiEntity>()
            .eq("clazz_id", kaoqintongjixinxi.getClazzId())
            .eq("riqi_time", new SimpleDateFormat("yyyy-MM-dd").format(kaoqintongjixinxi.getRiqiTime()))
            ;

        logger.info("sql语句:"+queryWrapper.getSqlSegment());
        KaoqintongjixinxiEntity kaoqintongjixinxiEntity = kaoqintongjixinxiService.selectOne(queryWrapper);
        if(kaoqintongjixinxiEntity==null){
            kaoqintongjixinxi.setInsertTime(new Date());
            kaoqintongjixinxi.setCreateTime(new Date());
            kaoqintongjixinxiService.insert(kaoqintongjixinxi);
            return R.ok();
        }else {
            return R.error(511,"该班级该天已有考勤信息记录");
        }
    }

    /**
    * 后端修改
    */
    @RequestMapping("/update")
    public R update(@RequestBody KaoqintongjixinxiEntity kaoqintongjixinxi, HttpServletRequest request){
        logger.debug("update方法:,,Controller:{},,kaoqintongjixinxi:{}",this.getClass().getName(),kaoqintongjixinxi.toString());

        String role = String.valueOf(request.getSession().getAttribute("role"));
//        if(false)
//            return R.error(511,"永远不会进入");
        //根据字段查询是否有相同数据
        Wrapper<KaoqintongjixinxiEntity> queryWrapper = new EntityWrapper<KaoqintongjixinxiEntity>()
            .notIn("id",kaoqintongjixinxi.getId())
            .andNew()
            .eq("clazz_id", kaoqintongjixinxi.getClazzId())
            .eq("riqi_time", new SimpleDateFormat("yyyy-MM-dd").format(kaoqintongjixinxi.getRiqiTime()))
            ;

        logger.info("sql语句:"+queryWrapper.getSqlSegment());
        KaoqintongjixinxiEntity kaoqintongjixinxiEntity = kaoqintongjixinxiService.selectOne(queryWrapper);
        if(kaoqintongjixinxiEntity==null){
            kaoqintongjixinxiService.updateById(kaoqintongjixinxi);//根据id更新
            return R.ok();
        }else {
            return R.error(511,"该班级该天已有考勤信息记录");
        }
    }


    /**
    * 后端修改
    */
    @RequestMapping("/selectOne")
    public R selectOne(@RequestParam Map<String, Object> params, HttpServletRequest request){
        logger.debug("selectOne方法:,,Controller:{},,params:{}",this.getClass().getName(),params.toString());

        KaoqintongjixinxiEntity kaoqintongjixinxiEntity = kaoqintongjixinxiService.selectOne(new EntityWrapper<KaoqintongjixinxiEntity>()
                .eq("clazz_id", params.get("clazzId"))
                .eq("riqi_time", params.get("riqiTime"))
        );


        return R.ok().put("data", kaoqintongjixinxiEntity);

    }

    /**
    * 删除
    */
    @RequestMapping("/delete")
    public R delete(@RequestBody Integer[] ids){
        logger.debug("delete:,,Controller:{},,ids:{}",this.getClass().getName(),ids.toString());
        kaoqintongjixinxiService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }


    /**
     * 批量上传
     */
    @RequestMapping("/batchInsert")
    public R save( String fileName, HttpServletRequest request){
        logger.debug("batchInsert方法:,,Controller:{},,fileName:{}",this.getClass().getName(),fileName);
        Integer yonghuId = Integer.valueOf(String.valueOf(request.getSession().getAttribute("userId")));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            List<KaoqintongjixinxiEntity> kaoqintongjixinxiList = new ArrayList<>();//上传的东西
            Map<String, List<String>> seachFields= new HashMap<>();//要查询的字段
            Date date = new Date();
            int lastIndexOf = fileName.lastIndexOf(".");
            if(lastIndexOf == -1){
                return R.error(511,"该文件没有后缀");
            }else{
                String suffix = fileName.substring(lastIndexOf);
                if(!".xls".equals(suffix)){
                    return R.error(511,"只支持后缀为xls的excel文件");
                }else{
                    URL resource = this.getClass().getClassLoader().getResource("../../upload/" + fileName);//获取文件路径
                    File file = new File(resource.getFile());
                    if(!file.exists()){
                        return R.error(511,"找不到上传文件，请联系管理员");
                    }else{
                        List<List<String>> dataList = PoiUtil.poiImport(file.getPath());//读取xls文件
                        dataList.remove(0);//删除第一行，因为第一行是提示
                        for(List<String> data:dataList){
                            //循环
                            KaoqintongjixinxiEntity kaoqintongjixinxiEntity = new KaoqintongjixinxiEntity();
//                            kaoqintongjixinxiEntity.setClazzId(Integer.valueOf(data.get(0)));   //班级 要改的
//                            kaoqintongjixinxiEntity.setYidakaiNumber(Integer.valueOf(data.get(0)));   //已打卡人数 要改的
//                            kaoqintongjixinxiEntity.setWeidakaNumber(Integer.valueOf(data.get(0)));   //未打卡人数 要改的
//                            kaoqintongjixinxiEntity.setKaoqintongjixinxiContent("");//详情和图片
//                            kaoqintongjixinxiEntity.setRiqiTime(sdf.parse(data.get(0)));          //日期 要改的
//                            kaoqintongjixinxiEntity.setInsertTime(date);//时间
//                            kaoqintongjixinxiEntity.setCreateTime(date);//时间
                            kaoqintongjixinxiList.add(kaoqintongjixinxiEntity);


                            //把要查询是否重复的字段放入map中
                        }

                        //查询是否重复
                        kaoqintongjixinxiService.insertBatch(kaoqintongjixinxiList);
                        return R.ok();
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return R.error(511,"批量插入数据异常，请联系管理员");
        }
    }






}
