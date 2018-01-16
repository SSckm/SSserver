package org.sms.project.blog.entity;

import java.util.List;

import org.sms.project.tag.entity.Tag;

public class IndexBlog extends Blog {
	
	private List<Tag> tags;

	public List<Tag> getTags() {
		return tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}
}
