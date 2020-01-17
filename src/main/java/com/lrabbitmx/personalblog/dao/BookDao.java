package com.lrabbitmx.personalblog.dao;

import com.lrabbitmx.personalblog.domain.Book;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BookDao {

    List<Book> allBooks();

    Book selectById(@Param("bookId") int bookId);

    Book selectOne(@Param("bookName") String bookName,
                   @Param("authorName") String authorName,
                   @Param("version") String version);

    void addBook(Book book);

    void deleteBookById(@Param("bookId") int bookId);

    void updateBook(Book book);
}
