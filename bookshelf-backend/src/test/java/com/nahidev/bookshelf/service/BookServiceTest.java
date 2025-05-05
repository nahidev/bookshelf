package com.nahidev.bookshelf.service;

import com.nahidev.bookshelf.model.Book;
import com.nahidev.bookshelf.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class BookServiceTest {

    @Mock
    private BookRepository bookRepository = mock(BookRepository.class);

    private BookService bookService;

    @BeforeEach
    void setUp() {
        bookService = new BookService(bookRepository);
    }

    @Test
    void testGetAllBook() {
        // Arrange
        Book book1 = new Book(1, "Title1", "Author1", "Genre1", 5, LocalDate.of(2023, 1, 1));
        Book book2 = new Book(2, "Title2", "Author2", "Genre2", 4, LocalDate.of(2023, 2, 1));
        when(bookRepository.findAll()).thenReturn(Arrays.asList(book1, book2));

        // Act
        List<Book> books = bookService.getAllBook();

        // Assert
        assertThat(books).hasSize(2);
        assertThat(books).containsExactly(book1, book2);
        Mockito.verify(bookRepository, Mockito.times(1)).findAll();
    }

    @Test
    void testGetBookByIdWhenBookExists() {
        // Arrange
        Book book = new Book(1, "Title1", "Author1", "Genre1", 5, LocalDate.of(2023, 1, 1));
        when(bookRepository.findById(1)).thenReturn(Optional.of(book));

        // Act
        Book result = bookService.getBookById(1);

        // Assert
        assertThat(result).isEqualTo(book);
        Mockito.verify(bookRepository, Mockito.times(1)).findById(1);
    }

    @Test
    void testGetBookByIdWhenBookDoesNotExist() {
        // Arrange
        when(bookRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        try {
            bookService.getBookById(1);
        } catch (IllegalStateException e) {
            assertThat(e.getMessage()).isEqualTo("The book with the id 1 is not found");
        }
        Mockito.verify(bookRepository, Mockito.times(1)).findById(1);
    }

    @Test
    void testAddBook() {
        // Arrange
        Book book = new Book(null, "Title1", "Author1", "Genre1", 5, LocalDate.of(2023, 1, 1));

        // Act
        bookService.addBook(book);

        // Assert
        Mockito.verify(bookRepository, Mockito.times(1)).save(book);
    }

    @Test
    void testDeleteBookById() {
        // Act
        bookService.deleteBookById(1);

        // Assert
        Mockito.verify(bookRepository, Mockito.times(1)).deleteById(1);
    }

    @Test
    void testPutBookByIdWhenBookExists() {
        // Arrange
        Book existingBook = new Book(1, "OldTitle", "OldAuthor", "OldGenre", 3, LocalDate.of(2023, 1, 1));
        Book updatedBook = new Book(1, "NewTitle", "NewAuthor", "NewGenre", 5, LocalDate.of(2023, 2, 1));
        when(bookRepository.findById(1)).thenReturn(Optional.of(existingBook));

        // Act
        bookService.putBookById(1, updatedBook);

        // Assert
        assertThat(existingBook.getTitle()).isEqualTo("NewTitle");
        assertThat(existingBook.getAuthor()).isEqualTo("NewAuthor");
        assertThat(existingBook.getGenre()).isEqualTo("NewGenre");
        assertThat(existingBook.getRating()).isEqualTo(5);
        assertThat(existingBook.getReadingDate()).isEqualTo(LocalDate.of(2023, 2, 1));
        Mockito.verify(bookRepository, Mockito.times(1)).save(existingBook);
    }

    @Test
    void testPutBookByIdWhenBookDoesNotExist() {
        // Arrange
        Book updatedBook = new Book(1, "NewTitle", "NewAuthor", "NewGenre", 5, LocalDate.of(2023, 2, 1));
        when(bookRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        try {
            bookService.putBookById(1, updatedBook);
        } catch (IllegalStateException e) {
            assertThat(e.getMessage()).isEqualTo("The book with the id 1 is not found");
        }
        Mockito.verify(bookRepository, Mockito.times(1)).findById(1);
    }
}