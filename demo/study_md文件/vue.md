# 						VUE学习手册





















## 拓展

### 1.axios 的使用：

https://www.cnblogs.com/libin-1/p/6607945.html

### 2.elementUI

https://www.cnblogs.com/yuyujuan/p/10267749.html

- 修改主题色

cnpm install node-sass sass-loader --save-dev

### 3.vue集成表情包

 https://blog.csdn.net/weixin_42460570/article/details/103493438 

 npm install --save emoji-mart-vue 

 import { Picker } from "emoji-mart-vue"; *//引入组件* 

```javascript
<template>
  <div class="container">
    <!-- 
    search: 'Search Results',
    recent: 'Frequently Used',
    people: 'Smileys & People',
    nature: 'Animals & Nature',
    foods: 'Food & Drink',
    activity: 'Activity',
    places: 'Travel & Places',
    objects: 'Objects',
    symbols: 'Symbols',
    flags: 'Flags',
    custom: 'Custom',-->
    <textarea name="emoji" v-model="content" cols="30" rows="10"></textarea>
    <picker
      :include="['people']"  //添加自己需要的表情类型
      :showSearch="false"    //搜索框显示
      :showPreview="false" 
      :showCategories="false"
      @select="addEmoji"
    />
  </div>
</template>
<script>
import { Picker } from "emoji-mart-vue";
export default {
  data() {
    return {
      content: ""
    };
  },
  methods: {
    addEmoji(e) {
      this.content += e.native;
    }
  },
  created() {},
  mounted() {},
  components: {
    Picker
  }
};
</script>
<style scoped>
.emoji-mart[data-v-7bc71df8] {
  font-family: -apple-system, BlinkMacSystemFont, "Helvetica Neue", sans-serif;
  display: -ms-flexbox;
  display: flex;
  -ms-flex-direction: column;
  flex-direction: column;
  height: 420px;
  color: #ffffff !important;
  border: 1px solid #d9d9d9;
  border-radius: 5px;
  background: #fff;
}
.text {
  display: block;
  margin: 0 auto;
  margin-bottom: 10px;
  width: 400px;
  resize: none;
  box-sizing: border-box;
  padding: 5px 10px;
  border-radius: 8px;
}
.text-place {
  height: 80px;
  box-sizing: border-box;
  padding: 5px 10px;
  border-radius: 8px;
  background: #ddd5d5;
}
.text-place p {
  display: flex;
  align-items: center;
  margin: 0;
}
</style>
```



### 4.vue集成富文本编辑器

```java
//项目链接：https://github.com/MigoNoSalt/vue-ueditor
//1.下载Uediter,将jsp下的src文件夹复制到springboot项目中
//2.其他的复制到static下面去
//3.将jsp controller.jsp文件内容复制到新的controller去
//4.修改ueditor.config.js serverUrl改成3中的路径

```

### 5.vue-router

```javascript
//this.$router   用作跳转
//如果使用完整路径和query传参，刷新页面时不会造成路由传参的参数丢失。path加 '/' 会被当做路径,就不会一直嵌套之前的路径。
this.$router.push({
    path:'/',
    query:{
        'id',1
    }
})
//
this.$router.push({
    name:'user',
    params:{
        'id',1
    }
})
//this.$route    用来获取参数
id = this.$route.query.userId
id = this.$route.params.userId
```

### 6.文件上传下载

```javascript
<input type="file"
           name="file"
           multiple='multiple'
           @change="getFile($event)">
get($event){
	//通过目标事件获取文件集合               
	const files = event.target.files               
	//通过选择器获取文件集合
	const files1 = document.querySelector(['input[type=file]']) 
    //创建FormData,通过formdata传递
    var formData = new FormData()
    for(var i = 0; i<files.length; i++){
        //键要和后台保持一致
        formData.append('file',files[i])
    }
	//this.axios.post(path,data,config)               
    this.axios.post('/api/upload',formData,{
            headers: {
              'Content-Type': 'multipart/form-data',
              'Authorization': this.token
            }
          })  
      //文件转换成imgurl(:src) 预览
      let url = '';
      this.isUpload = true;
      var reader = new FileReader();
      reader.readAsDataURL(files[0]);
      let that = this;
      reader.onload = function (e) {
        //读取的结果就在result中
        url = this.result.substring(this.result.indexOf(',') + 1);
        that.imgUrl = 'data:image/png;base64,' + url
        // that.$refs['imgimg'].setAttribute('src','data:image/png;base64,'+url);
      }
}               
```

