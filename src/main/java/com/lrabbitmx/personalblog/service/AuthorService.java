package com.lrabbitmx.personalblog.service;

import com.lrabbitmx.personalblog.dao.AuthorDao;
import com.lrabbitmx.personalblog.domain.Author;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorService {

    @Autowired
    private AuthorDao authorDao;

    public List<Author> getAuthorList() {
        return authorDao.selectAllAuthors();
    }

    public Integer getAuthorId(int blogId) {
        return authorDao.selectAuthorId(blogId);
    }

    public String login(String authorName, String password) {
        Author author = authorDao.selectOne(authorName);
        if(author == null || !(password.equals(author.getPassword()))) return "FAIL user name or password error";
        return "SUCCESS?id=" + author.getId();
    }

    public String addAuthor(Author author) {
        if(authorDao.selectOne(author.getAuthorName()) != null) return "FAIL";
        authorDao.insertAuthor(author);
        return "SUCCESS";
    }

    public Author getTheAuthor(Integer id) {
        return authorDao.selectOneById(id);
    }

    public int getAuthorId(String authorName, String password) {
        Author author = authorDao.selectOne(authorName);
        if(author == null || !(password.equals(author.getPassword()))) return -1;
        return author.getId();
    }

    public String deleteAuthor(int authorId) {
        System.out.println(authorId);
        authorDao.deleteAuthor(authorId);
        authorDao.deleteAuthorMapping(authorId);
        return "SUCCESS";
    }
}
