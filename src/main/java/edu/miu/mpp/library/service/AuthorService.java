package edu.miu.mpp.library.service;

import edu.miu.mpp.library.model.Author;

import java.util.List;

public class AuthorService extends AbstractService {
    public List<Author> findAllAuthors() {
        return dataAccess.readAuthorList();
    }
}
