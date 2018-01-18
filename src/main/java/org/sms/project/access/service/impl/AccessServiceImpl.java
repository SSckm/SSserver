package org.sms.project.access.service.impl;

import org.sms.project.access.dao.AccessDao;
import org.sms.project.access.entity.Access;
import org.sms.project.access.service.AccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service("accessService")
public class AccessServiceImpl implements AccessService {
	
	@Autowired
	private AccessDao accessDao;
	
	@Override
	public int deleteByPrimaryKey(String id) {
		return 0;
	}

	@Override
	public long insert(Access record) {
		return accessDao.insert(record);
	}

	@Override
	public int insertSelective(Access record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Access selectByPrimaryKey(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int updateByPrimaryKeySelective(Access record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateByPrimaryKey(Access record) {
		// TODO Auto-generated method stub
		return 0;
	}

}
