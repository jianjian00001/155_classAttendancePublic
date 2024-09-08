
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
 * 作业
 * 后端接口
 * @author
 * @email
*/
@RestController
@Controller
@RequestMapping("/zuoye")
public class ZuoyeController {
    private static final Logger logger = LoggerFactory.getLogger(ZuoyeController.class);

    @Autowired
    private ZuoyeService zuoyeService;


    @Autowired
    private TokenService tokenService;
    @Autowired
    private DictionaryService dictionaryService;

    //级联表service
    @Autowired
    private ClazzService clazzService;
    @Autowired
    private LaoshiService laoshiService;

    @Autowired
    private YonghuService yonghuService;


    /**
    * 后端列表
    */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, HttpServletRequest request){
        logger.debug("page方法:,,Controller:{},,params:{}",this.getClass().getName(),JSONObject.toJSONString(params));
        String role = String.valueOf(request.getSession().getAttribute("role"));
        if(false)
            return R.error(511,"永不会进入");
        else if("学生".equals(role)){

            YonghuEntity yonghuEntity = yonghuService.selectById(Integer.valueOf(String.valueOf(request.getSession().getAttribute("userId"))));
            if(yonghuEntity == null)
                return R.error("查不到该学生");
            params.put("clazzId",yonghuEntity.getClazzId());
            params.put("yonghuId",request.getSession().getAttribute("userId"));

        }
        else if("老师".equals(role))
            params.put("laoshiId",request.getSession().getAttribute("userId"));
        if(params.get("orderBy")==null || params.get("orderBy")==""){
            params.put("orderBy","id");
        }
        PageUtils page = zuoyeService.queryPage(params);

        //字典表数据转换
        List<ZuoyeView> list =(List<ZuoyeView>)page.getList();
        for(ZuoyeView c:list){
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
        ZuoyeEntity zuoye = zuoyeService.selectById(id);
        if(zuoye !=null){
            //entity转view
            ZuoyeView view = new ZuoyeView();
            BeanUtils.copyProperties( zuoye , view );//把实体数据重构到view中

                //级联表
                ClazzEntity clazz = clazzService.selectById(zuoye.getClazzId());
                if(clazz != null){
                    BeanUtils.copyProperties( clazz , view ,new String[]{ "id", "createTime", "insertTime", "updateTime", "laoshiId"});//把级联的数据添加到view中,并排除id和创建时间字段
                    view.setClazzId(clazz.getId());
                    view.setClazzLaoshiId(clazz.getLaoshiId());
                }
                //级联表
                LaoshiEntity laoshi = laoshiService.selectById(zuoye.getLaoshiId());
                if(laoshi != null){
                    BeanUtils.copyProperties( laoshi , view ,new String[]{ "id", "createTime", "insertTime", "updateTime"});//把级联的数据添加到view中,并排除id和创建时间字段
                    view.setLaoshiId(laoshi.getId());
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
    public R save(@RequestBody ZuoyeEntity zuoye, HttpServletRequest request){
        logger.debug("save方法:,,Controller:{},,zuoye:{}",this.getClass().getName(),zuoye.toString());

        String role = String.valueOf(request.getSession().getAttribute("role"));
        if(false)
            return R.error(511,"永远不会进入");
        else if("老师".equals(role))
            zuoye.setLaoshiId(Integer.valueOf(String.valueOf(request.getSession().getAttribute("userId"))));

        Wrapper<ZuoyeEntity> queryWrapper = new EntityWrapper<ZuoyeEntity>()
            .eq("laoshi_id", zuoye.getLaoshiId())
            .eq("clazz_id", zuoye.getClazzId())
            .eq("zuoye_uuid_number", zuoye.getZuoyeUuidNumber())
            .eq("zuoye_name", zuoye.getZuoyeName())
            .eq("zuoye_types", zuoye.getZuoyeTypes())
            ;

        logger.info("sql语句:"+queryWrapper.getSqlSegment());
        ZuoyeEntity zuoyeEntity = zuoyeService.selectOne(queryWrapper);
        if(zuoyeEntity==null){
            zuoye.setInsertTime(new Date());
            zuoye.setCreateTime(new Date());
            zuoyeService.insert(zuoye);
            return R.ok();
        }else {
            return R.error(511,"表中有相同数据");
        }
    }

    /**
    * 后端修改
    */
    @RequestMapping("/update")
    public R update(@RequestBody ZuoyeEntity zuoye, HttpServletRequest request){
        logger.debug("update方法:,,Controller:{},,zuoye:{}",this.getClass().getName(),zuoye.toString());

        String role = String.valueOf(request.getSession().getAttribute("role"));
//        if(false)
//            return R.error(511,"永远不会进入");
//        else if("老师".equals(role))
//            zuoye.setLaoshiId(Integer.valueOf(String.valueOf(request.getSession().getAttribute("userId"))));
        //根据字段查询是否有相同数据
        Wrapper<ZuoyeEntity> queryWrapper = new EntityWrapper<ZuoyeEntity>()
            .notIn("id",zuoye.getId())
            .andNew()
            .eq("laoshi_id", zuoye.getLaoshiId())
            .eq("clazz_id", zuoye.getClazzId())
            .eq("zuoye_uuid_number", zuoye.getZuoyeUuidNumber())
            .eq("zuoye_name", zuoye.getZuoyeName())
            .eq("zuoye_types", zuoye.getZuoyeTypes())
            ;

        logger.info("sql语句:"+queryWrapper.getSqlSegment());
        ZuoyeEntity zuoyeEntity = zuoyeService.selectOne(queryWrapper);
        if("".equals(zuoye.getZuoyeFile()) || "null".equals(zuoye.getZuoyeFile())){
                zuoye.setZuoyeFile(null);
        }
        if(zuoyeEntity==null){
            zuoyeService.updateById(zuoye);//根据id更新
            return R.ok();
        }else {
            return R.error(511,"表中有相同数据");
        }
    }

    /**
    * 删除
    */
    @RequestMapping("/delete")
    public R delete(@RequestBody Integer[] ids){
        logger.debug("delete:,,Controller:{},,ids:{}",this.getClass().getName(),ids.toString());
        zuoyeService.deleteBatchIds(Arrays.asList(ids));
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
            List<ZuoyeEntity> zuoyeList = new ArrayList<>();//上传的东西
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
                            ZuoyeEntity zuoyeEntity = new ZuoyeEntity();
//                            zuoyeEntity.setLaoshiId(Integer.valueOf(data.get(0)));   //老师 要改的
//                            zuoyeEntity.setClazzId(Integer.valueOf(data.get(0)));   //班级 要改的
//                            zuoyeEntity.setZuoyeUuidNumber(data.get(0));                    //作业唯一编号 要改的
//                            zuoyeEntity.setZuoyeName(data.get(0));                    //作业名称 要改的
//                            zuoyeEntity.setZuoyeTypes(Integer.valueOf(data.get(0)));   //作业类型 要改的
//                            zuoyeEntity.setInsertTime(date);//时间
//                            zuoyeEntity.setZuoyeFile(data.get(0));                    //作业文件 要改的
//                            zuoyeEntity.setZuoyeContent("");//详情和图片
//                            zuoyeEntity.setCreateTime(date);//时间
                            zuoyeList.add(zuoyeEntity);


                            //把要查询是否重复的字段放入map中
                                //作业唯一编号
                                if(seachFields.containsKey("zuoyeUuidNumber")){
                                    List<String> zuoyeUuidNumber = seachFields.get("zuoyeUuidNumber");
                                    zuoyeUuidNumber.add(data.get(0));//要改的
                                }else{
                                    List<String> zuoyeUuidNumber = new ArrayList<>();
                                    zuoyeUuidNumber.add(data.get(0));//要改的
                                    seachFields.put("zuoyeUuidNumber",zuoyeUuidNumber);
                                }
                        }

                        //查询是否重复
                         //作业唯一编号
                        List<ZuoyeEntity> zuoyeEntities_zuoyeUuidNumber = zuoyeService.selectList(new EntityWrapper<ZuoyeEntity>().in("zuoye_uuid_number", seachFields.get("zuoyeUuidNumber")));
                        if(zuoyeEntities_zuoyeUuidNumber.size() >0 ){
                            ArrayList<String> repeatFields = new ArrayList<>();
                            for(ZuoyeEntity s:zuoyeEntities_zuoyeUuidNumber){
                                repeatFields.add(s.getZuoyeUuidNumber());
                            }
                            return R.error(511,"数据库的该表中的 [作业唯一编号] 字段已经存在 存在数据为:"+repeatFields.toString());
                        }
                        zuoyeService.insertBatch(zuoyeList);
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
