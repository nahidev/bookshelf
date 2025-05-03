package com.nahidev.bookshelf.service.exporter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nahidev.bookshelf.model.Book;
import java.util.List;

public class JsonExporter implements Exporter {
    @Override
    public String export(List<Book> books) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(books);
        } catch (Exception e) {
            throw new RuntimeException("Error exporting books to JSON", e);
        }
    }
}