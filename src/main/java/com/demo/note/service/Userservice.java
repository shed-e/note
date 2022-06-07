package com.demo.note.service;

import com.demo.note.dao.Userdao;
import com.demo.note.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Userservice {
    @Autowired
    Userdao userdao;

    public User login(String username,String password){
        return userdao.getByUsernameAndPassword(username,password);
        //登录
    }

    public User add(User user){
        userdao.save(user);
        return user;
        //注册
    }

    public User find(String username){
        return userdao.findByUsername(username);
        //根据uname查找user
    }





}
