package com.fastdfs.cn.controller;

import com.fastdfs.cn.service.FdfsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileContoller {
    @Autowired
    private FdfsService fdfsService;

    public String upload(MultipartFile file) {
        if (file == null) {
            return "文件为空";
        }
        String[] split = file.getOriginalFilename().split("\\.");
        String upload = null;
        try {
            upload = fdfsService.upload(file, split[split.length - 1]);
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return upload;
    }
}
