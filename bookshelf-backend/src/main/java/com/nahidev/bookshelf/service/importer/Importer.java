package com.nahidev.bookshelf.service.importer;

import com.nahidev.bookshelf.model.Book;
import java.util.List;

public interface Importer {
    List<Book> importData(String data);
}