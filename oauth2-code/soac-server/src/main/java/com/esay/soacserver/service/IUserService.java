package com.esay.soacserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.esay.soacserver.entity.Permission;
import com.esay.soacserver.entity.User;

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

    /**
     * @return
     */
    List<User> selectAll();
}
