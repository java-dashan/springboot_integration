package com.test.exec;

import com.test.job.Myjob;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

public class Test {
    public static void main(String[] args) throws SchedulerException {
//        JobDetail build = JobBuilder.newJob(Myjob.class)
//                .usingJobData("a", "b")
//                .withIdentity("da", "manager")
//                .build();
//
//        Trigger build1 = TriggerBuilder.newTrigger()
////                .forJob(build)
//                .startNow()
//                .usingJobData("a", "c")
//                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(3).repeatForever())
//                .build();
//
//        StdSchedulerFactory stdSchedulerFactory = new StdSchedulerFactory();
//        Scheduler scheduler = stdSchedulerFactory.getScheduler();
//        scheduler.scheduleJob(build,build1);
//        scheduler.start();


//        new MyThread().start();
//
//        new MyThread(() -> {
//            System.out.println("myThread runable run");
//        }).start();
//
        new Thread(() -> System.out.println("Thread runable run")).start();

        new Thread(() -> System.out.println("a Thread runable run")) {
            @Override
            public void run (){
                System.out.println("a Thread  run");
                super.run();
            }
        }.start();
    }

    static class MyThread extends Thread {
        public MyThread() {
        }

        public MyThread(Runnable target) {
            super(target);
        }

        @Override
        public void run() {
            System.out.println("myThread run");
        }
    }
}
