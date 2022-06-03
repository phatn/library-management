package edu.miu.mpp.library.service;

import java.util.HashMap;
import java.util.Map;

public class ServiceFactory {

    private ServiceFactory() {}
    private static final Map<Class<? extends Service>, Service> SERVICE_MAP = new HashMap<>();

    static {
        register(LoginService.class, new LoginService());
        register(BookService.class, new BookService());
        register(LibraryMemberService.class, new LibraryMemberService());
    }
    public static void register(Class<? extends Service> serviceClass, Service service) {
        SERVICE_MAP.put(serviceClass, service);
    }

    public static Service getServiceInstance(Class<? extends Service> serviceClass) {
        return SERVICE_MAP.get(serviceClass);
    }
}
