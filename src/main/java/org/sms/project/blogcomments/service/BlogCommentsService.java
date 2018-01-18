package org.sms.project.blogcomments.service;

import org.sms.project.blogcomments.entity.BlogComments;

public interface BlogCommentsService {
    int deleteByPrimaryKey(String id);

    long insert(BlogComments record);

    int insertSelective(BlogComments record);

    BlogComments selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(BlogComments record);

    int updateByPrimaryKey(BlogComments record);
}