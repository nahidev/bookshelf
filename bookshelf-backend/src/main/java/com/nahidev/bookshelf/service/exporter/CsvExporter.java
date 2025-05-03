package com.nahidev.bookshelf.service.exporter;

import com.nahidev.bookshelf.model.Book;
import java.util.List;
import java.util.stream.Collectors;

public class CsvExporter implements Exporter {
    @Override
    public String export(List<Book> books) {
        return books.stream()
                .map(book -> String.join(",", book.getTitle(), book.getAuthor(), book.getGenre(), String.valueOf(book.getRating())))
                .collect(Collectors.joining("\n"));
    }
}