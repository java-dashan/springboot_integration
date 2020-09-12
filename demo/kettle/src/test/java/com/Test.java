//package com;
//
//import cn.hutool.core.io.FileUtil;
//import org.junit.runner.RunWith;
//import org.pentaho.di.core.Const;
//import org.pentaho.di.core.KettleEnvironment;
//import org.pentaho.di.core.exception.KettleException;
//import org.pentaho.di.core.exception.KettleXMLException;
//import org.pentaho.di.trans.Trans;
//import org.pentaho.di.trans.TransMeta;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.test.context.junit4.SpringRunner;
//
//
//@RunWith(SpringRunner.class)
//@SpringBootApplication
//public class Test {
//
//    @org.junit.Test
//    public void t() {
//        try {
//            // JNDI_DIRECTORY = NVL(System.getProperty("KETTLE_JNDI_ROOT"), System.getProperty("org.osjava.sj.root"));
//            // 该参数如果没配置,默认为空,将导致使用默认gitxxx驱动报错
//            // 我们自定义配置数据库信息
//            Const.JNDI_DIRECTORY = "D:\\data-integration\\simple-jndi";
//            // 初始化
//            KettleEnvironment.init();
//            // 文件路径
//            TransMeta transMeta = new TransMeta("C:\\Users\\hez\\Desktop\\a.ktr");
//            Trans trans = new Trans(transMeta);
//            // 放入参数，这里其实可以从数据库中取到
//            // 如果没有参数，可以把这步忽略
//            trans.setVariable("stade", "2019-04-24");
//            trans.prepareExecution(null);
//            trans.startThreads();
//            // 等待执行完毕
//            trans.waitUntilFinished();
//            if (trans.getErrors() != 0) {
//                System.out.println("Trans Error!");
//            }
//        } catch (KettleXMLException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (KettleException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }
//}
