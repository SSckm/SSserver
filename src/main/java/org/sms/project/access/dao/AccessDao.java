package org.sms.project.access.dao;

import org.apache.ibatis.session.SqlSession;
import org.sms.project.access.entity.Access;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AccessDao {
	@Autowired
	private SqlSession sqlSession;

	public long insert(Access access) {
		return sqlSession.insert(this.getClass().getName() + ".insert", access);
	}
}
