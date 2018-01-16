package org.sms.project.blog.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.sms.SysConstants;
import org.sms.core.id.UUIDFactory;
import org.sms.project.base.UploadFileBase;
import org.sms.project.blog.dao.BlogDao;
import org.sms.project.blog.entity.Blog;
import org.sms.project.blog.service.BlogService;
import org.sms.project.blogtag.entity.BlogTag;
import org.sms.project.blogtag.service.BlogTagService;
import org.sms.project.common.ResultAdd;
import org.sms.project.elasticsearch.option.SearchOptions;
import org.sms.project.elasticsearch.service.IndexServiceImpl;
import org.sms.project.filemanage.entity.FileManage;
import org.sms.project.filemanage.service.FileManageService;
import org.sms.project.init.SysConfig;
import org.sms.project.page.Page;
import org.sms.project.tag.entity.Tag;
import org.sms.project.tag.service.TagService;
import org.sms.project.user.entity.User;
import org.sms.project.util.FileUtil;
import org.sms.project.util.HttpUtil;
import org.sms.project.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;


@Service("blogService")
public class BlogServiceImpl implements BlogService {

	public static String INDEX = "soaer";
	public static String TYPE = "blog";

	@Autowired
	private BlogDao blogDao;

	@Autowired
	private TagService tagService;

	@Autowired
	private BlogTagService blogTagService;

	@Autowired
	private FileManageService fileManageService;

	public static String insertScript(Map<String, ?> map) {
		String scriptStart = "<script type=\"text/javascript\">";
		String scriptEnd = "</script>";
		StringBuffer buffer = new StringBuffer();
		buffer.append(scriptStart);
		buffer.append("\r\n");
		for (Map.Entry<String, ?> entry : map.entrySet()) {
			buffer.append("var " + entry.getKey() + "=\"" + entry.getValue()
					+ "\" ");
			buffer.append("\r\n");
		}
		buffer.append("\r\n");
		buffer.append(scriptEnd);
		return buffer.toString();
	}

	private boolean checkBlog(Blog blog) {
		if (Objects.isNull(blog)) {
			return false;
		}

		String htmlId = blog.getHtmlFileId();
		String mdId = blog.getMdFileId();
		if (Objects.isNull(htmlId) || Objects.isNull(mdId)) {
			return false;
		}

		if (blog.getCreateUserId() == null) {
			return false;
		}

		if (blog.getContent() == null) {
			return false;
		}

		if (htmlId.length() != 32 || mdId.length() != 32) {
			return false;
		}

		if (blog.getUsableStatus() != 0 && blog.getUsableStatus() != 1) {
			return false;
		}
		return true;
	}

	private Document buildHtmlTemplate(Blog blog, String uploadHtmlStr) {
		String templateText = SysConfig.INSTANCE
				.getCacheDate(SysConstants.BLOG_HTML_KEY);
		Document docBlog = Jsoup.parse(templateText);
		// 设置网页标题
		docBlog.title(blog.getTitle() + " - Sunny个人博客");
		// 设置创建日期
		Element createDataDiv = docBlog.getElementsByAttributeValue("id",
				"blogCreateDate").first();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(blog.getCreateDate());
		createDataDiv.append("创建日期：" + calendar.get(Calendar.YEAR) + "年"
				+ (calendar.get(Calendar.MONTH) + 1) + "月"
				+ calendar.get(Calendar.DAY_OF_MONTH) + "日");
		// 设置正文标题
		Element c = docBlog.getElementsByAttributeValue("id", "page-title")
				.first();
		Elements titleElement = c.getElementsByTag("h1");
		titleElement.append(blog.getTitle());
		// 设置Html模版的标签
		Element tagDiv = docBlog.getElementsByAttributeValue("class", "noteAd").first();
		tagDiv.append("<a href=\"javascript:;\" class=\"btn btn-circle red disabled\">原创</a>");
		tagDiv.append("<a href=\"javascript:;\" class=\"btn btn-circle green disabled\">标签</a>");
		
		
		String content = blog.getContent();
		if (!Objects.isNull(content)) {
			Element keywordHtml = docBlog.getElementsByAttributeValue("name", "keywords").first();
			Element descriptionHtml = docBlog.getElementsByAttributeValue("name", "description").first();
			if (content.length() >= 100) {
				String value = content.substring(0, 100);
				keywordHtml.attr("content", value);
				descriptionHtml.attr("content", value);
			} else {
				keywordHtml.attr("content", content);
				descriptionHtml.attr("content", content);
			}
		}
		// 添加上传Html的文本内容
		tagDiv.after(uploadHtmlStr);
		return docBlog;
	}

