package org.sms;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.sms.project.blog.entity.IndexBlog;
import org.sms.project.elasticsearch.option.SearchOptions;
import org.sms.project.elasticsearch.option.SortOption;
import org.sms.project.tag.entity.Tag;
import org.sms.project.util.JsonUtil;

import com.fasterxml.jackson.core.JsonProcessingException;

public class Test {

    public static void testInsert() throws UnknownHostException {
        Settings settings = Settings.builder().put("client.transport.sniff", false).put("transport.tcp.compress", true)
                .build();
        TransportClient client = new PreBuiltTransportClient(settings);
        client.addTransportAddress(new TransportAddress(InetAddress.getByName("10.234.138.64"), 9300));
        IndexBlog blog = new IndexBlog();
        blog.setContent(
                "在 2012 年的时候，加入到奇虎 360 公司，为新的产品做技术选型。由于之前一直混迹在 Python 圈子里面，也接触过 Nginx C 模块的高性能开发，一直想找到一个兼备 Python 快速开发和 Nginx C 模块高性能的产品。看到 OpenResty 后，有发现新大陆的感觉。\n"
                        + "\n"
                        + "于是在新产品里面力推 OpenResty，团队里面几乎没人支持，经过几轮性能测试，虽然轻松击败所有的其他方案，但是其他开发人员并不愿意参与到基于 OpenResty 这个“陌生”框架的开发中来。于是我开始了一个人的 OpenResty 之旅，刚开始经历了各种技术挑战，庆幸有详细的文档，以及春哥和邮件列表里面热情的帮助，成了团队里面 bug 最少和几乎不用加班的同学。");
        blog.setCreateDate(new Date());
        Tag tag = new Tag();
        tag.setName("java");
        List<Tag> tags = new ArrayList<Tag>();
        tags.add(tag);

        Tag tag1 = new Tag();
        tag1.setName("nginxadsfasfasd");
        tags.add(tag1);

        Tag tag2 = new Tag();
        tag2.setName("elasticsearch");
        tags.add(tag2);

        blog.setCreateUserId(1L);
        blog.setTags(tags);
        blog.setUsableStatus(0);
        blog.setId(1000L);
        blog.setTitle("OpenResty入门实战");
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, String> map1 = new HashMap<String, String>();
        map1.put("name", "openresty");

        Map<String, String> map2 = new HashMap<String, String>();
        map2.put("name", "入门");

        Map<String, String> map3 = new HashMap<String, String>();
        map3.put("name", "nginxadsfasdf");

        List<Map> l = new ArrayList<Map>();
        l.add(map1);
        l.add(map2);
        l.add(map3);
        map.put("id", blog.getId());
        map.put("title", blog.getTitle());
        map.put("tags", l);
        map.put("usableStatus", blog.getUsableStatus());
        map.put("createUserId", blog.getCreateUserId());
        map.put("htmlFileId", "d98duj88766dsa77232242ds");
        map.put("content", blog.getContent());
        map.put("createDate", blog.getCreateDate());
        IndexRequestBuilder builder = client.prepareIndex("blog", "blog").setSource(map);
        System.out.println(builder.toString());
        IndexResponse response = builder.get();
        System.out.println(response.getId());
        System.out.println(response.getIndex());
        System.out.println(response.getType());
        System.out.println(response.getVersion()); 
    }

