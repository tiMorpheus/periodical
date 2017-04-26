package com.tolochko.periodicals.model.service;

import com.tolochko.periodicals.model.domain.user.User;

import java.util.List;


public interface UserService {
    User findOneById(long id);

    User findOneUserByUserName(String userName);

    List<User> findAll();

    boolean createNewUser(User user);

    boolean emailExistsInDb(String email);

    void update(User user);

}
