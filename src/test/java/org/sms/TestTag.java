package org.sms;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sms.project.blog.entity.Blog;
import org.sms.project.util.HttpUtil;
import org.sms.project.util.JsonUtil;

import com.fasterxml.jackson.core.JsonProcessingException;

public class TestTag {
	
	public static boolean createIndex(Blog blog, List<String> tags) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> l = new ArrayList<Map<String, Object>>();
		for (String tag : tags) {
			Map<String, Object> t = new HashMap<String, Object>();
			t.put("name", tag);
			l.add(t);
		}
		map.put("id", blog.getId());
		map.put("title", blog.getTitle());
		map.put("tags", l);
		map.put("htmlFileId", blog.getHtmlFileId());
		map.put("content", blog.getContent());
		map.put("createDate", blog.getCreateDate());
		map.put("createUserId", blog.getCreateUserId());
		map.put("usableStatus", blog.getUsableStatus());
		map.put("token", "84ae986edef62809ffa97a2d40c36807");
		map.put("readNum", 0);
		try {
			String jsonStr = JsonUtil.ObjectToJson(map);
			System.out.println(jsonStr);
//			String response = HttpUtil.httpPost("http://blog.soaer.com/abi", jsonStr);
//			System.out.println(response);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return false;
		}
		return true;
//		IndexOptions options = new IndexOptions();
//		options.setId(blog.getId().toString());
//		IndexResponse response = IndexServiceImpl.NSTANCE.createDocument(INDEX, TYPE, map, options);
//		return response.getId() == blog.getId().toString();
	}
    
    public static void main(String[] args) {
    	
    	Blog blog = new Blog();
    	blog.setContent("asdfasdfasdfasdf");
    	blog.setCreateDate(new Date());
    	blog.setReadNum(0L);
    	blog.setCreateUserId(1L);
    	blog.setTitle("asdfasdfasdfas");
    	blog.setHtmlFileId("84ae986edef62809ffa97a2d40c36807");
    	blog.setUsableStatus(0);
    	blog.setId(90L);
    	List<String> tags = new ArrayList<>();
    	tags.add("java");
    	createIndex(blog, tags);
    }
}
