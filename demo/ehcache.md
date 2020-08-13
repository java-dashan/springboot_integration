### 						spring-ehcache

#### 1.配置

```java
package com.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@EnableCaching
@Configuration
public class EhcacheConfig {

    @Bean
    public EhCacheManagerFactoryBean ehCacheManagerFactoryBean(){
        EhCacheManagerFactoryBean factoryBean = new EhCacheManagerFactoryBean();
        factoryBean.setConfig Location(new ClassPathResource("ehcache.xml"));
        factoryBean.setShared(true);
        return factoryBean;
    }

    @Bean
    public EhCacheCacheManager ehCacheFactoryBean(EhCacheManagerFactoryBean bean){
        return new EhCacheCacheManager(bean.getObject());
    }
}

```

#### 2.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<ehcache xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:noNamespaceSchemaLocation="http://ehcache.org/ehcache.xsd"
         updateCheck="false">
    <diskStore path="java.io.tmpdir/Tmp_EhCache" />

    <!-- defaultCache，是默认的缓存策略 -->
    <!-- 如果你指定的缓存策略没有找到，那么就用这个默认的缓存策略 -->
    <!-- external：缓存对象是否一直存在，如果设置为true的话，那么timeout就没有效果，缓存就会一直存在，一般默认就是false -->
    <!-- maxElementsInMemory：内存中可以缓存多少个缓存条目 -->
    <!-- overflowToDisk：如果内存不够的时候，是否溢出到磁盘 -->
    <!-- diskPersistent：是否启用磁盘持久化的机制，在jvm崩溃的时候和重启之间 -->
    <!-- timeToIdleSeconds：对象最大的闲置的时间，如果超出闲置的时间，可能就会过期  单位：秒 当eternal=false对象不是永久有效时使用，可选属性，默认值是0，也就是可闲置时间无穷大-->
    <!-- timeToLiveSeconds：对象最多存活的时间  单位：秒 当eternal=false对象不是永久有效时使用，可选属性，默认值是0，也就是存活时间无穷大-->
    <!-- memoryStoreEvictionPolicy：当缓存数量达到了最大的指定条目数的时候，需要采用一定的算法，从缓存中清除一批数据，LRU，最近最少使用算法，最近一段时间内，最少使用的那些数据，就被干掉了 -->
    <defaultCache
            eternal="false"
            maxElementsInMemory="1000"
            overflowToDisk="false"
            diskPersistent="false"
            timeToIdleSeconds="300"
            timeToLiveSeconds="0"
            memoryStoreEvictionPolicy="LRU" />

    <!-- 手动指定的缓存策略 -->
    <!-- 对不同的数据，缓存策略可以在这里配置多种 -->
    <cache
            name="user"
            eternal="false"
            maxElementsInMemory="1000"
            overflowToDisk="false"
            diskPersistent="false"
            timeToIdleSeconds="300"
            timeToLiveSeconds="0"
            memoryStoreEvictionPolicy="LRU" />
</ehcache>

```

#### 3.使用

##### （1）@cacheable

```java
package com.service;


import com.dao.UserDao;
import com.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class EhcacheService{

    @Autowired
    private UserDao userDao;

    //user要在xml中配置	 ,key 中如果不为root 则为参数,condition为真则缓存
    @Cacheable(value = {"user"},key = "#username")
    public User findByUsername(String username) {
        System.out.println("aaaa");
        return userDao.selectByUsername(username);
    }
    
    @Cacheable(value = {"user"},key = "#user.id",condition = "#user.id%2 == 0")
    public User findByUsername(User user) {
        System.out.println("aaaa");
        return userDao.selectByUsername(username);
    }

}

```

##### （2）@cachePut

 		@CachePut在支持Spring Cache的环境下，对于使用@Cacheable标注的方法，Spring在每次执行前都会检查Cache中是否存在相同key的缓存元素，如果存在就不再执行该方法，而是直接从缓存中获取结果进行返回，否则才会执行并将返回结果存入指定的缓存中。
 @CachePut也可以声明一个方法支持缓存功能。与@Cacheable不同的是使用@CachePut标注的方法在执行前不会去检查缓存中是否存在之前执行过的结果，而是每次都会执行该方法，并将执行结果（返回值）以键值对的形式存入指定的缓存中。所以必须有返回值，没有返回值则不会放入新的缓存。
 @CachePut也可以标注在类上和方法上。使用@CachePut时我们可以指定的属性跟@Cacheable是一样的。

#####    （3）@cacheEvict

  allEntries属性
 allEntries是boolean类型，表示是否需要清除缓存中的所有元素。默认为false，表示不需要。当指定了allEntries为true时，Spring Cache将忽略指定的key。有的时候我们需要Cache一下清除所有的元素，这比一个一个清除元素更有效率。

```java
  @CacheEvict(value="users", allEntries=true)
  public void delete(Integer id) {
     System.out.println("delete user by id: " + id);
  }
```

  beforeInvocation属性
 清除操作默认是在对应方法成功执行之后触发的，即方法如果因为抛出异常而未能成功返回时也不会触发清除操作。使用beforeInvocation可以改变触发清除操作的时间，当我们指定该属性值为true时，Spring会在调用该方法之前清除缓存中的指定元素。

```java
  @CacheEvict(value="users", beforeInvocation=true)
  public void delete(Integer id) {
     System.out.println("delete user by id: " + id);
  }
```

##### （4）@Caching

 @Caching注解可以让我们在一个方法或者类上同时指定多个Spring Cache相关的注解。其拥有三个属性：cacheable、put和evict，分别用于指定@Cacheable、@CachePut和@CacheEvict。