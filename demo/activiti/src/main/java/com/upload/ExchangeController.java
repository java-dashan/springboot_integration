package com.upload;

import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

@RestController
public class ExchangeController {

    static String filename = "snp_imp_template.dat";
    //    @Autowired
    static String message = "";

    @PostMapping("/upload")
    public synchronized String upload(MultipartFile file) {
        InputStream in = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        OutputStream outStream = null;
        try {
            in = file.getInputStream();
            inputStreamReader = new InputStreamReader(in);
            bufferedReader = new BufferedReader(inputStreamReader);
            String s = bufferedReader.readLine();
            String s1 = s.replaceAll("\\t", ",");
            System.out.println(s);
            String[] title = s1.split(",");
            StringBuffer buffer = new StringBuffer();
            while (bufferedReader.ready()) {
                String message = bufferedReader.readLine();
                String s2 = message.replaceAll("\\t", ",");
                parse(buffer, s2, title);
            }
            message = buffer.toString();
            return "ok";
        } catch (IOException e) {
            return "failed";
        } finally {
            try {
                bufferedReader.close();
                inputStreamReader.close();
                in.close();
//                outStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @GetMapping("/download")
    public synchronized void download(HttpServletResponse response) {
        byte[] data = message.getBytes();
        response.reset();
        response.setHeader("Content-Disposition", "attachment; filename="+filename);
        response.addHeader("Content-Length", "" + data.length);
        response.setContentType("application/octet-stream; charset=UTF-8");
        try {
            IOUtils.write(data,response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/exchange")
    public void exchange(MultipartFile file, HttpServletRequest request, HttpServletResponse response) throws IOException {
        InputStream in = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        OutputStream outStream = null;
        try {
            in = file.getInputStream();
            inputStreamReader = new InputStreamReader(in);
            bufferedReader = new BufferedReader(inputStreamReader);
            String s = bufferedReader.readLine();
            String s1 = s.replaceAll("\\t", ",");
            System.out.println(s);
            String[] title = s1.split(",");
            StringBuffer buffer = new StringBuffer();
            while (bufferedReader.ready()) {
                String message = bufferedReader.readLine();
                String s2 = message.replaceAll("\\t", ",");
                parse(buffer, s2, title);
            }
            byte[] data = buffer.toString().getBytes();
//            response.setHeader("content-type", "application/octet-stream");
//            response.setContentType("application/octet-stream");
//            response.addHeader("Content-Length", "" + data.length);
//            response.setHeader("Content-Disposition", "attachment;filename=" + filename);
            response.reset();
            response.setHeader("Content-Disposition", "attachment; filename="+filename);
            response.addHeader("Content-Length", "" + data.length);
            response.setContentType("application/octet-stream; charset=UTF-8");
            IOUtils.write(data,response.getOutputStream());
        } catch (IOException e) {

        } finally {
            try {
                bufferedReader.close();
                inputStreamReader.close();
                in.close();
//                outStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static StringBuffer parse(StringBuffer buffer, String message, String[] arr) {
        String[] split = message.split(",");
        int length = message.length();
        String[] split2 = message.split(",");
        int index = length - ((split2.length - 6) * 2 - 1);
        String substring = message.substring(0, index);
//        System.out.println("subString:"+substring);
        String suffix = message.substring(index, length);
        String[] split1 = suffix.split(",");
        for (int i = 6; i < arr.length; i++) {
            if (i < (split1.length + 6)) {
                buffer.append(arr[i] + ",");
                buffer.append(substring);
                buffer.append(split1[i - 6]);
                buffer.append("\n");
            }
        }
//        System.out.println(buffer);
        return buffer;
    }

}
