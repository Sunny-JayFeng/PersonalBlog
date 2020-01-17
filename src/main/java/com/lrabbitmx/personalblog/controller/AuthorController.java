package com.lrabbitmx.personalblog.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lrabbitmx.personalblog.domain.Author;
import com.lrabbitmx.personalblog.domain.Blog;
import com.lrabbitmx.personalblog.domain.Book;
import com.lrabbitmx.personalblog.domain.Tag;
import com.lrabbitmx.personalblog.service.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
public class AuthorController extends BaseController {

    @Autowired
    private AuthorService authorService;
    @Autowired
    private BlogService blogService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private TagService tagService;
    @Autowired
    private BookService bookService;


    @RequestMapping(value = "/login", method = RequestMethod.POST)
    private void login(@Param("authorName") String authorName,
                       @Param("password") String password,
                       HttpServletRequest request,
                       HttpServletResponse response) {
        setEncoding(request, response);
        String result = authorService.login(authorName, password);
        util.setCookie(result, result.split("\\?")[1].split("=")[1], response);
        responseMessageToBrowser(response, result);
    }

    @RequestMapping(value = "/addAuthor", method = RequestMethod.POST)
    private void addAuthor(HttpServletRequest request,
                           HttpServletResponse response) {
        setEncoding(request, response);
        Author newAuthor = (Author) util.createAnObject(Author.class, request);
        responseMessageToBrowser(response, authorService.addAuthor(newAuthor));
    }

    @RequestMapping(value = "/deleteAuthor", method = RequestMethod.POST)
    private void deleteAuthor(@Param("authorName") String authorName,
                              @Param("password") String password,
                              HttpServletRequest request,
                              HttpServletResponse response) {
        setEncoding(request, response);
        int authorId = authorService.getAuthorId(authorName, password);
        if(authorId == -1) responseMessageToBrowser(response, "author name or password error");
        bufferMap.removeOneAuthor(authorId);
        List<Integer> blogIdList = bufferMap.getTheAuthorBlogsId(authorId); // 这个作者写过的所有文章的id集合
        bufferMap.removeTheAuthorBlogs(blogIdList, request, response); // 要删除这个作者，这个作者写的所有文章也都得删除
        responseMessageToBrowser(response, authorService.deleteAuthor(authorId));
    }

    @RequestMapping(value = "/addBlog", method = RequestMethod.POST)
    private void addBlog(@Param("authorId") Integer authorId,
                         HttpServletRequest request,
                         HttpServletResponse response) {
        setEncoding(request, response);
        setEncoding(request, response);
        Blog newBlog = (Blog) util.createAnObject(Blog.class, request);
        String realFileName = util.fileUpload(request);
        if("no new file".equals(realFileName)) responseMessageToBrowser(response, "必须上传文件");
        else if(realFileName == null) responseMessageToBrowser(response, "文件格式不正确");
        else {
            newBlog.setContentUrl(realFileName);
            responseMessageToBrowser(response, blogService.addBlog(newBlog, authorId, util.getTagsId(request.getParameter("blogTag").replaceAll("CAA", "C++"))));
        }
    }

    @RequestMapping(value = "/deleteBlog", method = RequestMethod.POST)
    public void deleteBlog(@Param("blogId") Integer blogId,
                            HttpServletRequest request,
                            HttpServletResponse response) {
        setEncoding(request, response);
        Blog theBlog = bufferMap.blogsBufferMap.get(blogId);
        String blogContent = theBlog == null ? blogService.getBlog(blogId).getContentUrl() : theBlog.getContentUrl();
        util.deleteFile(blogContent, "html");
        util.deleteFile(blogContent, "css");
        responseMessageToBrowser(response, util.deleteBlog(blogId));
    }