    public static void testSearch() throws UnknownHostException, JsonProcessingException {
        Settings settings = Settings.builder().put("client.transport.sniff", false).put("transport.tcp.compress", true)
                .build();
        TransportClient client = new PreBuiltTransportClient(settings);
        client.addTransportAddress(new TransportAddress(InetAddress.getByName("10.234.138.64"), 9300));

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        SearchOptions options = new SearchOptions();
        List<String> list = new ArrayList<String>();
        list.add("blog");
        options.setFrom(0);
        options.setSize(1);
        options.setSearchType(SearchType.DEFAULT);
        String[] muilt = new String[3];
        muilt[0] = "title";
        muilt[1] = "content";
        muilt[2] = "tags.name";

        String keyword = "入门";
        QueryBuilder multiMatchQuery = QueryBuilders.multiMatchQuery(keyword, muilt).analyzer("ik_max_word");
        QueryBuilder regx = QueryBuilders.regexpQuery("title", "~*" + keyword + "~*");
        QueryBuilder contentRegx = QueryBuilders.regexpQuery("content", "~*" + keyword + "~*");

        queryBuilder.should(multiMatchQuery);
        queryBuilder.should(regx);
        queryBuilder.should(contentRegx);
        queryBuilder.minimumShouldMatch(1);

        QueryBuilder filters = QueryBuilders.termQuery("usableStatus", 0);
        queryBuilder.filter(filters);
        options.setBuilder(queryBuilder);
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        String startTags = "<strong>";
        String endTags = "</strong>";
        highlightBuilder.preTags(startTags);
        highlightBuilder.postTags(endTags);
        highlightBuilder.field("title");
        highlightBuilder.field("content");
        highlightBuilder.field("tags.name");
        highlightBuilder.fragmentSize(1000);
        highlightBuilder.numOfFragments(20);

        options.setHighlightBuilder(highlightBuilder);

        SearchRequestBuilder builder = client.prepareSearch(list.toArray(new String[list.size()]));
        TimeValue timeValue = TimeValue.timeValueSeconds(30);
        builder.setTimeout(timeValue);
        if (options != null) {
            if (!options.getTypes().isEmpty()) {
                builder.setTypes(options.getTypes().toArray(new String[options.getTypes().size()]));
            }
            if (options.getSearchType() != null) {
                builder.setSearchType(options.getSearchType());
            }
            if (options.getSize() != null) {
                builder.setSize(options.getSize());
            }
            if (options.getFrom() != null) {
                builder.setFrom(options.getFrom());
            }
            if (options.getBuilder() != null) {
                builder.setQuery(options.getBuilder());
            }
            if (!options.getSorts().isEmpty()) {
                options.getSorts().forEach(sort -> builder.addSort(sort.getField(), sort.getOrder()));
            }

            if (options.getHighlightBuilder() != null) {
                builder.highlighter(options.getHighlightBuilder());
            }
        }
        SearchResponse response = builder.get();
        System.out.println(response.toString());
        System.out.println(builder.toString());
        SearchHits hits = response.getHits();
        Map<String, Object> kkk = new HashMap<String, Object>();
        kkk.put("total", hits.totalHits);
        List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();

        for (SearchHit hit : hits.getHits()) {
            Map<String, Object> map = hit.getSourceAsMap();
            Map<String, HighlightField> h = hit.getHighlightFields();
            Set<Entry<String, HighlightField>> bb = h.entrySet();
            for (Entry<String, HighlightField> str : bb) {
                String key = str.getKey();
                HighlightField f = str.getValue();
                Text[] text = f.getFragments();

                String hValue = text[0].toString();
                if ("content".equals(key)) {
                    map.put(key, hValue + map.get(key).toString());
                } else if ("tags.name".equals(key)) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, String>> n = (List<Map<String, String>>) map.get("tags");
                    if (!Objects.isNull(n) && n.size() >= 1) {
                        for (int k = 0; k < n.size(); k ++) {
                            if (hValue.contains(n.get(k).get("name"))) {
                                n.get(k).put("name", "<strong>" + n.get(k).get("name") + "</strong>");
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
        kkk.put("blogs", lists);
        System.out.println(JsonUtil.ObjectToJson(kkk));
        client.close();
    }

    public static void testLastSearch() throws UnknownHostException, JsonProcessingException {
        Settings settings = Settings.builder().put("client.transport.sniff", false).put("transport.tcp.compress", true).build();
        TransportClient client = new PreBuiltTransportClient(settings);
        client.addTransportAddress(new TransportAddress(InetAddress.getByName("10.234.138.64"), 9300));
        
        
        SearchOptions options = new SearchOptions();
        List<String> list = new ArrayList<String>();
        list.add("blog");
        options.setFrom(0);
        options.setSize(10);
        options.setSearchType(SearchType.DEFAULT);
        options.addSort("createDate", SortOrder.DESC);
        options.addType("blog");
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        QueryBuilder filters = QueryBuilders.termQuery("usableStatus", 0);
        QueryBuilder blogFilter = QueryBuilders.termQuery("blogId", 0);
        queryBuilder.filter(filters);
        queryBuilder.filter(blogFilter);
        options.setBuilder(queryBuilder);
        SearchRequestBuilder builder = client.prepareSearch(list.toArray(new String[list.size()]));
        TimeValue timeValue = TimeValue.timeValueSeconds(30);
        builder.setTimeout(timeValue);
        if (options != null) {
            if (!options.getTypes().isEmpty()) {
                builder.setTypes(options.getTypes().toArray(new String[options.getTypes().size()]));
            }
            if (options.getSearchType() != null) {
                builder.setSearchType(options.getSearchType());
            }
            if (options.getBuilder() != null) {
                builder.setQuery(options.getBuilder());
            }
            if (options.getSize() != null) {
                builder.setSize(options.getSize());
            }
            if (options.getFrom() != null) {
                builder.setFrom(options.getFrom());
            }
            
            if (!options.getSorts().isEmpty()) {
                options.getSorts().forEach(sort -> builder.addSort(sort.getField(), sort.getOrder()));
            }
        }
        
        SearchResponse response = builder.get();
        System.out.println(builder.toString());
        System.out.println(response.toString());
        SearchHits hits = response.getHits();
        Map<String, Object> kkk = new HashMap<String, Object>();
        kkk.put("total", hits.totalHits);
        List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();

        for (SearchHit hit : hits.getHits()) {
            Map<String, Object> map = hit.getSourceAsMap();
            Map<String, HighlightField> h = hit.getHighlightFields();
            Set<Entry<String, HighlightField>> bb = h.entrySet();
            for (Entry<String, HighlightField> str : bb) {
                String key = str.getKey();
                HighlightField f = str.getValue();
                Text[] text = f.getFragments();

                String hValue = text[0].toString();
                if ("content".equals(key)) {
                    map.put(key, hValue + map.get(key).toString());
                } else if ("tags.name".equals(key)) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, String>> n = (List<Map<String, String>>) map.get("tags");
                    if (!Objects.isNull(n) && n.size() >= 1) {
                        for (int k = 0; k < n.size(); k ++) {
                            if (hValue.contains(n.get(k).get("name"))) {
                                n.get(k).put("name", "<strong>" + n.get(k).get("name") + "</strong>");
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
        kkk.put("blogs", lists);
        System.out.println(JsonUtil.ObjectToJson(kkk));
        client.close();
    }

    public static void main(String[] args) throws UnknownHostException, JsonProcessingException {
//         testInsert();
        testSearch();
//    	testLastSearch();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
