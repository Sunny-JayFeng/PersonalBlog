package com.lrabbitmx.personalblog.dao;

import com.lrabbitmx.personalblog.domain.Tag;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TagDao {

    List<Tag> selectTags();

    List<Integer> selectTagsId(@Param("blogId") int blogId);

    List<Integer> selectFiveBlogsId(@Param("tagId") int tagId, @Param("index") int index);

    Tag selectOneByTagName(@Param("tagName") String tagName);

    Tag selectOneByTagId(@Param("tagId") int tagId);

    Integer tagBlogTotal(@Param("tagId") int tagId);

    void insertTag(Tag tag);

    void deleteTag(@Param("tagId") int tagId);

    void deleteBlogTag(@Param("tagId") int tagId);
}
