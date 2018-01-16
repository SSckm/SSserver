package org.sms.project.elasticsearch;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.sms.project.elasticsearch.option.GetOptions;
import org.sms.project.elasticsearch.option.IndexOptions;
import org.sms.project.elasticsearch.option.SearchOptions;

/**
 * @author Sunny
 */
public interface SearchService {
	
	IndexResponse createDocument(String index, String type, Map<String, Object> source, IndexOptions options);

	default void get(String index, String type, String id) {
		get(index, type, id, new GetOptions());
	}

	void get(String index, String type, String id, GetOptions options);

	default SearchResponse search(String index) {
		return search(index, new SearchOptions());
	}

	default SearchResponse search(String index, SearchOptions options) {
		return search(Arrays.asList(index), options);
	}

	default SearchResponse search(List<String> indices) {
		return search(indices, new SearchOptions());
	}

	SearchResponse search(List<String> indices, SearchOptions options);

	void delete(String index, String type, String id);

	void delete(String index, String type, List<String> ids);
}