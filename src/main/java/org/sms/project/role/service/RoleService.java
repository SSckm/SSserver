package org.sms.project.role.service;

import java.util.List;

import org.sms.project.page.Page;
import org.sms.project.role.entity.Role;

/**
 * @author Sunny
 */
public interface RoleService {

    int insert(Role role);

    int update(Role role);

    int delete(long id);

    Role findById(long id);

    List<String> getRoleNameById(long id);

    int getCount();

    List<Role> queryByCondition(Page page);

    List<Role> queryByCondition(String query, String order, Page page);
}
