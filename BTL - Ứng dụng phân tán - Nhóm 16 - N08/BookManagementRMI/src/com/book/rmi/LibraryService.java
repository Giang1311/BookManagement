package com.book.rmi;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface LibraryService extends Remote {
    void addBook(Book book) throws RemoteException;
    void removeBook(String isbn) throws RemoteException;
    Book getBook(String isbn) throws RemoteException;
    List<Book> getAllBooks() throws RemoteException;
    void updateBook(String isbn, String newTitle, String newAuthor, String newPublisher, int newYear, int newQuantity) throws RemoteException;
    List<Book> searchBooks(String isbn, String title, String author, String publisher, String year, String quantity) throws RemoteException;
}