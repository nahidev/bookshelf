package com.nahidev.bookshelf.service.exporter;

import com.nahidev.bookshelf.model.Book;
import java.util.List;

public interface Exporter {
    String export(List<Book> books);
}