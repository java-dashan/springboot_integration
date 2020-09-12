
package com.util;

public class MyClassLoad extends ClassLoader {
    
     @Override
     protected Class<?> findClass(String name) throws ClassNotFoundException {
         MyJavaFileObject javaFileObject = MyJavaFileManager.fileObjects.get(name);
         if(javaFileObject != null){
             byte[] compileByte = javaFileObject.getCompileByte();
             return defineClass(name,compileByte,0,compileByte.length);
         }
         try {
             return ClassLoader.getSystemClassLoader().loadClass(name);
         }catch (Exception e){
             return super.findClass(name);
         }
     }

}