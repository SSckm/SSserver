package org.sms.project.search.service.impl;

import org.sms.project.search.dao.SearchDao;
import org.sms.project.search.entity.Search;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("searchService")
public class SearchServiceImpl implements org.sms.project.search.service.SearchService {

	@Autowired
	private SearchDao searchDao;
	
	@Override
	public int deleteByPrimaryKey(String id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long insert(Search record) {
		// TODO Auto-generated method stub
		return searchDao.insert(record);
	}

	@Override
	public int insertSelective(Search record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Search selectByPrimaryKey(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int updateByPrimaryKeySelective(Search record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateByPrimaryKey(Search record) {
		// TODO Auto-generated method stub
		return 0;
	}

}
