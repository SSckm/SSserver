package org.sms.project.comments.service;

import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.sms.project.comments.entity.Comments;
import org.sms.project.common.ResultAdd;
import org.sms.project.page.Page;

public interface CommentsService {

    ResultAdd insert(Comments comments, Cookie[] cookies);

    int update(Comments comments);

    int delete(long id);

    Comments findById(long id);

    ResultAdd validationEmail(String ticket, String ip, HttpServletResponse response);

    ResultAdd sendEmail(String emailAddress);

    List<Comments> queryByCondition(Page page, Long blogId);

    List<Comments> queryByCondition(Page page);
}
