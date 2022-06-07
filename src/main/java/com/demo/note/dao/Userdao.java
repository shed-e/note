package com.demo.note.dao;

import com.demo.note.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Userdao extends JpaRepository<User,Integer> {

    User getByUsernameAndPassword(String username,String password);

    User findByUsername(String username);

    User save(User user);




}
