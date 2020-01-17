package com.lrabbitmx.personalblog.util;

import com.lrabbitmx.personalblog.domain.Author;
import com.lrabbitmx.personalblog.domain.Blog;
import com.lrabbitmx.personalblog.domain.Comment;
import com.lrabbitmx.personalblog.domain.Tag;
import com.lrabbitmx.personalblog.service.AuthorService;
import com.lrabbitmx.personalblog.service.BlogService;
import com.lrabbitmx.personalblog.service.CommentService;
import com.lrabbitmx.personalblog.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class BufferMap {

    @Autowired
    private BlogService blogService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private TagService tagService;
    @Autowired
    private AuthorService authorService;
    @Autowired
    private Util util;

    public static List<Tag> tagsList;  // 缓存标签集合
    public static List<Blog> blogsList; // 缓存热点文章，有序
    public static Map<Integer, Author> authorMap; // 缓存作者集合
    public static Map<Integer, Blog> blogsBufferMap; // 缓存热点文章，无序
    public static Map<Integer, Tag> tagsMap; // 缓存标签
    public static Map<Integer, Integer> blogAuthorMap; // 文章id和作者id对应集合
    public static Map<Integer, List<Tag>> blogTagsMap; // 文章id和文章带有的标签的集合
    public static Map<Integer, Map<Integer, Blog>> tagBlogsBufferMap; // 每个标签缓存 100 篇文章
    public static Map<Integer, Map<Integer, Comment>> commentsBufferMap; // 热点文章的评论缓存

    // 博客模板
    public static final String blogTemplateWithoutCssFile = getBlogTemplateWithoutCss();
    public static final String blogTemplateWithCssFile = getBlogTemplateWithCss();

    // 读取博客模板文件
    private static String getBlogTemplate(String filePath) {
        StringBuilder builder = new StringBuilder();
        File file = new File(filePath);
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        try {
            fileReader = new FileReader(file);
            bufferedReader = new BufferedReader(fileReader);
            String result = bufferedReader.readLine();
            while(result != null) {
                builder.append(result.trim());
                result = bufferedReader.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        return builder.toString();
    }

    // 需要加载css文件
    private static String getBlogTemplateWithoutCss() {
        return getBlogTemplate("E:/personalblogfileupload/html/blogbasewithoutcssfile");
    }

    // 不需要加载css文件
    private static String getBlogTemplateWithCss() {
        return getBlogTemplate("E:/personalblogfileupload/html/blogbasewithcssfile");
    }

    // 到数据库中查出所有作者
    private List<Author> getAuthorList() {
        return authorService.getAuthorList();
    }

    // 到数据库中查出热点文章
    private List<Blog> getBufferBlogs() {
        blogsList = blogService.getBufferBlogs();
        return blogsList;
    }

    // 到数据库中查出所有的标签
    private List<Tag> getTagList() {
        return tagService.getAllTags();
    }

    // 到数据库中查出标签信息
    public List<Tag> getTagList(Integer blogId) {
        List<Tag> tagList = new LinkedList<>();
        for(Integer tagId : getTheBlogTagsId(blogId)) {
            tagList.add(tagService.getTheTag(tagId));
        }
        return tagList;
    }

    // 到数据库中查出作者的信息
    public Author getAuthor(Integer blogId) {
        Integer authorId = authorService.getAuthorId(blogId);
        return authorMap.get(authorId);
    }

    // 从标签集合中查出标签名对应的id
    public Integer getTagId(String tagName) {
        Iterator<Integer> iterator = tagsMap.keySet().iterator();
        while(iterator.hasNext()) {
            Integer tagId = iterator.next();
            if(tagName.equals(tagsMap.get(tagId).getTagName())) return tagId;
        }
        return null;
    }

    // 到数据库中查出某篇文章带有的所有的标签的id
    public List<Integer> getTheBlogTagsId(Integer blogId) {
        return tagService.getTheBlogTagsId(blogId);
    }

    // 到数据库中查出100篇带这个标签的文章的id
    private List<Integer> getBlogsIdList(Integer tagId) {
        return blogService.getBlogsId(tagId);
    }

    // 到数据库中查出某篇文章带有的所有评论的id
    private List<Integer> getTheBlogCommentsId(Integer blogId) {
        return commentService.getTheBlogCommentsId(blogId);
    }

    // 到数据库中查出一条评论
    private Comment getTheComment(Integer commentId) {
        return commentService.getTheComment(commentId);
    }

    // 初始化缓存集合
    public void init() {
        Lock lock = new ReentrantLock();
        lock.lock();
        if(authorMap == null) this.initAuthorMap(getAuthorList());
        if(tagsMap == null) this.initTagsMap(getTagList());
        if(blogsBufferMap == null) this.initBlogsBufferMap(getBufferBlogs());
        if(blogAuthorMap == null) this.initBlogAuthorMap();
        if(blogTagsMap == null) this.initBlogTagsMap();
        if(commentsBufferMap == null) this.initCommentsBufferMap();
        if(tagBlogsBufferMap == null) this.initTagBlogsBufferMap();
        lock.unlock();
    }

    // 刷新缓存。利用定时器，晚间刷新
    public void flushBufferMap() {
        Lock lock = new ReentrantLock();
        lock.lock();
        initAuthorMap(getAuthorList());
        initTagsMap(getTagList());
        initBlogsBufferMap(getBufferBlogs());
        initBlogAuthorMap();
        initBlogTagsMap();
        initCommentsBufferMap();
        initTagBlogsBufferMap();
        lock.unlock();
    }

    // 初始化作者集合
    private void initAuthorMap(List<Author> authorList) {
        authorMap = new HashMap<>();
        for(Author author : authorList) {
            authorMap.put(author.getId(), author);
        }
    }

    // 初始化热点文章缓存集合
    private void initBlogsBufferMap(List<Blog> blogList) {
        blogsBufferMap = new HashMap<>();
        for(Blog theBlog : blogList) {
            blogsBufferMap.put(theBlog.getId(), theBlog);
        }
    }

    // 初始化文章作者对应集合
    private void initBlogAuthorMap() {
        blogAuthorMap = new HashMap<>();
        Set<Integer> blogIdSet = blogsBufferMap.keySet();
        Iterator<Integer> iterator = blogIdSet.iterator();
        while(iterator.hasNext()) {
            int blogId = iterator.next();
            Integer authorId = authorService.getAuthorId(blogId);
            blogAuthorMap.put(blogId, authorId);
        }
    }

    // 初始化文章标签集合
    private void initBlogTagsMap() {
        blogTagsMap = new HashMap<>();
        Set<Integer> blogIdSet = blogsBufferMap.keySet();
        Iterator<Integer> iterator = blogIdSet.iterator();
        while(iterator.hasNext()) {
            int blogId = iterator.next();
            List<Integer> tagIdList = getTheBlogTagsId(blogId);
            List<Tag> tagList = new LinkedList<>();
            for(Integer tagId : tagIdList) {
                tagList.add(tagsMap.get(tagId));
            }
            blogTagsMap.put(blogId, tagList);
        }
    }

    // 初始化标签集合
    private void initTagsMap(List<Tag> tagList) {
        this.tagsList = tagList;
        tagsMap = new HashMap<>();
        for(Tag tag : tagList) {
            tagsMap.put(tag.getId(), tag);
        }
    }

    // 初始化热点文章评论集合
    private void initCommentsBufferMap() {
        commentsBufferMap = new HashMap<>();
        Set<Integer> blogIdSet = blogsBufferMap.keySet();
        Iterator<Integer> iterator = blogIdSet.iterator();
        while(iterator.hasNext()) {
            int blogId = iterator.next();
            commentsBufferMap.put(blogId, getCommentsMap(blogId));
        }
    }

    // 初始化标签文章缓存集合
    private void initTagBlogsBufferMap() {
        tagBlogsBufferMap = new HashMap<>();
        for(Tag tag : tagsList) {
            List<Integer> blogsIdList = getBlogsIdList(tag.getId());
            Map<Integer, Blog> blogMap = new HashMap<>();
            for(Integer blogId : blogsIdList) {
                blogMap.put(blogId, blogService.getBlog(blogId));
            }
            tagBlogsBufferMap.put(tag.getId(), blogMap);
        }
    }

    // 获取评论集合
    public Map<Integer, Comment> getCommentsMap(Integer blogId) {
        Map<Integer, Comment> commentsMap = new HashMap<>();
        List<Integer> commentIdList = this.getTheBlogCommentsId(blogId);
        for(Integer commentId : commentIdList) {
            commentsMap.put(commentId, this.getTheComment(commentId));
        }
        return commentsMap;
    }

    // 从有序的热点文章的缓存中拿5篇文章出来
    public List<Blog> getFiveHotBlogs(int pageIndex) {
        List<Blog> blogList = new LinkedList<>();
        for(int i = (pageIndex - 1) * 5; (i < blogsList.size()) && (i < pageIndex * 5); i ++) {
            blogList.add(blogsList.get(i));
        }
        return blogList;
    }

    // 从有序的热点文章中拿前5篇
    public List<Blog> getFiveHotBlogs() {
        return getFiveHotBlogs(1);
    }

    // 从标签文章缓存中拿5篇
    public List<Blog> getFiveTagBlogs(Integer tagId, Integer pageIndex) {
        Map<Integer, Blog> blogsMap = tagBlogsBufferMap.get(tagId);
        List<Blog> resultList = new LinkedList<>();
        Object[] blogsMapKeyArray = blogsMap.keySet().toArray();
        for(int i = (pageIndex - 1) * 5; i < blogsMapKeyArray.length && i < pageIndex * 5; i ++) {
            resultList.add(blogsMap.get(blogsMapKeyArray[i]));
        }
        return resultList;
    }

    // 删除缓存中的一篇文章（当删除一篇文章的时候需要这么做）
    public void removeOneBufferBlog(Integer blogId) {
        Lock lock = new ReentrantLock();
        lock.lock();
        blogsBufferMap.remove(blogId);
        blogAuthorMap.remove(blogId);
        blogTagsMap.remove(blogId);
        commentsBufferMap.remove(blogId);
        List<Integer> blogTagsIdList = getTheBlogTagsId(blogId);
        for(Integer tagId : blogTagsIdList) {
            tagBlogsBufferMap.get(tagId).remove(blogId);
        }
        lock.unlock();
    }

    // 删除缓存中的一条评论（当删除一条评论的时候需要这么做）
    public void removeOneBufferComment(Integer commentId) {
        Iterator<Integer> it = commentsBufferMap.keySet().iterator();
        while(it.hasNext()) {
            commentsBufferMap.get(it.next()).remove(commentId);
        }
    }

    // 当删除一篇文章的时候，这篇文章带有的所有评论都要删除
    public void removeComments(List<Integer> commentsIdList) {
        for(Integer commentId : commentsIdList) {
            removeOneBufferComment(commentId);
        }
    }

    // 删除缓存中的一个标签（当删除一个标签的时候需要这么做）
    public void removeOneBufferTag(Integer tagId) {
        Tag tag = tagsMap.get(tagId);
        tagsList.remove(tag);
        tagsMap.remove(tagId);
        Iterator<Integer> it = blogTagsMap.keySet().iterator();
        while(it.hasNext()) {
            blogTagsMap.get(it.next()).remove(tag);
        }
        tagBlogsBufferMap.remove(tagId);
    }

    // 删除缓存中的一个作者（当删除一个作者的时候需要这么做）
    public void removeOneAuthor(int authorId) {
        authorMap.remove(authorId);
    }

    // 获取某个作者写的所有文章的id
    public List<Integer> getTheAuthorBlogsId(int authorId) {
        List<Integer> blogIdList = new LinkedList<>();
        Iterator<Integer> it = blogAuthorMap.keySet().iterator();
        while(it.hasNext()) {
            Integer key = it.next();
            if(blogAuthorMap.get(key) == authorId) {
                blogIdList.add(key);
                blogAuthorMap.remove(key);
            }
        }
        return blogIdList;
    }

    // 删除某个作者写的所有的文章
    public void removeTheAuthorBlogs(List<Integer> blogIdList, HttpServletRequest request, HttpServletResponse response) {
        for(Integer blogId : blogIdList) {
            util.deleteBlog(blogId);
        }
    }

    // 更新一篇文章的内容，
    public void updateBuffer(Integer blogId, Blog theBlog) {
        Lock lock = new ReentrantLock();
        lock.lock();
        // 这里为什么不用更新 tagBlogsBufferMap 里的 blog呢？
        // 因为我是通过反射，用新的blog去修改旧的blog的属性值，所以缓存里的blog也会随着被修改的，它们本质上是同一个东西
        if(blogsBufferMap.get(blogId) == null) return; // 如果这篇文章本身没被缓存，那就不需要缓存
        blogsBufferMap.put(blogId, theBlog); // 如果这篇文章是缓存过的，那就需要覆盖上去
        lock.unlock();
    }

}
