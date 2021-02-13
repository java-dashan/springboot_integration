//package com;
//
//import org.apache.zookeeper.*;
//import org.apache.zookeeper.data.Stat;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.concurrent.CountDownLatch;
//
//public class ZookeeperProSync implements Watcher {
//    // 设置为1,一个准备好就直接触发
//    private static CountDownLatch latch = new CountDownLatch(1);
//    private static ZooKeeper zk = null;
//    private static Stat stat = new Stat();
//
//    public static void main(String[] args) {
//        //zookeeper配置数据存储路径
//        String path = "/zkPro";
//        try {
//            zk = new ZooKeeper("172.16.1.41:2181", 5000, new ZookeeperProSync());
//            //等待zk连接成功的通知
//            latch.await();
//            List<String> children = zk.getChildren("/", false);
//
//            children.forEach(p -> {
//                try {
//                    byte[] data = zk.getData("/" + p, false, stat);
//                    if (data != null) {
//                        System.out.println(p+"------>"+new String(data));
//                    }
//                } catch (KeeperException e) {
//                    e.printStackTrace();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            });
////            System.out.println("zookeeper connection success");
////            zk.create(path, "cacacacaca".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
//////            //获取节点数据
//////            System.out.println(new String(zk.getData(path, false, stat)));
//////            //获取所有节点
//////            List<String> children = zk.getChildren("/", false);
//////            System.out.println(children);
////
////            //获取状态
////            System.out.println(zk.getState());
////
////            //设置节点信息
////            zk.setData(path, "wocao".getBytes(), -1);
////            //获取创建时间
////            Stat exists = zk.exists(path, false);
////            System.out.println(exists.getCtime());
////            //删除节点
////            zk.delete(path, -1);
////            //创建节点
////            String s = zk.create("/new", "news".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
////            System.out.println(s);
////            zk.delete("/new", -1);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (KeeperException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//
//
//    @Override
//    public void process(WatchedEvent watchedEvent) {
//        System.out.println(watchedEvent.getState());
//        if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
//            System.out.println("dsdasda");
//            latch.countDown();
//        }
//    }
//}
