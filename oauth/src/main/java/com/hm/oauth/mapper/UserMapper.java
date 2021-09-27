package com.hm.oauth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hm.oauth.entity.Permission;
import com.hm.oauth.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author ltq
 * @since 2019-08-14
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    List<Permission> queryUserAuthorities(Integer userId);
    User queryUserByUsername(String username);
}
