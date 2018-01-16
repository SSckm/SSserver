package org.sms.project.comments.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.sms.project.comments.entity.Comments;
import org.sms.project.page.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author Sunny
 */
@Repository
public class CommentsDao {

    @Autowired
    private SqlSession sqlSession;

    public int insert(Comments comments) {
        return sqlSession.insert(this.getClass().getName() + ".insert", comments);
    }

    public int update(Comments comments) {
        return sqlSession.update(this.getClass().getName() + ".updateByPrimaryKeySelective", comments);
    }

    public int delete(long id) {
        return sqlSession.update(this.getClass().getName() + ".deleteByPrimaryKey", id);
    }

    public Comments findById(Long id) {
        return sqlSession.selectOne(this.getClass().getName() + ".selectByPrimaryKey", id);
    }

    public List<Comments> queryByCondition(Page page, Long blogId) {
        Map<String, Object> map = new HashMap<>();
        map.put("startIndex", page.getStartIndex());
        map.put("pageSize", page.getPageSize());
        map.put("blogId", blogId);
        return sqlSession.selectList(this.getClass().getName() + ".selectByPage", map);
    }
}