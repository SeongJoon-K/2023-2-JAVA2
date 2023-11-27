package org.example.entity;

import javax.persistence.*;

@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "genre")
    private String genre;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "price")
    private int price;

    @Column(name = "book_image")
    private String bookImage;
}