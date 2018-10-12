package org.sms.project.blog.service.impl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Before;
import org.junit.Test;
import org.sms.SysConstants;
import org.sms.project.blog.entity.Blog;
import org.sms.project.blog.service.BlogService;
import org.sms.project.tag.entity.Tag;
import org.sms.project.user.entity.User;
import org.sms.project.util.FileUtil;

import base.BaseTest;

public class BlogServiceImplTest extends BaseTest {

    private BlogService blogService = (BlogService) aCtx.getBean("blogService");

    private static String template;

    private  static String htmlFileId = "0e2600aee51145c2bcd8ac0a6d93be7c";
    private  static String mdFileId = "a98596af7b1a45f18f6be7ef3194ce6e";


    @Before
    public void setUp() {
        final String file = "/Users/liuzhenxing01/work/github/SSserver/src/main/resources/config/template/blog.html";
        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get(file), StandardCharsets.UTF_8);
            StringBuilder sb = new StringBuilder();
            for (String line : lines) {
                sb.append(line + "\r\n");
            }
            template = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(template);
    }

    @Test
    public void insert() {

        Blog blog = new Blog();
        blog.setCreateDate(new Date());
        blog.setHtmlFileId(htmlFileId);
        blog.setMdFileId(mdFileId);
        blog.setUsableStatus(1);
        blog.setContent("本篇文章主要讲解MQTT单向和双向验证Java实现,在实现过程中也走了很多弯路，所以记录下来供以后查阅。");
        blog.setId(63L);
        blog.setTitle("MQTT(mosquitto)单向/双向验证(TLS)（Java版）");
        User user = new User();
        user.setId(1L);
        String tagStr = "mqtt,tls,双向验证,单向验证,java,mosquitto";
        blog.setCreateDate(new Date());
        blog.setReadNum(0L);
        Elements elements = getUploadHtml(blog, user);
        Document docTemplate = buildHtmlTemplate(blog, elements.toString());
        long id = blog.getId();
        List<String> tags = this.addTag(blog, tagStr, user, docTemplate);
//        this.addRecommended(blog,user,docTemplate);
        this.createBolgFile(blog, user, docTemplate);
    }

    private Document buildHtmlTemplate(Blog blog, String uploadHtmlStr) {
        String templateText = template;

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
                //                int tagId = tagService.insert(tag);
                //                BlogTag blogTag = new BlogTag();
                //                blogTag.setBlogId(blog.getId());
                //                blogTag.setTagId(tagId);
                //                blogTag.setCreateDate(new Date());
                //                long count = blogTagService.insert(blogTag);
                //                if (count >= 1) {
                //                    list.add(tag.getName());
                //                }
            }
        }
        return list;
    }

    private Elements getUploadHtml(Blog blog, User user) {
        String path = SysConstants.FILE_ABS_PATH + blog.getHtmlFileId() + ".html";
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

//    public void addRecommended(Blog blog, User user, Document docTemplate) {
//        long id = blog.getId();
//        List<Blog> blogs = blogDao.selectRec(id);
//        if (blogs == null || blogs.size() == 0) {
//            return;
//        }
//
//        if (blogs.size() == 1) {
//            Element recommended1 = docTemplate.getElementsByAttributeValue("id", "recommended1").first();
//            recommended1.attr("href", "https://blog.soaer.com/1/" + blogs.get(0).getHtmlFileId() + ".html");
//            recommended1.text(blogs.get(0).getTitle());
//            return;
//        }
//
//        Element recommended1 = docTemplate.getElementsByAttributeValue("id", "recommended1").first();
//        recommended1.attr("href", "https://blog.soaer.com/1/" + blogs.get(0).getHtmlFileId() + ".html");
//        recommended1.text(blogs.get(0).getTitle());
//
//        Element recommended2 = docTemplate.getElementsByAttributeValue("id", "recommended2").first();
//        recommended2.attr("href", "https://blog.soaer.com/1/" + blogs.get(1).getHtmlFileId() + ".html");
//        recommended2.text(blogs.get(1).getTitle());
//        return;
//    }

    private void createBolgFile(Blog blog, User user, Document docTemplate) {
        String blogPath = SysConstants.FILE_ABS_PATH + blog.getHtmlFileId() + "111111.html";
        Element element = docTemplate.head();
        // 增加静态的javaScript脚本
        Map<String, String> scriptMap = new HashMap<>();
        scriptMap.put("userName", user.getName());
        scriptMap.put("blogKey", blog.getHtmlFileId());
        scriptMap.put("blogId", String.valueOf(blog.getId()));
        element.append(insertScript(scriptMap));
        FileUtil.writeText(blogPath, docTemplate.toString());
    }

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
}
