package com.esay.soacserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.esay.soacserver.entity.Permission;
import com.esay.soacserver.entity.User;
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

    List<User> selectAll();
}
