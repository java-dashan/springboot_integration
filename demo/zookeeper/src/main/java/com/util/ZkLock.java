package com.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Slf4j
public class ZkLock implements AutoCloseable, Watcher {

    public ZooKeeper zooKeeper;

    public String zNode;

    public ZkLock(String addr, Integer expireTime) throws IOException {
        this.zooKeeper = new ZooKeeper(addr, expireTime, this);
    }

    public ZkLock() throws IOException {
        this.zooKeeper = new ZooKeeper("192.168.139.101:2181", 10000, this);
    }

    public boolean getLock(String businessCode) {
        try {
            Stat stat = zooKeeper.exists("/" + businessCode, false);
            if (stat == null) {
                zooKeeper.create("/" + businessCode, businessCode.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
//          有序瞬时节点
            zNode = zooKeeper.create("/" + businessCode + "/" + businessCode + "_", businessCode.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);

            List<String> children = zooKeeper.getChildren("/" + businessCode, false);
            Collections.sort(children);
            String first = children.get(0);
            if (zNode.endsWith(first)) {
                return true;
            }
//          不是一个节点，则监听前一个节点
            String last = first;
            for (String node : children) {
                if (zNode.endsWith(node)) {
                    zooKeeper.exists("/" + businessCode + "/" + last, true);
                } else {
                    last = node;
                }
            }

            synchronized (this) {
                wait();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    @Override
    public void close() throws Exception {
        zooKeeper.delete(zNode, -1);
        zooKeeper.close();
        log.info("我已经释放了锁");
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        if (watchedEvent.getType() == Event.EventType.NodeDeleted) {
            synchronized (this) {
                notify();
            }
        }
    }
}
