package edu.miu.mpp.library.controller;

import edu.miu.mpp.library.exception.LoginException;
import edu.miu.mpp.library.model.Role;
import edu.miu.mpp.library.model.Book;
import edu.miu.mpp.library.model.LibraryMember;
import edu.miu.mpp.library.service.*;

import java.util.Map;

public class SystemController implements FrontController {

    private LoginService loginService = (LoginService) ServiceFactory.getServiceInstance(LoginService.class);

    private LibraryMemberService libraryMemberService = (LibraryMemberService) ServiceFactory.getServiceInstance(LibraryMemberService.class);

    private BookService bookService = (BookService) ServiceFactory.getServiceInstance(BookService.class);

    @Override
    public Role login(String username, char[] password) throws LoginException {
        return loginService.login(username, password);
    }

    @Override
    public Map<String, Book> getAllBooks() {
        return bookService.findAllBooks();
    }

    @Override
    public Map<String, LibraryMember> getAllLibraryMembers() {
        return libraryMemberService.findAllLibraryMembers();
    }
}
