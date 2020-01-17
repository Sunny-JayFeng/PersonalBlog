package com.lrabbitmx.personalblog.service;

import com.lrabbitmx.personalblog.dao.BookDao;
import com.lrabbitmx.personalblog.domain.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    @Autowired
    private BookDao bookDao;

    public List<Book> getBooks() {
        return bookDao.allBooks();
    }

    public Book getTheBook(int bookId) {
        return bookDao.selectById(bookId);
    }

    public String addBook(Book book) {
        if(bookDao.selectOne(book.getBookName(), book.getAuthorName(), book.getVersion()) != null) return "FAIL";
        bookDao.addBook(book);
        return "SUCCESS";
    }

    public String deleteBook(int bookId) {
        bookDao.deleteBookById(bookId);
        return "SUCCESS";
    }

    public String updateBook(Book book) {
        if(book == null) return "FAIL";
        System.out.println(book);
        bookDao.updateBook(book);
        return "SUCCESS";
    }
}
