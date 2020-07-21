package com.config;


import com.bstek.ureport.export.ExportManager;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class TestExportManager implements InitializingBean {
    @Autowired
    @Qualifier(ExportManager.BEAN_ID)
    private ExportManager exportManager;


    @Override
    public void afterPropertiesSet() throws Exception {
        System.err.println(exportManager);
//        exportManager.exportHtml("file:demo.ureport.xml");
    }
}
