package org.sms.project.common;

import java.util.List;

import org.sms.project.blog.entity.Blog;
import org.sms.project.comments.entity.Comments;
import org.sms.project.tag.entity.Tag;

public class ResultList extends ResultAdd {

    private List<Tag> tags;
    
    private List<Blog> blogs;
    
    private List<Comments> comments;
    
    private Blog currentBlog;

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public List<Blog> getBlogs() {
        return blogs;
    }

    public void setBlogs(List<Blog> blogs) {
        this.blogs = blogs;
    }

    public List<Comments> getComments() {
        return comments;
    }

    public void setComments(List<Comments> comments) {
        this.comments = comments;
    }

    public Blog getCurrentBlog() {
        return currentBlog;
    }

    public void setCurrentBlog(Blog currentBlog) {
        this.currentBlog = currentBlog;
    }
}