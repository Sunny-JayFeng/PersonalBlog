package com.lrabbitmx.personalblog.dao;

import com.lrabbitmx.personalblog.domain.Comment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CommentDao {

    Comment selectTheCommentById(@Param("id") int id);

    Comment selectTheCommentByUserName(@Param("userName") String userName);

    List<Integer> selectCommentsId(@Param("blogId") int blogId);

    void insertComment(Comment comment);

    void insertBlogCommentMapping(@Param("blogId") int blogId, @Param("commentId") int commentId);

    void deleteComment(@Param("commentId") int commentId);

    void deleteBlogComment(@Param("commentId") int commentId);
}
