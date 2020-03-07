package com.lrabbitmx.personalblog.service;

import com.lrabbitmx.personalblog.dao.BlogDao;
import com.lrabbitmx.personalblog.domain.Blog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;

@Service
public class BlogService {

    @Autowired
    private BlogDao blogDao;

    public Blog getBlog(int blogId) {
        return blogDao.selectOneById(blogId);
    }

    public List<Blog> getBufferBlogs() {
        return blogDao.bufferBlogs();
    }

    public List<Blog> getFiveBlogs(int pageIndex) {
        return blogDao.selectFiveBlogs((pageIndex - 1) * 5);
    }

    public List<Blog> getFiveBlogs(List<Integer> blogIdList) {
        List<Blog> blogsList = new LinkedList<>();
        for(Integer blogId : blogIdList) {
            blogsList.add(getBlog(blogId));
        }
        return blogsList;
    }

    public List<Integer> getBlogsId(int tagId) {
        return blogDao.selectBlogsId(tagId);
    }

    public Integer blogTotal() {
        return blogDao.selectBlogTotal();
    }

    public List<Blog> search(String keyWord) {
        return blogDao.searchBlogs(keyWord);
    }

    @Transactional
    public String addBlog(Blog blog, Integer authorId, List<Integer> tagsId) {
        if(blogDao.selectOneByTitle(blog.getBlogTitle()) != null) return "FAIL";
        blogDao.insertBlog(blog);
        Integer blogId = blog.getId();
        blogDao.insertBlogAuthor(blogId, authorId);
        for(Integer tagId : tagsId) {
            blogDao.insertBlogTag(blogId, tagId);
        }
        return "SUCCESS";
    }

    @Transactional
    public String deleteBlog(int blogId) {
        blogDao.deleteBlog(blogId);
        blogDao.deleteBlogAuthor(blogId);
        blogDao.deleteBlogComment(blogId);
        blogDao.deleteBlogTag(blogId);

        return "SUCCESS";
    }

    public String updateBlog(Blog blog) {
        if(blog == null) return "FAIL";
        blogDao.updateBlog(blog);
        return "SUCCESS";
    }
}
