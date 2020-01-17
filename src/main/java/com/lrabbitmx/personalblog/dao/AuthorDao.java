package com.lrabbitmx.personalblog.dao;

import com.lrabbitmx.personalblog.domain.Author;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AuthorDao {

    List<Author> selectAllAuthors();

    Integer selectAuthorId(@Param("blogId") int blogId);

    Author selectOne(@Param("authorName") String authorName);

    Author selectOneById(@Param("authorId") Integer authorId);

    void insertAuthor(Author author);

    void deleteAuthor(@Param("authorId") int authorId);

    void deleteAuthorMapping(@Param("authorId") int authorId);
}
