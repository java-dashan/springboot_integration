package com.config;

import com.dao.RecordDao;
import com.service.RecordService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.text.SimpleDateFormat;
import java.util.Date;


//@Component
public class MyQuartzJob1 extends QuartzJobBean {

    @Autowired
    private RecordDao recordDao;

    @Autowired
    private RecordService recordService;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.err.println("QuartzJob2----" + sdf.format(new Date()));
    }
}
