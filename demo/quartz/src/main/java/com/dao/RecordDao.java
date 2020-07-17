package com.dao;

import org.apache.ibatis.annotations.Insert;

public interface RecordDao {
    @Insert("insert into record (name) values(#{name})")
    int insert(String name);

}
