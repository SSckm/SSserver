package org.sms.project.user.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.sms.project.common.ResultAdd;
import org.sms.project.encrypt.md5.MD5;
import org.sms.project.page.Page;
import org.sms.project.user.dao.UserDao;
import org.sms.project.user.entity.User;
import org.sms.project.user.service.UserService;
import org.sms.project.util.AccountValidatorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Sunny
 */
@Service("userService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao sysUserDao;

    @Override
    public ResultAdd insert(User user) {
        ResultAdd resAdd = new ResultAdd();
        boolean isCorrect = this.checkUser(user, resAdd);
        if (!isCorrect) {
            return resAdd;
        }
        user.setLoginSum(0);
        user.setLastLoginDate(new Date());
        user.setCreateDate(new Date());
        user.setLoginType(0);
        int count = this.findUserByEmailAndName(user.getEmail(), user.getName());
        if (count >= 1) {
            resAdd.setCode(0);
            resAdd.setError("用户名或邮箱已被占用");
            return resAdd;
        }
        String salt = MD5.encrypt(user.getPassword().trim());
        user.setPassword(salt);
        long successInsert = sysUserDao.insert(user);
        if (successInsert <= 0) {
            resAdd.setCode(0);
            resAdd.setError("服务端错误");
            return resAdd;
        }
        resAdd.setCode(1);
        resAdd.setError("添加成功");
        return resAdd;
    }

    @Override
    public int findUserByEmailAndName(String email, String name) {
        return sysUserDao.findUserByEmailAndName(email, name);
    }

    @Override
    public int update(User user) {
        return sysUserDao.update(user);
    }

    @Override
    public int delete(long id) {
        return sysUserDao.delete(id);
    }

    @Override
    public List<User> queryByCondition(String query, String order, Page page) {
        return sysUserDao.queryByCondition(query, order, page);
    }

    @Override
    public List<User> queryByCondition(Page page) {
        return queryByCondition(null, null, page);
    }

    @Override
    public User findById(long id) {
        return sysUserDao.findById(id);
    }

    @Override
    public User findUserByEmail(String email) {
        return sysUserDao.findUserByEmail(email);
    }

    @Override
    public int getCount() {
        return sysUserDao.getCount();
    }
    
    private boolean checkUser(User user, ResultAdd resAdd) {
        String name = user.getName();
        String email = user.getEmail();
        String password = user.getPassword();
        if (name == null || email == null || password == null || name.trim() == "" || email.trim() == "" || password.trim() == "") {
            resAdd.setCode(0);
            resAdd.setError("用户名密码为空");
            return false;
        }
        if (name.length() >= 40 || password.length() < 3 || password.length() > 18) {
            resAdd.setCode(0);
            resAdd.setError("用户名密码长度错误");
            return false;
        }
        
        if (!AccountValidatorUtil.isEmail(email) || !AccountValidatorUtil.isPassword(password)) {
            resAdd.setCode(0);
            resAdd.setError("邮箱或者密码格式不正确");
            return false;
        }
        if (user.getAddress() != null && user.getAddress().length() >= 200) {
            resAdd.setCode(0);
            resAdd.setError("地址长度过长");
            return false;
        }
        
        if (!Objects.isNull(user.getPhone()) && user.getPhone() != "" && !AccountValidatorUtil.isMobile(user.getPhone())) {
            resAdd.setCode(0);
            resAdd.setError("手机号码格式错误");
            return false;
        }
        return true;
    }
}