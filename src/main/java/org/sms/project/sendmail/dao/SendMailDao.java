package org.sms.project.sendmail.dao;

import org.apache.ibatis.session.SqlSession;
import org.sms.project.sendmail.entity.SendMail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


@Repository
public class SendMailDao {

	@Autowired
	private SqlSession sqlSession;

	public long insert(SendMail sendMail) {
		return sqlSession.insert(this.getClass().getName() + ".insert", sendMail);
	}
}
