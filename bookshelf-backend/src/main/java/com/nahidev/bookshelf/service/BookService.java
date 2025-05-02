package com.nahidev.bookshelf.service;

import com.nahidev.bookshelf.model.Book;
import com.nahidev.bookshelf.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> getAllBook(){
        return bookRepository.findAll();
    }

    public Book getBookById(Integer id){
        return bookRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("The book with the id " + id +" is not found"));
    }

    public void addBook(Book book){
        bookRepository.save(book);
    }

    public void deleteBookById(Integer id){
        bookRepository.deleteById(id);
    }

    public void putBookById(Integer id, Book updatedBook){
        Book existing = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("The book with the id " + id + " is not found"));
        existing.setTitle(updatedBook.getTitle());
        existing.setAuthor(updatedBook.getAuthor());
        existing.setGenre(updatedBook.getGenre());
        existing.setRating(updatedBook.getRating());
        existing.setReadingDate(updatedBook.getReadingDate());

        bookRepository.save(existing);
}
}