    @RequestMapping(value = "/updateBlog", method = RequestMethod.POST)
    private void updateBlog(@Param("blogId") Integer blogId,
                            HttpServletRequest request,
                            HttpServletResponse response) {
        setEncoding(request, response);
        Blog bufferBlog = bufferMap.blogsBufferMap.get(blogId);
        Blog oldBlog = bufferBlog == null ? blogService.getBlog(blogId) : bufferBlog;
        Blog newBlog = (Blog) util.createAnObject(Blog.class, request);
        Blog updateBlog = (Blog) util.updateObject(oldBlog, newBlog);
        String fileRealName = util.fileUpload(request);
        if(!"no new file".equals(fileRealName)) { // 证明需要更新博客文件
            String oldBlogContent = oldBlog.getContentUrl();
            JSONObject newBlogContent = JSON.parseObject(oldBlogContent);
            JSONObject fileNameJsonObject = JSON.parseObject(fileRealName);
            if(fileNameJsonObject.get("html") != null) { // 证明有新的html文件
                util.deleteFile(oldBlogContent, "html");
                newBlogContent.put("html", fileNameJsonObject.get("html"));
            }
            if(fileNameJsonObject.get("css") != null) { // 证明有新的css文件
                util.deleteFile(oldBlogContent, "css");
                newBlogContent.put("css", fileNameJsonObject.get("css"));
            }
            updateBlog.setContentUrl(newBlogContent.toJSONString());
        }
        bufferMap.updateBuffer(blogId, updateBlog);
        responseMessageToBrowser(response, blogService.updateBlog(updateBlog));
    }

    @RequestMapping(value = "/addTag", method = RequestMethod.POST)
    private void addTag(HttpServletRequest request,
                        HttpServletResponse response) {
        setEncoding(request, response);
        Tag newTag = (Tag) util.createAnObject(Tag.class, request);
        responseMessageToBrowser(response, tagService.addTag(newTag));
    }

    @RequestMapping(value = "/deleteTag", method = RequestMethod.POST)
    private void deleteTag(@Param("tagId") Integer tagId,
                           HttpServletRequest request,
                           HttpServletResponse response) {
        setEncoding(request, response);
        bufferMap.removeOneBufferTag(tagId); // 删除这个标签的缓存
        responseMessageToBrowser(response, tagService.deleteTag(tagId));
    }

    @RequestMapping(value = "/deleteComment", method = RequestMethod.POST)
    private void deleteComment(@Param("commentId") Integer commentId,
                               HttpServletRequest request,
                               HttpServletResponse response) {
        setEncoding(request, response);
        bufferMap.removeOneBufferComment(commentId); // 删除这个评论的缓存
        responseMessageToBrowser(response, commentService.deleteComment(commentId));
    }

    @RequestMapping(value = "/addBook", method = RequestMethod.POST)
    private void addBook(HttpServletRequest request,
                         HttpServletResponse response) {
        setEncoding(request, response);
        Book newBook = (Book) util.createAnObject(Book.class, request);
        String bookImgRealName = util.fileUpload(request);
        if("no new file".equals(bookImgRealName)) responseMessageToBrowser(response, "必须上传文件");
        else if(bookImgRealName == null) responseMessageToBrowser(response, "文件格式不正确");
        else {
            newBook.setBookImage(bookImgRealName);
            responseMessageToBrowser(response, bookService.addBook(newBook));
        }
    }

    @RequestMapping(value = "/deleteBook", method = RequestMethod.POST)
    private void deleteBook(@Param("bookId") Integer bookId,
                            HttpServletRequest request,
                            HttpServletResponse response) {
        setEncoding(request, response);
        System.out.println(bookId);
        util.deleteFile(bookService.getTheBook(bookId).getBookImage(), "img");
        responseMessageToBrowser(response, bookService.deleteBook(bookId));
    }

    @RequestMapping(value = "/updateBook", method = RequestMethod.POST)
    private void updateBook(@Param("bookId") Integer bookId,
                            HttpServletRequest request,
                            HttpServletResponse response) {
        setEncoding(request, response);
        Book oldBook = bookService.getTheBook(bookId);
        Book newBook = (Book) util.createAnObject(Book.class, request);
        Book updateBook = (Book) util.updateObject(oldBook, newBook);
        String bookImgRealName = util.fileUpload(request);
        if(!"no new file".equals(bookImgRealName)) { // 证明书籍图片需要更新
            updateBook.setBookImage(bookImgRealName);
            util.deleteFile(oldBook.getBookImage(), "img"); // 删除原来的旧文件
        }
        responseMessageToBrowser(response, bookService.updateBook(updateBook));
    }

}
