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
    
    ```java
    //方式一
    @Component
    public class DateConverterConfig implements Converter<String, Date> {
    
        private static final List<String> formarts = new ArrayList<>(4);
        static{
            formarts.add("yyyy-MM");
            formarts.add("yyyy-MM-dd");
            formarts.add("yyyy-MM-dd hh:mm");
            formarts.add("yyyy-MM-dd HH:mm:ss");
        }
    
        @Override
        public Date convert(String source) {
            String value = source.trim();
            if ("".equals(value)) {
                return null;
            }
            if(source.matches("^\\d{4}-\\d{1,2}$")){
                return parseDate(source, formarts.get(0));
            }else if(source.matches("^\\d{4}-\\d{1,2}-\\d{1,2}$")){
                return parseDate(source, formarts.get(1));
            }else if(source.matches("^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}$")){
                return parseDate(source, formarts.get(2));
            }else if(source.matches("^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}:\\d{1,2}$")){
                return parseDate(source, formarts.get(3));
            }else {
                throw new IllegalArgumentException("Invalid boolean value '" + source + "'");
            }
        }
    
        /**
            * 格式化日期
            * @param dateStr String 字符型日期
            * @param format String 格式
            * @return Date 日期
            */
        public  Date parseDate(String dateStr, String format) {
            Date date=null;
            try {
                DateFormat dateFormat = new SimpleDateFormat(format);
                date = dateFormat.parse(dateStr);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return date;
        }
    
    }
    
    //方式二
    @ControllerAdvice
    public class DateAdvice {
    
        @InitBinder
        public void initBinder(WebDataBinder binder) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dateFormat.setLenient(false);//是否严格解析时间 false则严格解析 true宽松解析
            binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
        }
    }
    
    
    //方式三
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setLenient(false);//是否严格解析时间 false则严格解析 true宽松解析
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }
    
    @Component
    public class DateEditor extends PropertyEditorSupport {
        @Override  
        public void setAsText(String text) throws IllegalArgumentException {  
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = null;
            try {  
                date = format.parse(text);  
            } catch (ParseException e) {
                format = new SimpleDateFormat("yyyy-MM-dd");  
                try {  
                    date = format.parse(text);  
                } catch (ParseException e1) {  
                    e1.printStackTrace();  
                }  
            }  
            setValue(date);  
        }  
    }   
    
    //方式四
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    // 结束时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
    
    ```
    
    




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


7.ID过长,前端报错to long:
    @JsonSerialize(using = ToStringSerializer.class) //使用自带工具类
	private Long id;
	
	public class MyLongConverter extends JsonSerializer<Long> {
	@Override
	public void serialize(Long value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
	    if (value.toString().length() > 12) {
	    gen.writeString(value.toString());
	    log.info("the Long value is to long. will convert to String");
	    } else {
	    gen.writeNumber(value);
	    }
	}
	}
	@JsonSerialize(using = MyLongConverter.class) //使用自定义
	private Long id;






