	private Elements getUploadHtml(Blog blog, User user) {
		String path = SysConstants.FILE_ABS_PATH + File.separator + "html"
				+ File.separator + user.getId() + File.separator
				+ blog.getHtmlFileId() + ".html";
		String htmlText = FileUtil.getText(path);
		if (Objects.isNull(htmlText)) {
			return null;
		}
		Document docSource = Jsoup.parse(htmlText);
		Elements sourceRealBody = docSource.getElementsByTag("body");
		Elements h1Title = docSource.getElementsByTag("h1");
		if (Objects.isNull(h1Title)) {
			h1Title.first().remove();
		}
		return sourceRealBody;
	}

	private List<String> addTag(Blog blog, String tagStr, User user,
			Document docTemplate) {
		Element tagDiv = docTemplate.getElementsByAttributeValue("class",
				"noteAd").first();
		List<String> list = new ArrayList<String>();
		if (!Objects.isNull(tagStr) || !"".equals(tagStr)) {
			String[] tags = tagStr.split(",");
			for (int i = 0; i < tags.length; i++) {
				tagDiv.append("<a href=\"" + "javascript:;" + "\" class=\"btn btn-circle green\">"
						+ tags[i] + "</a>");
				Tag tag = new Tag();
				tag.setCreateDate(new Date());
				tag.setName(tags[i]);
				tag.setCreateUserId(user.getId());
				tag.setUsableStatus(0);
				int tagId = tagService.insert(tag);
				BlogTag blogTag = new BlogTag();
				blogTag.setBlogId(blog.getId());
				blogTag.setTagId(tagId);
				blogTag.setCreateDate(new Date());
				long count = blogTagService.insert(blogTag);
				if (count >= 1) {
					list.add(tag.getName());
				}
			}
		}
		return list;
	}

	private void createBolgFile(Blog blog, User user, Document docTemplate) {
		File dir = new File(SysConstants.FILE_ABS_PATH + File.separator + "blog" + File.separator + user.getId());
		if (!dir.exists()) {
			dir.mkdirs();
		}
		String blogPath = dir.getAbsolutePath() + File.separator + blog.getHtmlFileId() + ".html";
		Element element = docTemplate.head();
		// 增加静态的javaScript脚本
		Map<String, String> scriptMap = new HashMap<>();
		scriptMap.put("userName", user.getName());
		scriptMap.put("blogKey", blog.getHtmlFileId());
		scriptMap.put("blogId", String.valueOf(blog.getId()));
		element.append(insertScript(scriptMap));
		FileUtil.writeText(blogPath, docTemplate.toString());
	}

