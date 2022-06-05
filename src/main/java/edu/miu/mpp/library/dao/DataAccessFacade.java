package edu.miu.mpp.library.dao;

import edu.miu.mpp.library.model.Author;
import edu.miu.mpp.library.model.Book;
import edu.miu.mpp.library.model.LibraryMember;
import edu.miu.mpp.library.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DataAccessFacade implements DataAccess {

	private static final Logger LOG = LoggerFactory.getLogger(DataAccessFacade.class);

	enum StorageType {
		BOOKS, MEMBERS, USERS, AUTHORS
	}

	public static final String STORAGE_HOME = System.getenv("STORAGE_HOME");

	public static final String OUTPUT_DIR;

	static {
		LOG.info("STORAGE_HOME = {}", STORAGE_HOME);
		OUTPUT_DIR = (STORAGE_HOME == null ? System.getProperty("user.dir") : STORAGE_HOME) + File.separator + "storage";
		LOG.info("OUTPUT_DIR = {}", OUTPUT_DIR);
	}

	//implement: other save operations
	public void saveNewMember(LibraryMember member) {
		Map<String, LibraryMember> mems = readMemberMap();
		String memberId = member.getMemberId();
		mems.put(memberId, member);
		saveToStorage(StorageType.MEMBERS, mems);	
	}

	public void deleteMember(String memberId) {
		Map<String, LibraryMember> mems = readMemberMap();
		mems.remove(memberId);
		saveToStorage(StorageType.MEMBERS, mems);
	}

	@SuppressWarnings("unchecked")
	public  Map<String, Book> readBooksMap() {
		//Returns a Map with name/value pairs being
		//   isbn -> Book
		return (HashMap<String,Book>) readFromStorage(StorageType.BOOKS);
	}

	public void saveBooksMap(Map<String, Book> bookMap) {
		saveToStorage(StorageType.BOOKS, bookMap);
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, LibraryMember> readMemberMap() {
		//Returns a Map with name/value pairs being
		//   memberId -> LibraryMember
		return (HashMap<String, LibraryMember>) readFromStorage(
				StorageType.MEMBERS);
	}
	
	
	@SuppressWarnings("unchecked")
	public Map<String, User> readUserMap() {
		//Returns a Map with name/value pairs being
		//   userId -> User
		return (Map<String, User>)readFromStorage(StorageType.USERS);
	}

	@SuppressWarnings("unchecked")
	public List<Author> readAuthorList() {
		// Returns a List of authors
		return (List<Author>)readFromStorage(StorageType.AUTHORS);
	}

	public void saveAuthorList(List<Author> authorList) {
		saveToStorage(StorageType.AUTHORS, authorList);
	}
	
	
	/////load methods - these place test data into the storage area
	///// - used just once at startup  
	
		
	static void loadBookMap(List<Book> bookList) {
		Map<String, Book> books = new HashMap<>();
		bookList.forEach(book -> books.put(book.getIsbn(), book));
		saveToStorage(StorageType.BOOKS, books);
	}
	static void loadUserMap(List<User> userList) {
		Map<String, User> users = new HashMap<>();
		userList.forEach(user -> users.put(user.getUsername(), user));
		saveToStorage(StorageType.USERS, users);
	}
 
	static void loadMemberMap(List<LibraryMember> memberList) {
		Map<String, LibraryMember> members = new HashMap<>();
		memberList.forEach(member -> members.put(member.getMemberId(), member));
		saveToStorage(StorageType.MEMBERS, members);
	}

	static void loadAuthorList(List<Author> authorList) {
		List<Author> authors = new ArrayList<>(authorList);
		saveToStorage(StorageType.AUTHORS, authors);
	}
	
	static void saveToStorage(StorageType type, Object ob) {
		ObjectOutputStream out = null;
		try {
			Path path = FileSystems.getDefault().getPath(OUTPUT_DIR, type.toString());
			out = new ObjectOutputStream(Files.newOutputStream(path));
			out.writeObject(ob);
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			if(out != null) {
				try {
					out.close();
				} catch(Exception e) {}
			}
		}
	}
	
	static Object readFromStorage(StorageType type) {
		ObjectInputStream in = null;
		Object retVal = null;
		try {
			Path path = FileSystems.getDefault().getPath(OUTPUT_DIR, type.toString());
			in = new ObjectInputStream(Files.newInputStream(path));
			retVal = in.readObject();
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(in != null) {
				try {
					in.close();
				} catch(Exception e) {}
			}
		}
		return retVal;
	}
	
	
	
	final static class Pair<S,T> implements Serializable{
		
		S first;
		T second;
		Pair(S s, T t) {
			first = s;
			second = t;
		}
		@Override 
		public boolean equals(Object ob) {
			if(ob == null) return false;
			if(this == ob) return true;
			if(ob.getClass() != getClass()) return false;
			@SuppressWarnings("unchecked")
			Pair<S,T> p = (Pair<S,T>)ob;
			return p.first.equals(first) && p.second.equals(second);
		}
		
		@Override 
		public int hashCode() {
			return first.hashCode() + 5 * second.hashCode();
		}
		@Override
		public String toString() {
			return "(" + first.toString() + ", " + second.toString() + ")";
		}

		private static final long serialVersionUID = 5399827794066637059L;
	}
	
}
