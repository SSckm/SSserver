package org.sms.project.blog.service;

import java.util.List;

import org.jsoup.nodes.Document;
import org.sms.project.base.UploadFileBase;
import org.sms.project.blog.entity.Blog;
import org.sms.project.common.ResultAdd;
import org.sms.project.page.Page;
import org.sms.project.user.entity.User;
import org.springframework.web.multipart.MultipartFile;

public interface BlogService {

	ResultAdd insert(Blog blog, User user, String tags);

	int update(Blog blog);

	ResultAdd delete(long id);

	Blog findById(long id);

	int getCount();

	List<Blog> queryByCondition(Page page);

	String search(Page page, String keyWord);

	List<Blog> queryByCondition(String query, String order, Page page);

	UploadFileBase addUpload(MultipartFile file, User user);
	
	void addRecommended(Blog blog, User user, Document docTemplate);
}
