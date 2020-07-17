package com.service;


import com.dao.UserDao;
import com.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class EhcacheService{

    @Autowired
    private UserDao userDao;

    @Cacheable(value = {"user"},key = "#username",condition = "#username%2 == 0")
    public User findByUsername(String username) {
        System.out.println("aaaa");
        return userDao.selectByUsername(username);
    }

    @CachePut(value = {"user"}, key = "#username", condition = "#username%2 == 0")
    public User login(String username) {
        System.out.println("aaaa");

        return userDao.selectByUsername(username);
    }

    @CacheEvict(value = "user",key = "#username")
    public void logout(String username){

    }



    @CacheEvict(value = "user",allEntries = true,beforeInvocation = true)
    public void removeUserCache(String username){

    }
}
