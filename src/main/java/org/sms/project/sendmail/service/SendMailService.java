package org.sms.project.sendmail.service;

import org.sms.project.sendmail.entity.SendMail;

public interface SendMailService {

    int deleteByPrimaryKey(String id);

    long insert(SendMail record);

    int insertSelective(SendMail record);

    SendMail selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(SendMail record);

    int updateByPrimaryKey(SendMail record);
}
