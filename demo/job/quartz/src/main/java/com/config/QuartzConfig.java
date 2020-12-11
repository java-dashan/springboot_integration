package com.config;

import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class QuartzConfig  {
    @Bean
    public JobDetail jobDetail1(){
        return JobBuilder.newJob(MyQuartzJob.class).withIdentity("MyQuartzJob1").usingJobData("key","value").storeDurably().build();
    }

    @Bean
    public JobDetail jobDetail2(){
        return JobBuilder.newJob(MyQuartzJob1.class).withIdentity("MyQuartzJob2").usingJobData("key","value").storeDurably().build();
    }




    @Bean
    public Trigger trigger1(){
        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInSeconds(500) //每一秒执行一次
                .repeatForever(); //永久重复，一直执行下去
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail1())
                .withSchedule(scheduleBuilder)
                .build();
    }

    @Bean
    public Trigger trigger2(){
        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInSeconds(500) //每一秒执行一次
                .repeatForever(); //永久重复，一直执行下去
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail2())
                .withSchedule(scheduleBuilder)
                .build();
    }
}
