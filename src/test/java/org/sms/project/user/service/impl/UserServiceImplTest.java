package org.sms.project.user.service.impl;

import org.junit.Assert;
import org.junit.Test;
import org.sms.project.user.entity.User;
import org.sms.project.user.service.UserService;
import base.BaseTest;


/**
 * @author Sunny
 */
public class UserServiceImplTest extends BaseTest {

    private UserService userService = (UserService) aCtx.getBean("userService");

    /**
     * Test method for
     * {@link org.sms.project.user.service.impl.UserServiceImpl#insert(org.sms.project.user.entity.User)}
     * .
     */
    // @Test
    // public void testInsert() {
    // User user = new User();
    // user.setEmail("lzxlxw@163.com");
    // user.setPassword("111111");
    // user.setName("Sunny");
    // user.setLoginSum(0);
    // user.setAddress("北京市海淀区上庄水库");
    // user.setLastLoginDate(new Date());
    // user.setCreateDate(new Date());
    // user.setLastLoginIp("192.168.1.1");
    // user.setUsableStatus(0);
    // user.setLoginType(0);
    // userService.insert(user);
    // System.out.println(user.getId());
    // System.out.println(user.getId());
    // }

    /**
     * Test method for
     * {@link org.sms.project.user.service.impl.UserServiceImpl#update(org.sms.project.user.entity.User)}
     * .
     */
    // @Test
    // public void testUpdate() {
    // User user = new User();
    // user.setImgPath("http://domain.com/persion.jpg");
    // user.setLastLoginIp("192.168.1.2");
    // user.setPhone("13166666666");
    // user.setName("Sunny. Liu");
    // user.setId(1L);
    // Date date = new Date();
    // user.setModifyDate(date);
    // int count = userService.update(user);
    // Assert.assertNotSame(0, count);
    // }
    //
    // // /**
    // // * Test method for
    // // * {@link org.sms.project.user.service.impl.UserServiceImpl#delete(long)}.
    // // */
    // @Test
    // public void testDelete() {
    // int i = userService.delete(2L);
    // System.out.println(i);
    // }
    //
    // //
    // // /**
    // // * Test method for
    // // * {@link
    // //
    // org.sms.project.user.service.impl.UserServiceImpl#queryByCondition(java.lang.String,
    // // java.lang.String, int, int)}
    // // * .
    // // */
    //
    @Test
    public void findById() {
        User user = userService.findUserByEmail("dddd");
        System.out.println(user.getEmail());
        Assert.assertNotNull(user);
    }
    //
    // // // @Test
    // // public void testQueryByCondition() {
    // // List<User> list = userService.queryByCondition(null, null, 1, 2);
    // // System.out.println(list);
    // // Assert.assertNotNull(list);
    // // }
    // //
    // // /**
    // // * Test method for
    // // * {@link
    // //
    // org.sms.project.user.service.impl.UserServiceImpl#findUserByLogin_Id(java.lang.String)}
    // // * .
    // // */
    // @Test
    // public void testFindUserByEmail() {
    // User user = userService.findUserByEmail("lzxlxw@163.com");
    // System.out.println(user.getEmail());
    // Assert.assertNotNull(user);
    // }
    //
    // @Test
    // public void testPage() {
    // Page page = new Page(1, 2);
    // List<User> users = userService.queryByCondition(page);
    // System.out.println(users.size() + "===========");
    // }
}