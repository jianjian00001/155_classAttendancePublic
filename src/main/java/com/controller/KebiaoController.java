












package com.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.entity.ClazzEntity;
import com.entity.KebiaoEntity;
import com.entity.KechengEntity;
import com.entity.YonghuEntity;
import com.entity.view.KebiaoView;
import com.service.*;
import com.utils.PageUtils;
import com.utils.PoiUtil;
import com.utils.R;
import com.utils.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.net.URL;
import java.util.*;

/**
 * 课表
 * 后端接口
 * @author
 * @email
*/
@RestController
@Controller
@RequestMapping("/kebiao")
public class KebiaoController {
    private static final Logger logger = LoggerFactory.getLogger(KebiaoController.class);

    @Autowired
    private KebiaoService kebiaoService;


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
    private KechengService kechengService;


    /**
    * 后端列表
    */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, HttpServletRequest request){
        logger.debug("page方法:,,Controller:{},,params:{}",this.getClass().getName(),JSONObject.toJSONString(params));
        String role = String.valueOf(request.getSession().getAttribute("role"));
        if(StringUtil.isEmpty(role))
            return R.error(511,"权限为空");
        else if("学生".equals(role)){
//            params.put("yonghuId",request.getSession().getAttribute("userId"));
            YonghuEntity yonghuEntity = yonghuService.selectById(String.valueOf(request.getSession().getAttribute("userId")));
            params.put("clazzId",yonghuEntity.getClazzId());

        }
        if(params.get("orderBy")==null || params.get("orderBy")==""){
            params.put("orderBy","paixu_number");
        }
        PageUtils page = kebiaoService.queryPage(params);


        List<KebiaoView> list =(List<KebiaoView>)page.getList();
        if(list != null && list.size() > 0){
            HashSet<String> kechengIdSet = new HashSet<>();
            for(KebiaoView c:list){
                String kecheng = c.getKecheng();
                if(StringUtils.isNotEmpty(kecheng)){
                    kechengIdSet.addAll(Arrays.asList(kecheng.split(",")));
                }
            }

            List<KechengEntity> kechengEntities = kechengService.selectBatchIds(kechengIdSet);
            Map<String, KechengEntity> kechengMap = new HashMap<>();
            for(KechengEntity k:kechengEntities){
                kechengMap.put(String.valueOf(k.getId()),k);
            }
            if(!kechengMap.isEmpty()){
//                StringBuffer yiString = new StringBuffer();
                for(KebiaoView c:list){
                    String kecheng = c.getKecheng();
                    if(StringUtils.isNotEmpty(kecheng)){
                        String[] xingqi = kecheng.split(",");
                        c.setYi(kechengMap.get(xingqi[0]).getKechengName()+","+kechengMap.get(xingqi[0]).getTempLaoshiName());
                        c.setEr(kechengMap.get(xingqi[1]).getKechengName()+","+kechengMap.get(xingqi[1]).getTempLaoshiName());
                        c.setSan(kechengMap.get(xingqi[2]).getKechengName()+","+kechengMap.get(xingqi[2]).getTempLaoshiName());
                        c.setSi(kechengMap.get(xingqi[3]).getKechengName()+","+kechengMap.get(xingqi[3]).getTempLaoshiName());
                        c.setWu(kechengMap.get(xingqi[4]).getKechengName()+","+kechengMap.get(xingqi[4]).getTempLaoshiName());
                    }
                }
            }

        }
        //字典表数据转换
        for(KebiaoView c:list){
            //修改对应字典表字段
            dictionaryService.dictionaryConvert(c, request);
        }


        // kecheng1  xingqi2
        return R.ok().put("data", page);
    }

    /**
    * 后端详情
    */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id, HttpServletRequest request){
        logger.debug("info方法:,,Controller:{},,id:{}",this.getClass().getName(),id);
        KebiaoEntity kebiao = kebiaoService.selectById(id);
        if(kebiao !=null){
            //entity转view
            KebiaoView view = new KebiaoView();
            BeanUtils.copyProperties( kebiao , view );//把实体数据重构到view中

                //级联表
                ClazzEntity clazz = clazzService.selectById(kebiao.getClazzId());
                if(clazz != null){
                    BeanUtils.copyProperties( clazz , view ,new String[]{ "id", "createDate"});//把级联的数据添加到view中,并排除id和创建时间字段
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
    public R save(@RequestBody KebiaoEntity kebiao, HttpServletRequest request){
        logger.debug("save方法:,,Controller:{},,kebiao:{}",this.getClass().getName(),kebiao.toString());

        String role = String.valueOf(request.getSession().getAttribute("role"));
        if(StringUtil.isEmpty(role))
            return R.error(511,"权限为空");

        Wrapper<KebiaoEntity> queryWrapper = new EntityWrapper<KebiaoEntity>()
            .eq("sections_types", kebiao.getSectionsTypes())
            .eq("clazz_id", kebiao.getClazzId())
            .eq("xueqi_types", kebiao.getXueqiTypes())
            ;

        logger.info("sql语句:"+queryWrapper.getSqlSegment());
        KebiaoEntity kebiaoEntity = kebiaoService.selectOne(queryWrapper);
        if(kebiaoEntity==null){
            kebiao.setCreateTime(new Date());
            kebiaoService.insert(kebiao);
            return R.ok();
        }else {
            return R.error(511,"该班级的该学期的该节数的已经存在");
        }
    }

    /**
    * 后端修改
    */
    @RequestMapping("/update")
    public R update(@RequestBody KebiaoEntity kebiao, HttpServletRequest request){
        logger.debug("update方法:,,Controller:{},,kebiao:{}",this.getClass().getName(),kebiao.toString());

        String role = String.valueOf(request.getSession().getAttribute("role"));
//        if(StringUtil.isEmpty(role))
//            return R.error(511,"权限为空");
        //根据字段查询是否有相同数据
        Wrapper<KebiaoEntity> queryWrapper = new EntityWrapper<KebiaoEntity>()
            .notIn("id",kebiao.getId())
            .andNew()
            .eq("sections_types", kebiao.getSectionsTypes())
            .eq("clazz_id", kebiao.getClazzId())
            .eq("xueqi_types", kebiao.getXueqiTypes())
            ;

        logger.info("sql语句:"+queryWrapper.getSqlSegment());
        KebiaoEntity kebiaoEntity = kebiaoService.selectOne(queryWrapper);
        if(kebiaoEntity==null){
            //  String role = String.valueOf(request.getSession().getAttribute("role"));
            //  if("".equals(role)){
            //      kebiao.set
            //  }
            kebiaoService.updateById(kebiao);//根据id更新
            return R.ok();
        }else {
            return R.error(511,"该班级的该学期的该节数的已经存在");
        }
    }

    /**
    * 删除
    */
    @RequestMapping("/delete")
    public R delete(@RequestBody Integer[] ids){
        logger.debug("delete:,,Controller:{},,ids:{}",this.getClass().getName(),ids.toString());
        kebiaoService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }

    /**
     * 批量上传
     */
    @RequestMapping("/batchInsert")
    public R save(String fileName){
        logger.debug("batchInsert方法:,,Controller:{},,fileName:{}",this.getClass().getName(),fileName);
        try {
            List<KebiaoEntity> kebiaoList = new ArrayList<>();//上传的东西
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
                    URL resource = this.getClass().getClassLoader().getResource("static/upload/" + fileName);//获取文件路径
                    File file = new File(resource.getFile());
                    if(!file.exists()){
                        return R.error(511,"找不到上传文件，请联系管理员");
                    }else{
                        List<List<String>> dataList = PoiUtil.poiImport(file.getPath());//读取xls文件
                        dataList.remove(0);//删除第一行，因为第一行是提示
                        for(List<String> data:dataList){
                            //循环
                            KebiaoEntity kebiaoEntity = new KebiaoEntity();
//                            kebiaoEntity.setSectionsTypes(Integer.valueOf(data.get(0)));   //节数 要改的
//                            kebiaoEntity.setClazzId(Integer.valueOf(data.get(0)));   //班级 要改的
//                            kebiaoEntity.setXueqiTypes(Integer.valueOf(data.get(0)));   //学期 要改的
//                            kebiaoEntity.setKecheng(data.get(0));                    //课程 要改的
//                            kebiaoEntity.setPaixuNumber(Integer.valueOf(data.get(0)));   //排序字段 要改的
//                            kebiaoEntity.setCreateTime(date);//时间
                            kebiaoList.add(kebiaoEntity);


                            //把要查询是否重复的字段放入map中
                        }

                        //查询是否重复
                        kebiaoService.insertBatch(kebiaoList);
                        return R.ok();
                    }
                }
            }
        }catch (Exception e){
            return R.error(511,"批量插入数据异常，请联系管理员");
        }
    }






}
