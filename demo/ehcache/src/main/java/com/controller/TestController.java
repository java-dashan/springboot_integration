package com.controller;

import com.dao.Kill;
import com.itranswarp.compiler.JavaStringCompiler;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.*;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private BeanFactory beanFactory;

    @Autowired
    private ApplicationContext applicationContext;

    private static String pathSuffix= "E:\\IdeaWorkspace\\springboot_integration\\demo\\ehcache\\src\\main\\java\\com\\test\\";


    @GetMapping("/load")
    public String load(String path) throws Exception{



//        ClassLoader classLoader = applicationContext.getClassLoader();
////        Class.forName();
//        Class<?> aClass = classLoader.loadClass(path);
//
//        AutowireCapableBeanFactory autowireCapableBeanFactory = applicationContext.getAutowireCapableBeanFactory();
//        System.out.println(applicationContext instanceof AnnotationConfigApplicationContext);
//        AnnotationConfigServletWebApplicationContext annotationConfigServletWebApplicationContext = (AnnotationConfigServletWebApplicationContext) applicationContext;
//        annotationConfigServletWebApplicationContext.register(aClass);
        return "success";
    }

    @GetMapping("/exec")
    public Object exec(String className) {
        Object bean = applicationContext.getBean(className);
        Kill kill = (Kill) bean;
        kill.set("dashan");
        System.out.println(kill.say());
        return kill;
//        Class<?> aClass = bean.getClass();
//        Method[] methods = aClass.getMethods();
//        for (Method method : methods) {
//            if (method.getName() == methodName) {
//
//            }
//        }
    }

    @PostMapping("/upload")
    public String upload(MultipartHttpServletRequest request) throws Exception{
        MultipartFile multipartFile = request.getFile("file");
        String filename = multipartFile.getResource().getFilename();
        Kill kill = new Kill() {
            @Override
            public String say() {
                return "dashan";
            }

            @Override
            public void set(String name) {

            }
        };
        String property = System.getProperty("user.dir");
        JavaStringCompiler compiler = new JavaStringCompiler();
        Map<String, byte[]> compile = compiler.compile("aaa.java", new String(multipartFile.getBytes()));
        Class<?> aClass = compiler.loadClass("com.dao.aaa", compile);

        Object o = aClass.newInstance();
        Method set = aClass.getMethod("set",Kill.class);
        set.invoke(o,kill);
        Method say = aClass.getMethod("say");
        System.out.println(say.invoke(o));
//        String fullPath = pathSuffix + filename;
//        File file = new File(fullPath);
//        if (!file.exists()) {
//            file.createNewFile();
//        }
//        FileCopyUtils.copy(multipartFile.getBytes(),file);
//        InputStream inputStream = multipartFile.getInputStream();
//        BufferedInputStream bis = new BufferedInputStream(inputStream);
//        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
//        byte[] bytes = new byte[1024];
//        while (bis.read(bytes, 0, -1) != -1) {
//            bos.write(bytes,0,-1);
//        }
//        bis.close();
//        bos.close();
        return "success";
    }

}
