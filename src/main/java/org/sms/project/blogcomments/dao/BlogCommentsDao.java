package org.sms.project.blogcomments.dao;

import org.apache.ibatis.session.SqlSession;
import org.sms.project.blogcomments.entity.BlogComments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class BlogCommentsDao {

	@Autowired
	private SqlSession sqlSession;

	public long insert(BlogComments blogComments) {
		return sqlSession.insert(this.getClass().getName() + ".insert", blogComments);
	}
}
