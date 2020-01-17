package com.lrabbitmx.personalblog.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lrabbitmx.personalblog.domain.Blog;
import com.lrabbitmx.personalblog.domain.Comment;
import com.lrabbitmx.personalblog.service.BlogService;
import com.lrabbitmx.personalblog.service.BookService;
import com.lrabbitmx.personalblog.service.CommentService;
import com.lrabbitmx.personalblog.service.TagService;
import com.lrabbitmx.personalblog.util.ProcessBlogMessage;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


@Controller
public class MainController extends BaseController {

    @Autowired
    private ProcessBlogMessage processBlogMessage;
    @Autowired
    private BlogService blogService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private BookService bookService;
    @Autowired
    private TagService tagService;

    @PostConstruct
    public void init() {
        bufferMap.init();  // 当服务器启动时，初始化所有缓存
    }

    // 总共有多少文章
    @RequestMapping(value = "/blogTotal", method = RequestMethod.POST)
    private void getBlogTotal(HttpServletRequest request,
                               HttpServletResponse response) {
        setEncoding(request, response);
        responseMessageToBrowser(response, blogService.blogTotal().toString());
    }

    // 5篇某一标签的文章
    @RequestMapping(value = "/tagBlogs", method = RequestMethod.POST)
    private void getBlogsWithTag(@Param("tagId") Integer tagId,
                                 @Param("tagName") String tagName,
                                 @Param("pageIndex") Integer pageIndex,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {
        setEncoding(request, response);
        if(tagId == null) tagId = bufferMap.getTagId(tagName);
        List<Blog> tagBlogs = new LinkedList<>();
        if(pageIndex > 20) tagBlogs = blogService.getFiveBlogs(tagService.getFiveBlogsId(tagId, pageIndex));
        else tagBlogs = bufferMap.getFiveTagBlogs(tagId, pageIndex);
        responseMessageToBrowser(response, processBlogMessage.concatBlogMessage(tagBlogs).toString());
    }

    // 某一标签共有多少文章
    @RequestMapping(value = "/blogTotalWithTag", method = RequestMethod.POST)
    private void getBlogTotalWithTag(@Param("tagId") Integer tagId,
                                     @Param("tagName") String tagName,
                                     HttpServletRequest request,
                                     HttpServletResponse response) {
        setEncoding(request, response);
        if(tagId == null) tagId = bufferMap.getTagId(tagName);
        responseMessageToBrowser(response, tagService.getTagBlogTotal(tagId).toString());
    }

    @RequestMapping(value = "/allBlogs", method = RequestMethod.POST)
    private void getAllBlogs(@Param("pageIndex") Integer pageIndex,
                             HttpServletRequest request,
                             HttpServletResponse response) {
        setEncoding(request, response);
        List<Blog> blogList = blogService.getFiveBlogs(pageIndex);
        responseMessageToBrowser(response, processBlogMessage.concatBlogMessage(blogList).toString());
    }

    @RequestMapping(value = "/showBlog", method = RequestMethod.GET)
    private void responseHtml(@Param("blogId") Integer blogId,
                              HttpServletRequest request,
                              HttpServletResponse response) throws IOException {
        setEncoding(request, response);
        String htmlContent = blogService.getBlog(blogId).getContentUrl();
        util.responseFileStream(response, htmlContent, "html", "no this blog");
    }

    @RequestMapping(value = "/css", method = RequestMethod.GET)
    private void getCssFile(@Param("blogId") Integer blogId,
                            HttpServletRequest request,
                            HttpServletResponse response) {
        setEncoding(request, response);
        response.setContentType("text/css;charset=utf-8");
        String cssContent = blogService.getBlog(blogId).getContentUrl();
        util.responseFileStream(response, cssContent, "css", "no css file");
    }

    @RequestMapping(value = "/getImg", method = RequestMethod.GET)
    private void getImg(@Param("bookId") Integer bookId,
                        HttpServletRequest request,
                        HttpServletResponse response) {
        setEncoding(request, response);
        String imgContent = bookService.getTheBook(bookId).getBookImage();
        util.responseFileStream(response, imgContent, "img", "no book image");
    }

    @RequestMapping(value = "/allTags", method = RequestMethod.POST)
    private void getAllTags(HttpServletRequest request,
                            HttpServletResponse response) {
        setEncoding(request, response);
        responseMessageToBrowser(response, JSON.toJSONString(bufferMap.tagsList));
    }

    @RequestMapping(value = "/hotBlogsTotal", method = RequestMethod.POST)
    private void getHotBlogsTotal(HttpServletRequest request,
                                  HttpServletResponse response) {
        setEncoding(request, response);
        responseMessageToBrowser(response, bufferMap.blogsList.size() + "");
    }

    @RequestMapping(value = "/hotBlogs", method = RequestMethod.POST)
    private void getAllHotBlogs(@Param("pageIndex") Integer pageIndex,
                                HttpServletRequest request,
                                HttpServletResponse response) {
        setEncoding(request, response);
        List<Blog> hotBlogs;
        if(pageIndex == null) hotBlogs = bufferMap.getFiveHotBlogs();
        else hotBlogs = bufferMap.getFiveHotBlogs(pageIndex);
        responseMessageToBrowser(response, processBlogMessage.concatBlogMessage(hotBlogs).toString());
    }

    @RequestMapping(value = "/allComments", method = RequestMethod.POST)
    private void getAllComments(@Param("blogId") Integer blogId,
                                HttpServletRequest request,
                                HttpServletResponse response) {
        setEncoding(request, response);
        Map<Integer, Comment> commentMap = bufferMap.commentsBufferMap.get(blogId);
        if(commentMap == null) commentMap = bufferMap.getCommentsMap(blogId);
        responseMessageToBrowser(response, JSONObject.toJSONString(util.mapToList(commentMap)));
    }

    @RequestMapping(value = "/writeComment", method = RequestMethod.POST)
    private void writeComment(@Param("blogId") Integer blogId,
                              HttpServletRequest request,
                              HttpServletResponse response) {
        setEncoding(request, response);
        Comment newComment = (Comment)util.createAnObject(Comment.class, request);
        responseMessageToBrowser(response, commentService.writeComment(newComment, blogId));
    }

    @RequestMapping(value = "/recommendBook", method = RequestMethod.POST)
    private void recommendBook(HttpServletRequest request,
                               HttpServletResponse response) {
        setEncoding(request, response);
        responseMessageToBrowser(response, JSONObject.toJSONString(bookService.getBooks()));
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    private void search(@Param("keyWord") String keyWord,
                        HttpServletRequest request,
                        HttpServletResponse response) {
        setEncoding(request, response);
        responseMessageToBrowser(response, processBlogMessage.concatBlogMessage(blogService.search(keyWord)).toString());
    }

}
