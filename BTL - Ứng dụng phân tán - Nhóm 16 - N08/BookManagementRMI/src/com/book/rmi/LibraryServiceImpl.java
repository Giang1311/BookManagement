package com.book.rmi;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class LibraryServiceImpl extends UnicastRemoteObject implements LibraryService {
    private List<Book> books;

    protected LibraryServiceImpl() throws RemoteException {
        books = new ArrayList<>();
    }

    @Override
    public void addBook(Book book) throws RemoteException {
        books.add(book);
    }

    @Override
    public void removeBook(String isbn) throws RemoteException {
        books.removeIf(book -> book.getIsbn().equals(isbn));
    }

    @Override
    public Book getBook(String isbn) throws RemoteException {
        return books.stream()
         .filter(book -> book.getIsbn().equals(isbn))
         .findFirst()
         .orElse(null);
    }

    @Override
    public List<Book> getAllBooks() throws RemoteException {
        return books;
    }

    @Override
    public void updateBook(String isbn, String newTitle, String newAuthor, String newPublisher, int newYear, int newQuantity) throws RemoteException {
        for (Book book : books) {
            if (book.getIsbn().equals(isbn)) {
                book.setTitle(newTitle);
                book.setAuthor(newAuthor);
                book.setPublisher(newPublisher);
                book.setYear(newYear);
                book.setQuantity(newQuantity);
                break;
            }
        }
    }

    @Override
    public List<Book> searchBooks(String isbn, String title, String author, String publisher, String year, String quantity) throws RemoteException {
        List<Book> searchResults = new ArrayList<>();

        for (Book book : books) {
            boolean match = true;

            if (!isbn.isEmpty() && !book.getIsbn().contains(isbn)) match = false;
            if (!title.isEmpty() && !book.getTitle().contains(title)) match = false;
            if (!author.isEmpty() && !book.getAuthor().contains(author)) match = false;
            if (!publisher.isEmpty() && !book.getPublisher().contains(publisher)) match = false;
            if (!year.isEmpty() && !String.valueOf(book.getYear()).contains(year)) match = false;
            if (!quantity.isEmpty() && !String.valueOf(book.getQuantity()).contains(quantity)) match = false;

            if (match) {
                searchResults.add(book);
            }
        }

        return searchResults;
    }
}