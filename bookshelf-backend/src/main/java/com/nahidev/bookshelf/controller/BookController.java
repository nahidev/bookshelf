package com.nahidev.bookshelf.controller;

import com.nahidev.bookshelf.service.BookService;
import com.nahidev.bookshelf.model.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping(path= "api/bookshelf")
public class BookController {

    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public List<Book> getAllBook(){
        return bookService.getAllBook();
    }

    @GetMapping("{id}")
    public Book getBookById(@PathVariable Integer id){
        return bookService.getBookById(id);
    }

    @PostMapping
    public void addNewBook(@RequestBody Book book){
        bookService.addBook(book);
    }

    @DeleteMapping("{id}")
    public void deleteBookById(@PathVariable Integer id){
        bookService.deleteBookById(id);
    }

    @PutMapping("{id}")
    public void putBookById(@PathVariable Integer id, @RequestBody Book book){
        bookService.putBookById(id, book);
    }
}
