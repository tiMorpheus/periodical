package com.tolochko.periodicals.model.service.impl;

import com.tolochko.periodicals.model.TransactionHelper;
import com.tolochko.periodicals.model.connection.ConnectionProxy;
import com.tolochko.periodicals.model.dao.exception.DaoException;
import com.tolochko.periodicals.model.dao.factory.DaoFactory;
import com.tolochko.periodicals.model.dao.factory.impl.MySqlDaoFactory;
import com.tolochko.periodicals.model.domain.user.User;
import com.tolochko.periodicals.model.service.UserService;
import org.apache.log4j.Logger;

import java.util.List;

import static java.util.Objects.nonNull;


public class UserServiceImpl implements UserService {
    private static final Logger logger = Logger.getLogger(UserServiceImpl.class);

    private DaoFactory factory = MySqlDaoFactory.getFactoryInstance();

    @Override
    public User findOneById(long id) {

        User user = factory.getUserDao().findOneById(id);

        setUserRole(user);
        return user;

    }

    private void setUserRole(User user) {
        if (nonNull(user)) {
            user.setRole(factory.getRoleDao().findRoleByUserName(user.getUsername()));
        }
    }

    @Override
    public User findOneUserByUserName(String userName) {

        User user = factory.getUserDao().findOneByUserName(userName);

        setUserRole(user);

        return user;

    }

    @Override
    public List<User> findAll() {

        List<User> allUser = factory.getUserDao().findAll();
        allUser.forEach(user -> user.setRole(factory.getRoleDao()
                .findRoleByUserName(user.getUsername())));

        return allUser;


    }

    @Override
    public boolean createNewUser(User user) {

        try {
            TransactionHelper.beginTransaction();

            Long userId = factory.getUserDao().add(user);

            if (userId == 0) {
                TransactionHelper.rollback();
                return false;
            }

            factory.getRoleDao().addRole(userId, User.Role.SUBSCRIBER);

            TransactionHelper.commit();

            return true;
        } catch (RuntimeException e) {
            TransactionHelper.rollback();
            logger.error("transaction failed", e);
            throw new DaoException(e);
        }
    }

    @Override
    public boolean emailExistsInDb(String email) {


        return factory.getUserDao().emailExistsInDb(email);

    }

    @Override
    public void update(User user) {

        long id = user.getId();
        logger.debug("inside userService update id:" + id);
        int result = factory.getUserDao().updateById(id, user);
        logger.debug("updating result" + result);

    }


}