	public boolean createIndex(Blog blog, List<String> tags) {
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
//		map.put("token", "84ae986edef62809ffa97a2d40c36807");
		map.put("readNum", 0);
		try {
			String jsonStr = JsonUtil.ObjectToJson(map);
			System.out.println(jsonStr);
			String response = HttpUtil.httpPost("http://127.0.0.1:9200/blog/blog/" + blog.getId(), jsonStr);
			System.out.println(response);
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

	public ResultAdd insert(Blog blog, User user, String tagStr) {
		ResultAdd resAdd = new ResultAdd();
		boolean isCheck = checkBlog(blog);
		if (!isCheck) {
			resAdd.setCode(0);
			resAdd.setError("提交数据错误");
			return resAdd;
		}

		blog.setCreateDate(new Date());
		blog.setReadNum(0L);
		Elements elements = getUploadHtml(blog, user);
		if (Objects.isNull(elements)) {
			resAdd.setCode(0);
			resAdd.setError("找不到所上传的Html文件");
			return resAdd;
		}
		Document docTemplate = buildHtmlTemplate(blog, elements.toString());
		long count = blogDao.insert(blog);
		if (count <= 0) {
			resAdd.setCode(0);
			resAdd.setError("服务器端错误");
			return resAdd;
		}

		long id = blog.getId();
		List<String> tags = this.addTag(blog, tagStr, user, docTemplate);
		this.addRecommended(blog,user,docTemplate);
		this.createBolgFile(blog, user, docTemplate);
		if (id <= 0) {
			resAdd.setCode(0);
			resAdd.setError("服务器端错误");
			return resAdd;
		}
		boolean create = this.createIndex(blog, tags);
		if (!create) {
			resAdd.setCode(0);
			resAdd.setError("创建索引失败！");
			return resAdd;
		}
		resAdd.setCode(1);
		resAdd.setError("添加成功");
		return resAdd;
	}

	public void addRecommended(Blog blog, User user, Document docTemplate) {
		long id = blog.getId();
		List<Blog> blogs = blogDao.selectRec(id);
		if (blogs == null || blogs.size() == 0) {
			return;
		}
		
		if (blogs.size() == 1) {
			Element recommended1 = docTemplate.getElementsByAttributeValue("id","recommended1").first();
			recommended1.attr("href", "http://blog.soaer.com/1/" + blogs.get(0).getHtmlFileId() + ".html");
			recommended1.text(blogs.get(0).getTitle());
			return;
		}
		
		Element recommended1 = docTemplate.getElementsByAttributeValue("id","recommended1").first();
		recommended1.attr("href", "http://blog.soaer.com/1/" + blogs.get(0).getHtmlFileId() + ".html");
		recommended1.text(blogs.get(0).getTitle());
		
		Element recommended2 = docTemplate.getElementsByAttributeValue("id","recommended2").first();
		recommended2.attr("href", "http://blog.soaer.com/1/" + blogs.get(1).getHtmlFileId() + ".html");
		recommended2.text(blogs.get(1).getTitle());
		return;
	}

	public int update(Blog blog) {
		return blogDao.update(blog);
	}

	public ResultAdd delete(long id) {
		ResultAdd add = new ResultAdd();
		Blog blog = blogDao.findById(id);
		String mdFileId = blog.getMdFileId();
		String htmlId = blog.getHtmlFileId();
		int count = blogDao.delete(id);
		if (count <= 0) {
			add.setCode(0);
			add.setError("服务器文件错误");
			return add;
		}
		File htmlFile = new File(SysConstants.FILE_ABS_PATH + "html/1/" + htmlId + ".html");
		File mdFile = new File(SysConstants.FILE_ABS_PATH + "md/1/" + mdFileId + ".md");
		File blogFile = new File(SysConstants.FILE_ABS_PATH + "blog/1/" + htmlId + ".md");
		if (htmlFile.isFile()) {//判断是否是文件  
			htmlFile.delete();//删除文件   
		}
		
		if (mdFile.isFile()) {//判断是否是文件  
			mdFile.delete();//删除文件   
		}
		
		if (blogFile.isFile()) {//判断是否是文件  
			blogFile.delete();//删除文件   
		}
		String res = HttpUtil.httpDelete("http://127.0.0.1:9200/blog/blog/" + id, null, null);
		System.out.println(res);
		add.setCode(1);
		return add;
	}

	public Blog findById(long id) {
		return blogDao.findById(id);
	}

	@Override
	public List<Blog> queryByCondition(String query, String order, Page page) {
		return blogDao.queryByCondition(query, order, page);
	}

	@Override
	public List<Blog> queryByCondition(Page page) {
		return this.queryByCondition(null, null, page);
	}

	@Override
	public int getCount() {
		return blogDao.getCount();
	}

	@Override
	public UploadFileBase addUpload(MultipartFile file, User user) {
		InputStream in = null;
		OutputStream out = null;
		UploadFileBase resAdd = new UploadFileBase();
		String sourceFileName = file.getOriginalFilename();
		String suffix = file.getOriginalFilename()
				.substring(file.getOriginalFilename().lastIndexOf(".") + 1)
				.toLowerCase();
		String id = UUIDFactory.INSTANCE.getUUID();
		try {
			File dir = new File(SysConstants.FILE_ABS_PATH + File.separator
					+ suffix + File.separator + user.getId());
			if (!dir.exists()) {
				dir.mkdirs();
			}
			File serverFile = new File(dir.getAbsolutePath() + File.separator
					+ id + "." + suffix);
			in = file.getInputStream();
			out = new FileOutputStream(serverFile);
			byte[] b = new byte[1024];
			int len = 0;
			while ((len = in.read(b)) > 0) {
				out.write(b, 0, len);
			}
			out.close();
			in.close();

			FileManage fileManage = new FileManage();
			fileManage.setCreateDate(new Date());
			fileManage.setFilePath(serverFile.getPath());
			fileManage.setId(id);
			fileManage.setFileType(suffix);
			int count = fileManageService.insert(fileManage);
			if (count == 0) {
				resAdd.setCode(0);
				resAdd.setError("服务器文件错误");
				return resAdd;
			}
			resAdd.setCode(1);
			resAdd.setUrl("http://soaer.com/" + id + "." + suffix);
			resAdd.setName(sourceFileName);
			resAdd.setError("添加成功");
			resAdd.setId(id);
			resAdd.setType(suffix.toLowerCase().trim());
			List<UploadFileBase> res = new ArrayList<UploadFileBase>();
			res.add(resAdd);
			return resAdd;
		} catch (Exception e) {
			e.printStackTrace();
			resAdd.setCode(0);
			resAdd.setError("服务器文件错误");
			return resAdd;
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				out = null;
			}

			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				in = null;
			}
		}
	}

