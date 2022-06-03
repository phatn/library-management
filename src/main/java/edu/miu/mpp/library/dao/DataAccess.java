package edu.miu.mpp.library.dao;

import edu.miu.mpp.library.model.Author;
import edu.miu.mpp.library.model.Book;
import edu.miu.mpp.library.model.LibraryMember;
import edu.miu.mpp.library.model.User;

import java.util.List;
import java.util.Map;

public interface DataAccess {
	Map<String, Book> readBooksMap();
	void saveBooksMap(Map<String, Book> bookMap);
	Map<String, User> readUserMap();
	Map<String, LibraryMember> readMemberMap();
	void saveNewMember(LibraryMember member);
	void deleteMember(String memberId);
	List<Author> readAuthorList();
	void saveAuthorList(List<Author> authorList);
}
