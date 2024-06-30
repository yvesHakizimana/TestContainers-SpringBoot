package com.rca.testcontainers;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    final BookRepository bookRepository;

    public BookService(BookRepository bookRepository){
        this.bookRepository= bookRepository;
    }

    public Book createBook(Book book){
        return bookRepository.save(book);
    }

    public List<Book> getAllBooks(){
        return bookRepository.findAll();
    }
}