	@Override
	public String search(Page page, String keyWord) {
		BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
		SearchOptions options = new SearchOptions();
		List<String> list = new ArrayList<String>();
		list.add(INDEX);
		options.setFrom((page.getPageNumber() - 1) * page.getPageSize());
		options.setSize(page.getPageSize());
		options.setSearchType(SearchType.DEFAULT);
		String[] muilt = new String[3];
		muilt[0] = "title";
		muilt[1] = "content";
		muilt[2] = "tags.name";
		QueryBuilder multiMatchQuery = QueryBuilders.multiMatchQuery(keyWord, muilt).analyzer("ik_smart");
        QueryBuilder regx = QueryBuilders.regexpQuery("title", "~*" + keyWord + "~*");
        QueryBuilder contentRegx = QueryBuilders.regexpQuery("content", "~*" + keyWord + "~*");
        queryBuilder.should(multiMatchQuery);
        queryBuilder.should(regx);
        queryBuilder.should(contentRegx);
        queryBuilder.minimumShouldMatch(1);
		QueryBuilder filters = QueryBuilders.termQuery("usableStatus", 0);
		queryBuilder.filter(filters);
		options.setBuilder(queryBuilder);
		options.addType("blog");
		HighlightBuilder highlightBuilder = new HighlightBuilder();
		String startTags = "<font color=\"#FF0000\">";
		String endTags = "</font>";
		highlightBuilder.preTags(startTags);
		highlightBuilder.postTags(endTags);
		highlightBuilder.field("title");
		highlightBuilder.field("content");
		highlightBuilder.field("tags.name");
		options.setHighlightBuilder(highlightBuilder);
		SearchResponse response = IndexServiceImpl.NSTANCE.search(list, options);
		return this.parse(response);
	}

	private String parse(SearchResponse response) {
		SearchHits hits = response.getHits();
        Map<String, Object> res = new HashMap<String, Object>();
        res.put("total", hits.totalHits);
        List<Map<String, Object>> lists = new ArrayList<Map<String,Object>>();
		for (SearchHit hit : hits.getHits()) {
			Map<String, Object> map = hit.getSourceAsMap();
			Map<String, HighlightField> h = hit.getHighlightFields();
			Set<Entry<String, HighlightField>> hlist = h.entrySet();
			for (Entry<String, HighlightField> filed : hlist) {
				String key = filed.getKey();
				HighlightField f = filed.getValue();
				Text[] text = f.getFragments();
				String hValue = text[0].toString();
				if ("content".equals(key)) {
				    map.put(key, hValue + map.get(key).toString());
				} else if("tags.name".equals(key)) {
				    @SuppressWarnings("unchecked")
                    List<Map<String, String>> n = (List<Map<String, String>>) map.get("tags");
                    if (!Objects.isNull(n) && n.size() >= 1) {
                        for (int k = 0; k < n.size(); k ++) {
                            if (hValue.contains(n.get(k).get("name"))) {
                                n.get(k).put("name", "<font color=\"#FF0000\">" + n.get(k).get("name") + "</font>");
                            }
                        }
                    }
                    System.out.println(n);
				} else {
				    map.put(key, text[0].toString());
				}
			}
			lists.add(map);
		}
		res.put("list", lists);
		res.put("code", 1);
		try {
			return JsonUtil.ObjectToJson(res);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
		
	}
}