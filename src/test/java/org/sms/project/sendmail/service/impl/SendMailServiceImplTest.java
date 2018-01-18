package org.sms.project.sendmail.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Test;
import org.sms.SysConstants;
import org.sms.project.access.entity.Access;
import org.sms.project.access.service.AccessService;
import org.sms.project.blogcomments.entity.BlogComments;
import org.sms.project.blogcomments.service.BlogCommentsService;
import org.sms.project.search.entity.Search;
import org.sms.project.search.service.SearchService;
import org.sms.project.sendmail.entity.SendMail;
import org.sms.project.sendmail.service.SendMailService;

import base.BaseTest;

public class SendMailServiceImplTest extends BaseTest {

	private AccessService accessService = (AccessService) aCtx.getBean("accessService");
	private SearchService searchService = (SearchService) aCtx.getBean("searchService");
	private SendMailService sendMailService = (SendMailService) aCtx.getBean("sendMailService");
	private BlogCommentsService blogCommentsService = (BlogCommentsService) aCtx.getBean("blogCommentsService");

	public static String LOG_PATH = "E:\\log";

	public static void list(File file, List<String> list) {
		if (file.isDirectory())
		{
			File[] lists = file.listFiles();
			if (lists != null) {
				for (int i = 0; i < lists.length; i++) {
					list(lists[i], list);
				}
			}
		}
		list.add(file.getAbsolutePath());
	}
	
    public static String getText(String path) {
        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8);
            StringBuilder sb = new StringBuilder();
            for (String line : lines) {
                sb.append(line + "\r\n");
            }
            String fromFile = sb.toString();
            return fromFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

	@Test
	public void testInsert() throws UnsupportedEncodingException {
//		this.insertAccess();
//		this.insertSearch();
//		this.insertSendMail();
		this.insertComments();
	}

	private void insertComments() {
		String blogCommentsPath = LOG_PATH + File.separator + "blog.soaer.com" + File.separator + "comments";
		System.out.println("评论日志目录: " + blogCommentsPath);
		List<String> list = new ArrayList<>();
		list(new File(blogCommentsPath), list);
		for (int i = 0; i < list.size(); i ++) {
			if (list.get(i).equals(blogCommentsPath)) {
				continue;
			} else {
				String s = getText(list.get(i));
				parseComments(s);
			}
		}
	}

