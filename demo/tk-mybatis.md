# ''								tk-mybatis













#### 解决springboot配置文件yml中classpath找不到src/main/java下的xml文件

https://blog.csdn.net/qq_41357211/article/details/99855643

#### mapper.xml配置文件

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="org.sang.mapper.HrMapper">
    <resultMap id="BaseResultMap" type="org.sang.bean.Hr">
        <id column="id" property="id"/>
        <result column="name" property="name"/>
        <result column="phone" property="phone"/>
        <result column="telephone" property="telephone"/>
        <result column="address" property="address"/>
        <result column="enabled" property="enabled"/>
        <result column="username" property="username"/>
        <result column="password" property="password"/>
        <result column="userface" property="userface"/>
        <result column="remark" property="remark"/>
    </resultMap>
    <resultMap id="lazyLoadRoles" type="org.sang.bean.Hr" extends="BaseResultMap">
        <collection property="roles" ofType="org.sang.bean.Role" select="org.sang.mapper.HrMapper.getRolesByHrId"
                    column="id"></collection>
    </resultMap>
    <resultMap id="eagerLoadRoles" type="org.sang.bean.Hr" extends="BaseResultMap">
        <collection property="roles" ofType="org.sang.bean.Role">
            <id property="id" column="rid"/>
            <result property="name" column="rname"/>
            <result property="nameZh" column="rnameZh"/>
        </collection>
    </resultMap>
    <select id="loadUserByUsername" resultMap="lazyLoadRoles">
        select * from hr WHERE username=#{username};
    </select>
    <select id="getRolesByHrId" resultType="org.sang.bean.Role">
        SELECT r.* FROM hr_role h,role r where h.rid=r.id AND h.hrid=#{id}
    </select>
    <insert id="hrReg">
        INSERT INTO hr set username=#{username},password=#{password}
    </insert>
    <select id="getHrsByKeywords" resultMap="eagerLoadRoles">
        select h.*,`r`.`id` AS `rid`,`r`.`name` AS `rname`,`r`.`nameZh` AS `rnameZh` from ((`hr` `h` left join `hr_role`
        `h_r` on ((`h`.`id` = `h_r`.`hrid`))) left join `role` `r` on ((`h_r`.`rid` = `r`.`id`))) where h.`id` not
        in(select h_r.`hrid` from hr_role h_r,role r where h_r.`rid`=r.`id` and r.`nameZh`='系统管理员')
        <if test="keywords!='all' and keywords!=''">
            and h.`name` like concat('%',#{keywords},'%')
        </if>
        order by h.`id` limit 10
    </select>
    <select id="getHrById" resultMap="eagerLoadRoles">
        select h.*,`r`.`id` AS `rid`,`r`.`name` AS `rname`,`r`.`nameZh` AS `rnameZh` from ((`hr` `h` left join `hr_role`
        `h_r` on ((`h`.`id` = `h_r`.`hrid`))) left join `role` `r` on ((`h_r`.`rid` = `r`.`id`))) where h.`id`=#{hrId}
    </select>
    <update id="updateHr" parameterType="org.sang.bean.Hr">
        update hr
        <set>
            <if test="name != null">
                name = #{name,jdbcType=VARCHAR},
            </if>
            <if test="phone != null">
                phone = #{phone,jdbcType=CHAR},
            </if>
            <if test="telephone != null">
                telephone = #{telephone,jdbcType=VARCHAR},
            </if>
            <if test="address != null">
                address = #{address,jdbcType=VARCHAR},
            </if>
            <if test="enabled != null">
                enabled = #{enabled,jdbcType=BIT},
            </if>
            <if test="username != null">
                username = #{username,jdbcType=VARCHAR},
            </if>
            <if test="password != null">
                password = #{password,jdbcType=VARCHAR},
            </if>
            <if test="userface != null">
                userface = #{userface,jdbcType=VARCHAR},
            </if>
            <if test="remark != null">
                remark = #{remark,jdbcType=VARCHAR},
            </if>
        </set>
        where id = #{id}
    </update>
    <delete id="deleteRoleByHrId" parameterType="Long">
        DELETE FROM hr_role where hrid=#{hrId}
    </delete>
    <insert id="addRolesForHr">
        INSERT INTO hr_role(hrid,rid) VALUES
        <foreach collection="rids" separator="," item="rid">
            (#{hrId},#{rid})
        </foreach>
    </insert>
    <delete id="deleteHr" parameterType="Long">
        DELETE FROM hr WHERE id=#{hrId}
    </delete>
    <select id="getAllHr" resultType="org.sang.bean.Hr">
        select * from hr
        <if test="currentId!=null">
            WHERE id !=#{currentId}
        </if>
    </select>
</mapper>
```
