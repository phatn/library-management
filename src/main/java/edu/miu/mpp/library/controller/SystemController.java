package edu.miu.mpp.library.controller;

import edu.miu.mpp.library.exception.BookAddException;
import edu.miu.mpp.library.exception.BookCheckoutException;
import edu.miu.mpp.library.exception.LoginException;
import edu.miu.mpp.library.model.Author;
import edu.miu.mpp.library.model.Role;
import edu.miu.mpp.library.model.Book;
import edu.miu.mpp.library.model.LibraryMember;
import edu.miu.mpp.library.service.*;

import java.util.List;
import java.util.Map;

public class SystemController implements FrontController {

    private LoginService loginService = (LoginService) ServiceFactory.getServiceInstance(LoginService.class);

    private LibraryMemberService libraryMemberService = (LibraryMemberService) ServiceFactory.getServiceInstance(LibraryMemberService.class);

    private BookService bookService = (BookService) ServiceFactory.getServiceInstance(BookService.class);

    private AuthorService authorService = (AuthorService) ServiceFactory.getServiceInstance(AuthorService.class);

    @Override
    public Role login(String username, char[] password) throws LoginException {
        return loginService.login(username, password);
    }

    @Override
    public Map<String, Book> getAllBooks() {
        return bookService.findAllBooks();
    }

    @Override
    public void saveBooks(Map<String, Book> books) {
        bookService.saveBooks(books);
    }

    @Override
    public Map<String, LibraryMember> getAllLibraryMembers() {
        return libraryMemberService.findAllLibraryMembers();
    }

    @Override
    public void addNewLibraryMember(LibraryMember libraryMember) {
        libraryMemberService.addOrUpdateNewLibraryMember(libraryMember);
    }

    @Override
    public void deleteLibraryMember(String memberId) {
        libraryMemberService.deleteLibraryMember(memberId);
    }

    @Override
    public LibraryMember checkoutBook(String libraryMemberID, String isbn) throws BookCheckoutException {
        return bookService.checkout(libraryMemberID, isbn);
    }

    public List<Author> getAuthors() {
        return authorService.findAllAuthors();
    }

    @Override
    public void addBook(String isbn, String title, int maxCheckoutLength, List<Author> authors) throws BookAddException {
        bookService.addBook(isbn, title, maxCheckoutLength, authors);
    }
}
