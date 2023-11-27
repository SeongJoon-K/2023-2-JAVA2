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
    private int firstBookQuantity;

    @Column(name = "second_book_id")
    private Long secondBookId;

    @Column(name = "second_book_quantity")
    private int secondBookQuantity;

    @Column(name = "third_book_id")
    private Long thirdBookId;

    @Column(name = "third_book_quantity")
    private int thirdBookQuantity;

    @Column(name = "fourth_book_id")
    private Long fourthBookId;

    @Column(name = "fourth_book_quantity")
    private int fourthBookQuantity;

    @Column(name = "total_price", nullable = false)
    private int totalPrice;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFirstBookId() {
        return firstBookId;
    }

    public void setFirstBookId(Long firstBookId) {
        this.firstBookId = firstBookId;
    }

    public int getFirstBookQuantity() {
        return firstBookQuantity;
    }

    public void setFirstBookQuantity(int firstBookQuantity) {
        this.firstBookQuantity = firstBookQuantity;
    }

    public Long getSecondBookId() {
        return secondBookId;
    }

    public void setSecondBookId(Long secondBookId) {
        this.secondBookId = secondBookId;
    }

    public int getSecondBookQuantity() {
        return secondBookQuantity;
    }

    public void setSecondBookQuantity(int secondBookQuantity) {
        this.secondBookQuantity = secondBookQuantity;
    }

    public Long getThirdBookId() {
        return thirdBookId;
    }

    public void setThirdBookId(Long thirdBookId) {
        this.thirdBookId = thirdBookId;
    }

    public int getThirdBookQuantity() {
        return thirdBookQuantity;
    }

    public void setThirdBookQuantity(int thirdBookQuantity) {
        this.thirdBookQuantity = thirdBookQuantity;
    }

    public Long getFourthBookId() {
        return fourthBookId;
    }

    public void setFourthBookId(Long fourthBookId) {
        this.fourthBookId = fourthBookId;
    }

    public int getFourthBookQuantity() {
        return fourthBookQuantity;
    }

    public void setFourthBookQuantity(int fourthBookQuantity) {
        this.fourthBookQuantity = fourthBookQuantity;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }
}


