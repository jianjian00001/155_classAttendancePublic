import Vue from 'vue';
//配置路由
import VueRouter from 'vue-router'
Vue.use(VueRouter);
//1.创建组件
import Index from '@/views/index'
import Home from '@/views/home'
import Login from '@/views/login'
import NotFound from '@/views/404'
import UpdatePassword from '@/views/update-password'
import pay from '@/views/pay'
import register from '@/views/register'
import center from '@/views/center'

     import users from '@/views/modules/users/list'
    import clazz from '@/views/modules/clazz/list'
    import daka from '@/views/modules/daka/list'
    import dictionary from '@/views/modules/dictionary/list'
    import huida from '@/views/modules/huida/list'
    import kaoqintongjixinxi from '@/views/modules/kaoqintongjixinxi/list'
    import kebiao from '@/views/modules/kebiao/list'
    import kecheng from '@/views/modules/kecheng/list'
    import qingjia from '@/views/modules/qingjia/list'
    import shoukejihua from '@/views/modules/shoukejihua/list'
    import tiaoke from '@/views/modules/tiaoke/list'
    import yonghu from '@/views/modules/yonghu/list'
    import zuoye from '@/views/modules/zuoye/list'
    import laoshi from '@/views/modules/laoshi/list'
    import dictionaryKecheng from '@/views/modules/dictionaryKecheng/list'
    import dictionaryQingjia from '@/views/modules/dictionaryQingjia/list'
    import dictionaryQingjiaYesno from '@/views/modules/dictionaryQingjiaYesno/list'
    import dictionarySections from '@/views/modules/dictionarySections/list'
    import dictionarySex from '@/views/modules/dictionarySex/list'
    import dictionaryShoukejihua from '@/views/modules/dictionaryShoukejihua/list'
    import dictionaryTiaokeYesno from '@/views/modules/dictionaryTiaokeYesno/list'
    import dictionaryXueqi from '@/views/modules/dictionaryXueqi/list'
    import dictionaryZuoye from '@/views/modules/dictionaryZuoye/list'





//2.配置路由   注意：名字
const routes = [{
    path: '/index',
    name: '首页',
    component: Index,
    children: [{
      // 这里不设置值，是把main作为默认页面
      path: '/',
      name: '首页',
      component: Home,
      meta: {icon:'', title:'center'}
    }, {
      path: '/updatePassword',
      name: '修改密码',
      component: UpdatePassword,
      meta: {icon:'', title:'updatePassword'}
    }, {
      path: '/pay',
      name: '支付',
      component: pay,
      meta: {icon:'', title:'pay'}
    }, {
      path: '/center',
      name: '个人信息',
      component: center,
      meta: {icon:'', title:'center'}
    } ,{
        path: '/users',
        name: '管理信息',
        component: users
      }
    ,{
        path: '/dictionaryKecheng',
        name: '课程形式类型名称',
        component: dictionaryKecheng
    }
    ,{
        path: '/dictionaryQingjia',
        name: '请假类型',
        component: dictionaryQingjia
    }
    ,{
        path: '/dictionaryQingjiaYesno',
        name: '审核状态',
        component: dictionaryQingjiaYesno
    }
    ,{
        path: '/dictionarySections',
        name: '节数类型名称',
        component: dictionarySections
    }
    ,{
        path: '/dictionarySex',
        name: '性别类型名称',
        component: dictionarySex
    }
    ,{
        path: '/dictionaryShoukejihua',
        name: '授课计划类型',
        component: dictionaryShoukejihua
    }
    ,{
        path: '/dictionaryTiaokeYesno',
        name: '审核状态',
        component: dictionaryTiaokeYesno
    }
    ,{
        path: '/dictionaryXueqi',
        name: '学期名称',
        component: dictionaryXueqi
    }
    ,{
        path: '/dictionaryZuoye',
        name: '作业类型',
        component: dictionaryZuoye
    }


    ,{
        path: '/clazz',
        name: '班级',
        component: clazz
      }
    ,{
        path: '/daka',
        name: '打卡',
        component: daka
      }
    ,{
        path: '/dictionary',
        name: '字典',
        component: dictionary
      }
    ,{
        path: '/huida',
        name: '回答',
        component: huida
      }
    ,{
        path: '/kaoqintongjixinxi',
        name: '班级考勤信息',
        component: kaoqintongjixinxi
      }
    ,{
        path: '/kebiao',
        name: '课表',
        component: kebiao
      }
    ,{
        path: '/kecheng',
        name: '课程',
        component: kecheng
      }
    ,{
        path: '/qingjia',
        name: '请假',
        component: qingjia
      }
    ,{
        path: '/shoukejihua',
        name: '授课计划',
        component: shoukejihua
      }
    ,{
        path: '/tiaoke',
        name: '调课申请',
        component: tiaoke
      }
    ,{
        path: '/yonghu',
        name: '学生',
        component: yonghu
      }
    ,{
        path: '/zuoye',
        name: '作业',
        component: zuoye
      }
    ,{
        path: '/laoshi',
        name: '老师',
        component: laoshi
      }


    ]
  },
  {
    path: '/login',
    name: 'login',
    component: Login,
    meta: {icon:'', title:'login'}
  },
  {
    path: '/register',
    name: 'register',
    component: register,
    meta: {icon:'', title:'register'}
  },
  {
    path: '/',
    name: '首页',
    redirect: '/index'
  }, /*默认跳转路由*/
  {
    path: '*',
    component: NotFound
  }
]
//3.实例化VueRouter  注意：名字
const router = new VueRouter({
  mode: 'hash',
  /*hash模式改为history*/
  routes // （缩写）相当于 routes: routes
})

export default router;
