package org.sms.project.blog.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.sms.project.blog.entity.Blog;
import org.sms.project.blog.entity.SearchEntity;
import org.sms.project.page.Page;
import org.sms.project.tag.entity.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author Sunny
 */
@Repository
public class BlogDao {

    @Autowired
    private SqlSession sqlSession;

    public long insert(Blog blog) {
        return sqlSession.insert(this.getClass().getName() + ".insert", blog);
    }

    public int update(Blog blog) {
        return sqlSession.update(this.getClass().getName() + ".updateByPrimaryKeySelective", blog);
    }

    public int delete(long id) {
        return sqlSession.update(this.getClass().getName() + ".deleteByPrimaryKey", id);
    }

    public Blog findById(Long id) {
        return sqlSession.selectOne(this.getClass().getName() + ".selectByPrimaryKey", id);
    }

    public int getCount() {
        return sqlSession.selectOne(this.getClass().getName() + ".selectCount");
    }

    public List<Blog> queryByCondition(String query, String order, Page page) {
        return sqlSession.selectList(this.getClass().getName() + ".selectByPage", page);
    }

    public List<Tag> getTags(Long blogId) {
        return sqlSession.selectList(this.getClass().getName() + ".getTagsByBlogId", blogId);
    }
    
    public void updateReadNum(Long id) {
        sqlSession.update(this.getClass().getName() + ".updateReadNum", id);
    }

    public List<Blog> search(SearchEntity entity) {
        return sqlSession.selectList(this.getClass().getName() + ".selectByKeyWord", entity);
    }
    
    public List<Blog> selectRec(Long id) {
        return sqlSession.selectList(this.getClass().getName() + ".selectRec", id);
    }
}