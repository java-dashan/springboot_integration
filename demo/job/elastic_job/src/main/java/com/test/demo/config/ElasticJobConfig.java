package com.test.demo.config;

import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.event.JobEventConfiguration;
import com.dangdang.ddframe.job.event.rdb.JobEventRdbConfiguration;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.base.CoordinatorRegistryCenter;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.test.demo.job.MySimpleJob;
import com.test.demo.listener.MyElasticJobListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class ElasticJobConfig {

    @Autowired
    private JobEventConfiguration jobEventConfiguration;
    @Autowired
    private MySimpleJob simpleJob;
    @Autowired
    private CoordinatorRegistryCenter coordinatorRegistryCenter;
    @Autowired
    private MyElasticJobListener myElasticJobListener;


    @Bean(initMethod = "init")
    public SpringJobScheduler springJobScheduler() {
        return new SpringJobScheduler(simpleJob, coordinatorRegistryCenter, getLiteJobConfiguration(), jobEventConfiguration, myElasticJobListener);
    }

    @Bean(initMethod = "init")
    public CoordinatorRegistryCenter coordinatorRegistryCenter(@Value("${elastic.job.serverLists}") String serverLists,
                                                               @Value("${elastic.job.namespace}") String namespace) {
        ZookeeperConfiguration zookeeperConfiguration = new ZookeeperConfiguration(serverLists, namespace);
        return new ZookeeperRegistryCenter(zookeeperConfiguration);
    }

    private LiteJobConfiguration getLiteJobConfiguration() {
        JobCoreConfiguration test = JobCoreConfiguration
                .newBuilder("test111", "0/5 * * * * ?", 5)
                .failover(true)
                .misfire(true)
                .shardingItemParameters("0=RDP,1=CORE,2=SIMS,3=ECIF")
                .build();

        SimpleJobConfiguration simpleJobConfiguration = new SimpleJobConfiguration(test, "com.test.demo.job.MySimpleJob");

        LiteJobConfiguration build = LiteJobConfiguration.newBuilder(simpleJobConfiguration)
                .overwrite(true)
//                .disabled(true)
                .build();
        return build;
    }

    @Bean
    public JobEventConfiguration jobEventConfiguration(DataSource dataSource) {
        return new JobEventRdbConfiguration(dataSource);
    }

}
