package org.sms.project.search.dao;

import org.apache.ibatis.session.SqlSession;
import org.sms.project.search.entity.Search;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class SearchDao {
	
	@Autowired
	private SqlSession sqlSession;

	public long insert(Search search) {
		return sqlSession.insert(this.getClass().getName() + ".insert", search);
	}
}
