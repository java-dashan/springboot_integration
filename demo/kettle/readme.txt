mvn install:install-file -Dfile=D:\data-integration\lib\kettle-core-7.0.0.0-25.jar -DgroupId=pentaho-kettle -DartifactId=kettle-core -Dversion=7.0.0.0-25 -Dpackaging=jar

mvn install:install-file -Dfile=D:\data-integration\lib\kettle-engine-7.0.0.0-25.jar -DgroupId=pentaho-kettle -DartifactId=kettle-engine -Dversion=7.0.0.0-25 -Dpackaging=jar

mvn install:install-file -Dfile=D:\data-integration\lib\metastore-7.0.0.0-25.jar -DgroupId=pentaho-kettle -DartifactId=metastore -Dversion=7.0.0.0-25 -Dpackaging=jar

mvn install:install-file -Dfile=D:\data-integration\lib\simple-jndi-1.0.0.jar -DgroupId=pentaho-kettle -DartifactId=simple-jndi -Dversion=1.0.0 -Dpackaging=jar

<dependencies>
    <dependency>
        <groupId>pentaho-kettle</groupId>
        <artifactId>simple-jndi</artifactId>
        <version>1.0.0</version>
    </dependency>
    <dependency>
        <groupId>pentaho-kettle</groupId>
        <artifactId>kettle-core</artifactId>
        <version>7.0.0.0-25</version>
    </dependency>
    <dependency>
        <groupId>pentaho-kettle</groupId>
        <artifactId>kettle-engine</artifactId>
        <version>7.0.0.0-25</version>
    </dependency>
    <dependency>
        <groupId>pentaho-kettle</groupId>
        <artifactId>metastore</artifactId>
        <version>7.0.0.0-25</version>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-vfs2</artifactId>
        <version>2.1</version>
    </dependency>

    <!--   必须加入mysql连接驱动      -->
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
    </dependency>

    <!-- https://mvnrepository.com/artifact/com.google.guava/guava -->
    <dependency>
        <groupId>com.google.guava</groupId>
        <artifactId>guava</artifactId>
        <version>27.1-jre</version>
    </dependency>

    <dependency>
        <groupId>commons-io</groupId>
        <artifactId>commons-io</artifactId>
        <version>2.2</version>
    </dependency>

    <!-- https://mvnrepository.com/artifact/commons-lang/commons-lang -->
    <dependency>
        <groupId>commons-lang</groupId>
        <artifactId>commons-lang</artifactId>
        <version>2.6</version>
    </dependency>

    <!-- https://mvnrepository.com/artifact/cn.hutool/hutool-all -->
    <dependency>
        <groupId>cn.hutool</groupId>
        <artifactId>hutool-all</artifactId>
        <version>4.5.6</version>
    </dependency>
</dependencies>