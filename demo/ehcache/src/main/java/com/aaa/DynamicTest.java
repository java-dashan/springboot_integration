package com.aaa;

import com.itranswarp.compiler.JavaStringCompiler;

import java.lang.reflect.Method;
import java.util.Map;

public class DynamicTest {


    public static final String code = "package com.aaa;\n" +
            "\n" +
            "public class UserService {\n" +
            "\n" +
            "    private IndexService service;\n" +
            "\n" +
            "    public void user(){\n" +
            "        service.query();\n" +
            "    }\n" +
            "\n" +
            "    public void setService(IndexService service){\n" +
            "        this.service = service;\n" +
            "    }\n" +
            "\n" +
            "}";

    public static void main(String[] args) throws Exception {
        JavaStringCompiler compiler = new JavaStringCompiler();
        Map<String, byte[]> compile = compiler.compile("UserService.java", code);
        Class<?> aClass = compiler.loadClass("com.aaa.UserService", compile);

        Method setService = aClass.getMethod("setService", IndexService.class);
        Object o = aClass.newInstance();
        setService.invoke(o,new IndexService());
        Method user = aClass.getMethod("user");
        user.invoke(o);


    }
}