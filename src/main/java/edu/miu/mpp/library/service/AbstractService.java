package edu.miu.mpp.library.service;

import edu.miu.mpp.library.dao.DataAccess;
import edu.miu.mpp.library.dao.DataAccessFacade;

public class AbstractService implements Service {
    protected DataAccess dataAccess = new DataAccessFacade();

}
