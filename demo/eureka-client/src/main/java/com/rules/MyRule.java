package com.rules;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.Server;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 一致性 hash 算法规则
 */
public class MyRule extends AbstractLoadBalancerRule {
    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {

    }

    @Override
    public Server choose(Object key) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String servletPath = request.getServletPath()+"?"+request.getQueryString();
        return router(servletPath.hashCode(),getLoadBalancer().getAllServers());
    }

    public Server router(int hashId, List<Server> serverList) {
        if (CollectionUtils.isEmpty(serverList)) {
            return null;
        }
        TreeMap<Long, Server> treeMap = new TreeMap<>();
        serverList.stream().forEach(server -> {
//            8个虚拟节点
            for (int i = 0; i < 8; i++) {
                long hash = hash(server.getId() + i);
                treeMap.put(hash, server);
            }
        });

        long hash = hash(String.valueOf(hashId));

//        得到 大于 该值的所有节点
        SortedMap<Long, Server> last = treeMap.tailMap(hash);

//        当 这个hash值后没有节点时，得到空
        if (last.isEmpty()) {
            return treeMap.firstEntry().getValue();
        }
//         取第一个
        return last.get(last.firstKey());
    }

    public long hash(String key) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return 0L;
    }
}