	private void parseComments(String s) {
		//a4a344b0493f4336aa110f6416793342	1515854282885 IP	lzxlxw@163.com	%E6%8C%89%E
		if (Objects.isNull(s)) {
			return;
		}
		System.out.println("============");
		String[] array = s.split("\r\n");
		for (int i = 0; i < array.length; i ++) {
			String re = array[i];
			String [] accessFiled = re.split("\t");
			if (accessFiled.length != 5) {
				continue;
			} else {
				BlogComments comments = new BlogComments();
				comments.setId(accessFiled[0]);
				comments.setCreateDate(new Date(Long.parseLong(accessFiled[1])));
				comments.setIp(accessFiled[2]);
				comments.setEmail(accessFiled[3]);
				String commentsContent = accessFiled[4];
				try {
					commentsContent = URLDecoder.decode(commentsContent, SysConstants.CHARSET);
					comments.setContent(commentsContent);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				System.out.println("============");
				long a = blogCommentsService.insert(comments);
				Assert.assertNotSame(0, a);
			}
		}
	}

	private void insertSendMail() {
		String blogEmailPath = LOG_PATH + File.separator + "blog.soaer.com" + File.separator + "email";
		System.out.println("发送邮件日志目录: " + blogEmailPath);
		//0f747b69e9e441e49f6d1afe42d91022	1515854031765	124.202.184.222	lzxlxw%40163.com
		List<String> list = new ArrayList<>();
		list(new File(blogEmailPath), list);
		for (int i = 0; i < list.size(); i ++) {
			if (list.get(i).equals(blogEmailPath)) {
				continue;
			} else {
				String s = getText(list.get(i));
				parseSendMail(s);
			}
		}
	}
	
	private void parseSendMail(String s) {
		if (Objects.isNull(s)) {
			return;
		}
		String[] array = s.split("\r\n");
		for (int i = 0; i < array.length; i ++) {
			String re = array[i];
			String [] accessFiled = re.split("\t");
			if (accessFiled.length != 4) {
				continue;
			} else {
				SendMail sendMail = new SendMail();
				sendMail.setId(accessFiled[0]);
				sendMail.setCreateDate(new Date(Long.parseLong(accessFiled[1])));
				sendMail.setIp(accessFiled[2]);
				sendMail.setEmail(accessFiled[3]);
				long a = sendMailService.insert(sendMail);
				Assert.assertNotSame(0, a);
			}
		}
	}

	public void parseSearch(String s) throws UnsupportedEncodingException {
		//a3fc3d7c2921496387433b78a3335f54	1515753268823	220.181.102.176	Openresty
		if (Objects.isNull(s)) {
			return;
		}
		String[] array = s.split("\r\n");
		for (int i = 0; i < array.length; i ++) {
			String re = array[i];
			String [] accessFiled = re.split("\t");
			if (accessFiled.length != 4) {
				continue;
			} else {
				Search search = new Search();
				search.setId(accessFiled[0]);
				search.setCreateDate(new Date(Long.parseLong(accessFiled[1])));
				search.setIp(accessFiled[2]);
				String keyword = accessFiled[3];
				keyword = URLDecoder.decode(keyword, SysConstants.CHARSET);
				search.setKeyword(keyword);
				long a = searchService.insert(search);
				Assert.assertNotSame(0, a);
			}
		}
	}

	private void insertSearch() throws UnsupportedEncodingException {
		String blogSearchPath = LOG_PATH + File.separator + "blog.soaer.com" + File.separator + "search";
		System.out.println("搜索关键字日志目录: " + blogSearchPath);
		List<String> list = new ArrayList<>();
		list(new File(blogSearchPath), list);
		for (int i = 0; i < list.size(); i ++) {
			if (list.get(i).equals(blogSearchPath)) {
				continue;
			} else {
				String s = getText(list.get(i));
				parseSearch(s);
			}
		}
	}
	
	public void parseAccess(String s) {
		//bea9c54f48c140e6baead63502fb278c	1516218873919	111.206.221.14	blog.soaer.com	1	38	d64a53028765441587bbcac432fd834f
		//ce97dcb510b34e8ab93e1e968dc11bba	1515905736405	124.202.184.222	soaer.com	/
		String[] array = s.split("\r\n");
		for (int i = 0; i < array.length; i ++) {
			String re = array[i];
			String [] accessFiled = re.split("\t");
			if (accessFiled.length != 5 && accessFiled.length != 7) {
				continue;
			} else {
				Access access = new Access();
				if (accessFiled.length == 5) {
					access.setId(accessFiled[0]);
					access.setCreateDate(new Date(Long.parseLong(accessFiled[1])));
					access.setIp(accessFiled[2]);
					access.setDomain(accessFiled[3]);
					access.setPath(accessFiled[4]);
				} else {
					access.setId(accessFiled[0]);
					access.setCreateDate(new Date(Long.parseLong(accessFiled[1])));
					access.setIp(accessFiled[2]);
					access.setDomain(accessFiled[3]);
					access.setUserId(Long.parseLong(accessFiled[4]));
					access.setBlogId(Long.parseLong(accessFiled[5]));
					access.setHtmlId(accessFiled[6]);
				}
				long a = accessService.insert(access);
				Assert.assertNotSame(0, a);
			}
		}
	}

	private void insertAccess() {
		String blogAccessIndexPath = LOG_PATH + File.separator + "blog.soaer.com" + File.separator + "index";
		String blogAccessContentPath = LOG_PATH + File.separator + "blog.soaer.com" + File.separator + "access";
		String domainAccessContentPath = LOG_PATH + File.separator + "soaer.com" + File.separator + "access";
		String toolAccesPath = LOG_PATH + File.separator + "tool.soaer.com" + File.separator + "access";
		System.out.println("博客首页日志目录: " + blogAccessIndexPath);
		System.out.println("博客内容日志目录: " + blogAccessContentPath);
		System.out.println("首站访问日志目录: " + domainAccessContentPath);
		System.out.println("工具类访问日志目录: " + toolAccesPath);
		
		List<String> list = new ArrayList<>();
		list(new File(blogAccessIndexPath), list);
		list(new File(blogAccessContentPath), list);
		list(new File(domainAccessContentPath), list);
		list(new File(toolAccesPath), list);
		for (int i = 0; i < list.size(); i ++) {
			if (list.get(i).equals(blogAccessIndexPath) ||list.get(i).equals(blogAccessContentPath) || list.get(i).equals(domainAccessContentPath) || list.get(i).equals(toolAccesPath)) {
				continue;
			} else {
				String s = getText(list.get(i));
				parseAccess(s);
			}
		}
	}
}
