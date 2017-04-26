package com.tolochko.periodicals.model.dao.interfaces;

import com.tolochko.periodicals.model.domain.user.User;

public interface RoleDao {

    User.Role findRoleByUserName(String userName);

    void addRole(long userId, User.Role role);
}
