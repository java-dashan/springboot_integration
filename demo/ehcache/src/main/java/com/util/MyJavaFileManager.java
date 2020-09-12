package com.util;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MyJavaFileManager extends ForwardingJavaFileManager<JavaFileManager> {

    public static Map<String, MyJavaFileObject> fileObjects = new ConcurrentHashMap<>();

    public MyJavaFileManager(JavaFileManager fileManager) {
        super(fileManager);
    }

    @Override
    public JavaFileObject getJavaFileForInput(Location location, String className, JavaFileObject.Kind kind) throws IOException {
        JavaFileObject javaFileObject = fileObjects.get(className);
        if (javaFileObject == null){
            super.getJavaFileForInput(location,className,kind);
        }
        return javaFileObject;
    }

    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
        MyJavaFileObject javaFileObject = new MyJavaFileObject(className,kind);
        fileObjects.put(className,javaFileObject);
        return javaFileObject;
    }
}
