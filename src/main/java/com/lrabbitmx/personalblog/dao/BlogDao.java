package com.lrabbitmx.personalblog.dao;

import com.lrabbitmx.personalblog.domain.Blog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BlogDao {

    List<Blog> selectFiveBlogs(@Param("begin") int begin);

    int selectBlogTotal();

    List<Integer> selectBlogsId(@Param("tagId") int tagId);

    List<Blog> bufferBlogs();

    List<Blog> searchBlogs(@Param("keyWord")  String keyWord);

    Blog selectOneById(@Param("blogId") int blogId);

    Blog selectOneByTitle(@Param("blogTitle") String blogTitle);


    void insertBlog(Blog blog);

    void insertBlogAuthor(@Param("blogId") int blogId, @Param("authorId") int authorId);

    void insertBlogTag(@Param("blogId") int blogId, @Param("tagId") int tagId);

    void deleteBlog(@Param("blogId") int blogId);

    void deleteBlogAuthor(@Param("blogId") int blogId);

    void deleteBlogComment(@Param("blogId") int blogId);

    void deleteBlogTag(@Param("blogId") int blogId);

    void updateBlog(Blog blog);

}
