package com.hm.oauth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hm.oauth.entity.Permission;
import com.hm.oauth.entity.User;

import java.util.List;

/**
 * @author 28236
 */
public interface IUserService extends IService<User> {
    /**
     * @param userId
     * @return
     */
    List<Permission> queryUserAuthorities(Integer userId);

    /**
     * @param username
     * @return
     */
    User queryUserByUsername(String username);
}
