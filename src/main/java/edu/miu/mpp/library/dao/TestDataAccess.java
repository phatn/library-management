package edu.miu.mpp.library.dao;

import edu.miu.mpp.library.model.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TestDataAccess {

    public static void main(String[] args) {
        DataAccess dataAccess = new DataAccessFacade();
        Map<String, Book> bookMap = dataAccess.readBooksMap();
        System.out.println("======================================== All Books & Copies ======================================== ");
        bookMap.forEach((k, v) -> {
            System.out.println(v);
            Arrays.stream(bookMap.get(k).getCopies()).forEach(bc -> System.out.println("\t" + bc));
        });

        System.out.println("\n========================================  All Library Members ======================================== ");
        Map<String, LibraryMember> libraryMemberMap = dataAccess.readMemberMap();
        libraryMemberMap.forEach((k, v) ->  {
            System.out.println(v);
            List<CheckoutRecordEntry> entries = Optional.ofNullable(libraryMemberMap.get(k))
                    .map(LibraryMember::getCheckoutRecord)
                    .map(CheckoutRecord::getCheckoutRecordEntries)
                    .stream()
                    .flatMap(List::stream).toList();
            entries.forEach(entry -> System.out.println("\t" + "CheckoutEntry{isbn=" + entry.getIsbn() + ",bookCopyId=" + entry.getBookCopyId() +
            ",checkoutDate=" + entry.getCheckoutDate() + ",dueDate=" + entry.getDueDate() + "}"));
        });

        System.out.println("\n========================================  All Users======================================== ");
        Map<String, User> userMap = dataAccess.readUserMap();
        userMap.forEach((k, v) -> System.out.println(k + " => " + v));
    }
}
