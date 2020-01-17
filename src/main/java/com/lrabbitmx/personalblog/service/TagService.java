package com.lrabbitmx.personalblog.service;

import com.lrabbitmx.personalblog.dao.TagDao;
import com.lrabbitmx.personalblog.domain.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService {

    @Autowired
    private TagDao tagDao;

    public List<Tag> getAllTags() {
        return tagDao.selectTags();
    }

    public List<Integer> getTheBlogTagsId(Integer blogId) {
        return tagDao.selectTagsId(blogId);
    }

    public List<Integer> getFiveBlogsId(Integer tagId, Integer pageIndex) {
        return tagDao.selectFiveBlogsId(tagId, (pageIndex - 1) * 5);
    }

    public Tag getTheTag(Integer tagId) {
        return tagDao.selectOneByTagId(tagId);
    }

    public Integer getTagBlogTotal(Integer tagId) {
        return tagDao.tagBlogTotal(tagId);
    }

    public String addTag(Tag tag) {
        if(tagDao.selectOneByTagName(tag.getTagName()) != null) return "FAIL";
        tagDao.insertTag(tag);
        return "SUCCESS";
    }

    public String deleteTag(int tagId) {
        tagDao.deleteTag(tagId);
        tagDao.deleteBlogTag(tagId);
        return "SUCCESS";
    }
}
