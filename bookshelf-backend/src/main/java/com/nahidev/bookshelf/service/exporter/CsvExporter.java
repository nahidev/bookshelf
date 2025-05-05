package com.nahidev.bookshelf.service.exporter;

import com.nahidev.bookshelf.model.Book;
import java.util.List;
import java.util.stream.Collectors;

public class CsvExporter implements Exporter {
    @Override
    public String export(List<Book> books) {
        String headers = "title,author,genre,rating,readingDate";

        String rows = books.stream()
                .map(book -> String.join(",", 
                        book.getTitle(), 
                        book.getAuthor(), 
                        book.getGenre(), 
                        String.valueOf(book.getRating()), 
                        String.valueOf(book.getReadingDate())))
                .collect(Collectors.joining("\n"));

        return headers + "\n" + rows;
    }
}