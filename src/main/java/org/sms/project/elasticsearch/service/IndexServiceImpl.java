package org.sms.project.elasticsearch.service;

import java.util.List;
import java.util.Map;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.sms.project.elasticsearch.SearchService;
import org.sms.project.elasticsearch.client.SearchClient;
import org.sms.project.elasticsearch.option.GetOptions;
import org.sms.project.elasticsearch.option.IndexOptions;
import org.sms.project.elasticsearch.option.SearchOptions;

public enum IndexServiceImpl implements SearchService {

	NSTANCE;

	@Override
	public void get(String index, String type, String id, GetOptions options) {

	}

	@Override
	public SearchResponse search(List<String> indices, SearchOptions options) {
		SearchRequestBuilder builder = SearchClient.INSTANCE.getClient().prepareSearch(indices.toArray(new String[indices.size()]));
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
				options.getSorts().forEach(
						sort -> builder.addSort(sort.getField(),
								sort.getOrder()));
			}
			
			if (options.getHighlightBuilder() != null) {
				builder.highlighter(options.getHighlightBuilder());
			}
		}
		System.out.println(builder.toString());
		SearchResponse response = builder.get();
		return response;
	}

	@Override
	public void delete(String index, String type, String id) {

	}

	@Override
	public void delete(String index, String type, List<String> ids) {
	}

	@Override
	public IndexResponse createDocument(String index, String type, Map<String, Object> source, IndexOptions options) {
		IndexRequestBuilder builder = SearchClient.INSTANCE.getClient().prepareIndex(index, type).setSource(source);
		if (options != null) {
			if (options.getId() != null) {
				builder.setId(options.getId());
			}
			if (options.getOpType() != null) {
				builder.setOpType(options.getOpType());
			}
		}
		IndexResponse response = builder.get();
		return response;
	}
}