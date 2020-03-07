package com.lrabbitmx.personalblog.service;

import com.lrabbitmx.personalblog.dao.CommentDao;
import com.lrabbitmx.personalblog.domain.Comment;
import com.lrabbitmx.personalblog.util.BufferMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentDao commentDao;
    @Autowired
    private BufferMap bufferMap;

    public Comment getTheComment(int id) {
        return commentDao.selectTheCommentById(id);
    }

    public List<Integer> getTheBlogCommentsId(Integer blogId) {
        return commentDao.selectCommentsId(blogId);
    }

    @Transactional
    public String writeComment(Comment comment, int blogId) {
        if(commentDao.selectTheCommentByUserName(comment.getUserName()) != null) return "FAIL THIS userName is already exists";
        commentDao.insertComment(comment);
        commentDao.insertBlogCommentMapping(blogId, comment.getId());
        if(bufferMap.commentsBufferMap.get(blogId) != null) bufferMap.commentsBufferMap.get(blogId).put(comment.getId(), comment);
        return "SUCCESS";
    }

    @Transactional
    public void deleteComments(List<Integer> commentIdList) {
        for(int commentId : commentIdList) {
            commentDao.deleteComment(commentId);
        }
    }

    @Transactional
    public String deleteComment(int commentId) {
        commentDao.deleteComment(commentId);
        commentDao.deleteBlogComment(commentId);
        return "SUCCESS";
    }
}
