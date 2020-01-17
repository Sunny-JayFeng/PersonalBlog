package com.lrabbitmx.personalblog.util;

import com.alibaba.fastjson.JSONObject;
import com.lrabbitmx.personalblog.controller.MainController;
import com.lrabbitmx.personalblog.domain.Author;
import com.lrabbitmx.personalblog.domain.Blog;
import com.lrabbitmx.personalblog.domain.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.LinkedList;
import java.util.List;

@Component
public class ProcessBlogMessage {

    @Autowired
    BufferMap bufferMap;
    @Autowired
    MainController mainController;

    // 设置值
    private String setTheBlogResponseMessage(Blog theBlog, List<Tag> tagList, Author author) {
        if(tagList == null) tagList = bufferMap.getTagList(theBlog.getId());
        if(author == null) author = bufferMap.getAuthor(theBlog.getId());
        String authorName = author.getAuthorName();
        StringBuilder tagMessage = new StringBuilder("");
        for(Tag tag : tagList) {
            tagMessage.append(tag.getTagName());
            tagMessage.append(",");
        }
        JSONObject blogJSONObject = setTheBlogBaseMessage(theBlog);
        blogJSONObject.put("thumbs", theBlog.getThumbs());
        if(!("".equals(tagMessage.toString())))
            blogJSONObject.put("tag", tagMessage.substring(0, tagMessage.length() - 1));
        if(authorName != null && !("".equals(authorName)) && authorName.length() != 0)
            blogJSONObject.put("author", authorName);

        return blogJSONObject.toJSONString();
    }

    // 设置值
    private JSONObject setTheBlogBaseMessage(Blog theBlog) {
        JSONObject blogJSONObject = new JSONObject();
        blogJSONObject.put("id", theBlog.getId());
        blogJSONObject.put("blogTitle", theBlog.getBlogTitle());
        blogJSONObject.put("contentHeader", theBlog.getContentHeader());
        blogJSONObject.put("contentUrl", theBlog.getContentUrl());
        blogJSONObject.put("isTop", theBlog.getIsTop());

        blogJSONObject.put("ctime", theBlog.getCtime());
        blogJSONObject.put("readCount", theBlog.getReadCount());

        return blogJSONObject;
    }

    // 拼接响应字符串
    public List<String> concatBlogMessage(List<Blog> blogList) {
        List<String> responseMessage = new LinkedList<>();
        for(Blog theBlog : blogList) {
            int blogId = theBlog.getId();
            responseMessage.add(
                    this.setTheBlogResponseMessage(theBlog,
                    bufferMap.blogTagsMap.get(blogId), // 有可能这篇文章是没有缓存的，所以自然blogTagsMap也取不到这篇文章的标签信息
                    bufferMap.authorMap.get(bufferMap.blogAuthorMap.get(blogId)) // 这里同上，也取不到作者信息
                    ));
        }
        return responseMessage;
    }
}
