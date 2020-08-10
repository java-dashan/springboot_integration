1. 关于文件下载前端无法生成下载文件  (无论使用ajax,还是axios都得这么做)
    1.使用 window.location.href = "下载地址"
    2.后端response头设置
            response.reset(); //重置response头信息
            response.setHeader("Content-Disposition", "attachment; filename="+filename);
            response.addHeader("Content-Length", "" + data.length);
            response.setContentType("application/octet-stream; charset=UTF-8");
            // 下载类型具体改成什么参照: https://blog.csdn.net/asd54090/article/details/80920564
                

2. 时间格式化:
    1.自定义类 CustomDateSerializer 继承com.fasterxml.jackson.databind.JsonSerializer,实现方法
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            gen.writeString(value.getTime()+"");
    2.在字段上使用注解@JsonSerialize(using = CustomDateSerializer.class)

3.spring扩展点: https://segmentfault.com/a/1190000023033670

4.GateWay : ServerWebExchangeUtils,RouteDefinitionLocator接口,
            AbstractRoutePredicateFactory<HeaderRoutePredicateFactory.Config>抽象类
            PreGatewayFilterFactory
            PostGatewayFilterFactory
            GlobalFilter

5.spring在多线程下,bean的注入问题:
    1.将需要的Bean作为线程的的构造函数的参数传入
    2.使用ApplicationContext.getBean方法来静态的获取Bean
    3.使用内部类
    4.静态代码块

6.SpringBoot使用过滤器、拦截器、切面(AOP)，及其之间的区别和执行顺序
    Filter -> DispatcherServlet -> HandlerIntercept -> Aspect -> Controller  























