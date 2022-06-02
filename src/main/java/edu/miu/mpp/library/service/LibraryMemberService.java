package edu.miu.mpp.library.service;

import edu.miu.mpp.library.model.LibraryMember;

import java.util.Map;

public class LibraryMemberService extends AbstractService {

    public Map<String, LibraryMember> findAllLibraryMembers() {
        return dataAccess.readMemberMap();
    }
}
