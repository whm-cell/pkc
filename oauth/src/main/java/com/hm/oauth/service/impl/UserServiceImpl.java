package com.hm.oauth.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hm.oauth.entity.Permission;
import com.hm.oauth.entity.User;
import com.hm.oauth.mapper.UserMapper;
import com.hm.oauth.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @author whm
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public List<Permission> queryUserAuthorities(Integer userId) {
        return baseMapper.queryUserAuthorities(userId);
    }

    @Override
    public User queryUserByUsername(String username) {
        return baseMapper.queryUserByUsername(username);
    }
}
