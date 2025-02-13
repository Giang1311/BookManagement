package com.book.rmi;
import java.io.Serializable;

public class Book implements Serializable {
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private int year;
    private int quantity;

    public Book(String isbn, String title, String author, String publisher, int year, int quantity) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.year = year;
        this.quantity = quantity;
    }

    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getPublisher() { return publisher; }
    public int getYear() { return year; }
    public int getQuantity() { return quantity; }

    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setPublisher(String publisher) { this.publisher = publisher; }
    public void setYear(int year) { this.year = year; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}