package org.example.entity;

import javax.persistence.*;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_book_id")
    private Long firstBookId;

    @Column(name = "first_book_quantity")
    private Integer firstBookQuantity;

    @Column(name = "second_book_id")
    private Long secondBookId;

    @Column(name = "second_book_quantity")
    private Integer secondBookQuantity;

    @Column(name = "third_book_id")
    private Long thirdBookId;

    @Column(name = "third_book_quantity")
    private Integer thirdBookQuantity;

    @Column(name = "fourth_book_id")
    private Long fourthBookId;

    @Column(name = "fourth_book_quantity")
    private Integer fourthBookQuantity;

    @Column(name = "total_price", nullable = false)
    private Double totalPrice;
}
