package com.tolochko.periodicals.model.dao.interfaces;

import com.tolochko.periodicals.model.domain.user.User;

public interface UserDao extends GenericDao<User, Long> {

    User findOneByUserName(String userName);

    boolean emailExistsInDb(String email);
}
