package com.dao;

import com.entity.User;
import org.apache.ibatis.annotations.Select;

public interface UserDao {

    @Select("select * from user where username = #{username}")
    User selectByUsername(String username);

}
