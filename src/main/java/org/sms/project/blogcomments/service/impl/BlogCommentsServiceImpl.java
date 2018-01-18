package org.sms.project.blogcomments.service.impl;

import org.sms.project.blogcomments.dao.BlogCommentsDao;
import org.sms.project.blogcomments.entity.BlogComments;
import org.sms.project.blogcomments.service.BlogCommentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("blogCommentsService")
public class BlogCommentsServiceImpl implements BlogCommentsService {
	
	@Autowired
	private BlogCommentsDao blogCommentsDao;

	@Override
	public int deleteByPrimaryKey(String id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long insert(BlogComments record) {
		// TODO Auto-generated method stub
		return blogCommentsDao.insert(record);
	}

	@Override
	public int insertSelective(BlogComments record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public BlogComments selectByPrimaryKey(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int updateByPrimaryKeySelective(BlogComments record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateByPrimaryKey(BlogComments record) {
		// TODO Auto-generated method stub
		return 0;
	}

}
