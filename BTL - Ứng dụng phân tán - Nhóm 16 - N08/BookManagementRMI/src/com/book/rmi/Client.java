package com.book.rmi;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            LibraryService libraryService = (LibraryService) registry.lookup("LibraryService");

            libraryService.addBook(new Book("001", "The Witcher", " Andrzej Sapkowski", "A Company", 2007, 10));
            libraryService.addBook(new Book("002", "300 bài code thiếu nhi", "Nguyen Van A", "B Company", 2010, 15));

            Book book = libraryService.getBook("001");
            System.out.println("Book: " + book.getTitle() + ", Author: " + book.getAuthor() + 
                               ", Publisher: " + book.getPublisher() + ", Year: " + book.getYear() + 
                               ", Quantity: " + book.getQuantity());

            libraryService.getAllBooks().forEach(b -> 
                System.out.println("ISBN: " + b.getIsbn() + ", Title: " + b.getTitle() + ", Author: " + b.getAuthor() + 
                                   ", Publisher: " + b.getPublisher() + ", Year: " + b.getYear() + 
                                   ", Quantity: " + b.getQuantity())
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}