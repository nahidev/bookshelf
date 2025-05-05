package com.nahidev.bookshelf.service.exporter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nahidev.bookshelf.model.Book;
import java.util.List;

public class JsonExporter implements Exporter {
    @Override
    public String export(List<Book> books) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JavaTimeModule module = new JavaTimeModule();
            mapper.registerModule(module);
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            return mapper.writeValueAsString(books);
        } catch (Exception e) {
            throw new RuntimeException("Error exporting books to JSON", e);
        }
    }
}