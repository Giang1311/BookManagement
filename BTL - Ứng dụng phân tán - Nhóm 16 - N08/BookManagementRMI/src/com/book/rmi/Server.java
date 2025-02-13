package com.book.rmi;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {
    public static void main(String[] args) {
        try {
            LibraryService libraryService = new LibraryServiceImpl();
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("LibraryService", libraryService);
            
            System.out.println("Server is ready");
        } catch (Exception e) {
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}