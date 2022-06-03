package edu.miu.mpp.library.service;

import edu.miu.mpp.library.model.Book;

import java.util.Map;

public class BookService extends AbstractService {
    public Map<String, Book> findAllBooks() {
        return dataAccess.readBooksMap();
    }

    public void saveBooks(Map<String, Book> books) {
        dataAccess.saveBooksMap(books);
    }
}
