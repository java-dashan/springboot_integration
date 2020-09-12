package com.util;

import javax.tools.SimpleJavaFileObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

public class MyJavaFileObject extends SimpleJavaFileObject {
    
     private String source;
     private ByteArrayOutputStream outputStream;

     public MyJavaFileObject(String name,String source) {
         super(URI.create("String:///"+name),Kind.SOURCE);
         this.source = source;
     }

     public MyJavaFileObject(String name,Kind kind){
         super(URI.create("String:///"+name),kind);
         source = null;
     }

     @Override
     public OutputStream openOutputStream() throws IOException {
         outputStream = new ByteArrayOutputStream();
         return outputStream;
     }

     @Override
     public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
         if(source == null){
             throw new IllegalArgumentException("source == null");
         }
         return source;
     }

     public byte[] getCompileByte(){
         return outputStream.toByteArray();
     }
}  