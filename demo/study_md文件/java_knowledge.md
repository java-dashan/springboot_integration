### 1.序列化



#### 1 .serialVersionUID

- 指定id  
- 不指定id (如果早期不指定id,后期又指定,会导致序列化异常,即为保证序列化反序列化安全性问题)

```java
private static final long serialVersionUID = -434545646546546L;
private static final long serialVersionUID = 1L;
```

#### 2 .transient

- 在字段上加上 transient 可令该字段不序列化

#### 3 .序列化+transient 组合拳 (绕过transient 序列化)

```java
public <T> byte[] serialize(T obj){
    try{
        ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(new File("user")));
        outputStream.writeObject(obj);
    }catch(IOException e){
        e.printStackTrace();
    }
    return new bytep[0];
}

public <T> deserialize(byte[] data,Class<T> clazz){
    try{
        ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(new File("user")));
        outputStream.writeObject(obj);
    }catch(IOException e){
        e.printStackTrace();
    }catch(ClassNotFoundException e){
        e.printStactTrance();
    }
    return null;
}

// 在需要序列化的类上加上 以下两个方法 方可绕过transient 可参考ArrayList源码    
// fastjson  无法配合该方式   XStream可以  取决于底层是否判断transient
private transient String name;
private void writeObject(java.io.ObjectOutputStream s) throws IOException {
    s.defaultWriteObject();
    s.writeObject(name);
}

private void readObject(java.io.ObjectInputStream s) throws IOException{
    s.defaultReadObject();
    name = (String) s.readObject();
}
```

#### 4. 使用技术 (json/XStream/jdk/kyro)