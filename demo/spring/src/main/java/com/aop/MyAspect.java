package com.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

@Component
@Aspect
@EnableAspectJAutoProxy
public class MyAspect {

    @Pointcut("execution(* com.entity.*.*(..))")
    public void aspect() {

    }

    @Before("aspect()")
    public void before(JoinPoint joinPoint) {

        Class target = joinPoint.getTarget().getClass();
        //拿到方法签名
        Signature signature = joinPoint.getSignature();

        MethodSignature methodSignature = (MethodSignature) signature;
//        Method method1 = ((MethodSignature) signature).getMethod();

        Class[] classes = methodSignature.getParameterTypes();

        Object[] args = joinPoint.getArgs();
        //拿到方法名
        String name = signature.getName();
        //拿到方法参数
        try {
//            Method method = target.getMethod()
            Method method = target.getMethod(name, classes);
            Annotation[] declaredAnnotations = method.getDeclaredAnnotations();
            for (int i = 0; i < declaredAnnotations.length; i++) {
                Annotation declaredAnnotation = declaredAnnotations[i];
                if ( declaredAnnotation instanceof PostMapping) {
                    System.out.println("新增");
                } else if (declaredAnnotation instanceof Override) {
                    System.out.println("复写方法");
                }
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @After("aspect()")
    public void after() {

    }

    @Around("aspect()")
    public Object around(JoinPoint joinPoint) {
        //环切必须返回 ((ProceedingJoinPoint)joinPoint).proceed()
        try {
            return ((ProceedingJoinPoint)joinPoint).proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }

    //配置后置返回通知,使用在方法aspect()上注册的切入点
    @AfterReturning("aspect()")
    public void afterReturn(JoinPoint joinPoint) {
        System.out.println("afterReturn");
    }

    //配置抛出异常后通知,使用在方法aspect()上注册的切入点
    @AfterThrowing(pointcut = "aspect()", throwing = "ex")
    public void afterThrow(JoinPoint joinPoint, Exception ex) {
        System.out.println("afterThrow");
    }
}
