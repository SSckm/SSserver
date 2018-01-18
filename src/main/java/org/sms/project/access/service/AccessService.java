package org.sms.project.access.service;

import org.sms.project.access.entity.Access;

public interface AccessService {

    int deleteByPrimaryKey(String id);

    long insert(Access record);

    int insertSelective(Access record);

    Access selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Access record);

    int updateByPrimaryKey(Access record);
}
