package org.sms.project.sendmail.service.impl;

import org.sms.project.sendmail.dao.SendMailDao;
import org.sms.project.sendmail.entity.SendMail;
import org.sms.project.sendmail.service.SendMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service("sendMailService")
public class SendMailServiceImpl implements SendMailService {

	@Autowired
	private SendMailDao sendMailDao;
	
	@Override
	public int deleteByPrimaryKey(String id) {
		return 0;
	}

	@Override
	public long insert(SendMail record) {
		return sendMailDao.insert(record);
	}

	@Override
	public int insertSelective(SendMail record) {
		return 0;
	}

	@Override
	public SendMail selectByPrimaryKey(String id) {
		return null;
	}

	@Override
	public int updateByPrimaryKeySelective(SendMail record) {
		return 0;
	}

	@Override
	public int updateByPrimaryKey(SendMail record) {
		return 0;
	}

}
