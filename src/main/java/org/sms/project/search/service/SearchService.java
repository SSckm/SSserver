package org.sms.project.search.service;

import org.sms.project.search.entity.Search;

public interface SearchService {

    int deleteByPrimaryKey(String id);

    long insert(Search record);

    int insertSelective(Search record);

    Search selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Search record);

    int updateByPrimaryKey(Search record);
}