```java
String path = "E:\\springboot\\";
    /**
     * 多文件上传
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping("/upload")
    public String upload(HttpServletRequest request) throws Exception{
        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");
        MultipartFile file = null;
        List newFileNames = new ArrayList();
        if (files != null) {
            for (int i = 0; i < files.size(); i++) {
                file = files.get(i);
                String originalFilename = file.getOriginalFilename();
                String substring = originalFilename.substring(originalFilename.lastIndexOf("."));
                String  random = new Random().nextInt(100000)+"";
                String newFileName = random  + substring;
                newFileNames.add(newFileName);

                File dest = new File(path + newFileName);
                file.transferTo(dest);
            }
        }
        return newFileNames.toString();
    }
    /**
     * 单文件下载  
     * @param response
     * @param filename
     * @return
     */
    @GetMapping("/downloadzip")
    public String downloadzip(HttpServletResponse response, String filename){
        byte[] buffer = new byte[1024];
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        File file = new File(path + filename);
        if (file.exists()) {
            try {
//                response.setHeader("content-type", "application/octet-stream");
                //response.setContentType("application/octet-stream"); 这个下载zip文件打不开
                response.setContentType("application/x-download");
                // 下载文件能正常显示中文
                response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
                // 实现文件下载
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);

                ServletOutputStream outputStream = response.getOutputStream();
                int i = bis.read(buffer);
                while (i != -1) {
                    outputStream.write(buffer, 0, i);
                    i = bis.read(buffer);
                }
                outputStream.flush();
                return "下载成功";
            } catch (Exception e) {
                e.printStackTrace();
                return "下载失败";
            }finally {
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }else {
            return "下载失败";
        }
    }
//多文件下载： 将多个文件转成zip  然后再下载
 @GetMapping("/downloadList")
    public String down(HttpServletResponse response,String arr){
        if (arr == null) {
            return "请输入文件名";
        }else {
            String[] filenames = arr.split(",");
            ZipOutputStream zip = null;
            BufferedInputStream bis = null;
            FileInputStream fis = null;
            String ZipName = new Random().nextInt(100) + ".rar";
            File file = new File(path + ZipName);
            try {
                if (!file.exists()) {
                    file.createNewFile();
                }
                byte[] b = new byte[1024];
                zip = new ZipOutputStream(new FileOutputStream(file));
                for (int i = 0; i < filenames.length; i++) {
                    String filename = filenames[i];
                    File file1 = new File(path + filename);
                    fis = new FileInputStream(file1);
                    bis = new BufferedInputStream(fis);
                    ZipEntry zipEntry = new ZipEntry(filename);
                    zip.putNextEntry(zipEntry);

                    int read = bis.read(b);
                    while (read != -1) {
                        zip.write(b,0,read);
                        zip.flush();
                        read = bis.read(b);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    bis.close();
                    zip.flush();
                    zip.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                file.delete();
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "aaa";
        }
    }
```

### 7.在线聊天

```javascript
//1.建立连接
//2.发送信息
//3.推送信息
export default {
  data() {
    return {
      name: "", // 昵称
      websocket: null, // WebSocket对象
      aisle: "", // 对方频道号
      messageList: [], // 消息列表
      messageValue: "" // 消息内容
    };
  },
  methods:{
	connectWebSocket(){
        //判断当前浏览器是否支持websocket
        if("WebSocket" in window){
            //创建websocket对象
            this.websocket = new WebSocket("ws://localhost:9999/websocket/"+this.name)
        }else{
            alert("当前浏览器不支持websocket连接")
        }
        //连接发生错误  回调函数
        this.websocket.onerror = function(){}
        //连接成功建立的回调方法
        this.websocket.onopen = function(event) {}
        var that = this
        //接收消息回调函数 event参数包含接收的参数
        this.websocket.onmessage = function(event){
            //该参数为字符串 通过函数eval转换成对象
            var object = eval("("+ event.data +")");
            //或者通过JSON.parse(event.data)
        }
        //连接关闭回调函数
        this.websocket.onclose = function(){}
        //监听窗口关闭,自动关闭websocket连接
        window.onbeforeunload = function() {
            //关闭连接
            this.websocket.close()
        } 
    },
    sendMessage(){
      var socketMsg = { msg: this.messageValue, toUser: this.aisle };
      if (this.aisle == "") {
        //群聊.
        socketMsg.type = 0;
      } else {
        //单聊.
        socketMsg.type = 1;
      }
        this.websocket.send(JSON.stringify())
    }  
  },
  created(){
      //获取用户信息,赋值给user
      
  }
}
```





## 问题及解答

### 1.项目中向子组件传递参数页面不渲染(参数是异步)

```javascript
//watch监听prop
watch: {
    value: function (newValue, oldValue) {
      // this.categorys = newValue;
      this.test()
    }
  }
```

### 2.等待axios完成后再执行下面的代码

```javascript
upload(){
     //这里的then 处理完成后会返回一个 Promise，其中如果没有异常，
     //其 then return 的值会作为下一次 then 的参数。
    return this.axios.post(path, data, condig).then({})
}

created(){
    //匿名函数
    this.upload().then( ()=>{
        console.log('xxx')
    })
}

```

