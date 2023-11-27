package org.example.repository;

import org.example.entity.Book;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

public class BookRepository {
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("dguBook");
    private EntityManager em = emf.createEntityManager();

    public Book findBookById(Long id) {
        return em.find(Book.class, id);
    }

    public Book saveBook(Book book) {
        try {
            em.getTransaction().begin();
            em.persist(book);
            em.getTransaction().commit();
            return book;
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    public List<Book> findAllBooks() {
        try {
            em.getTransaction().begin();
            List<Book> books = em.createQuery("SELECT b FROM Book b", Book.class).getResultList();
            em.getTransaction().commit();
            return books;
        } catch (Exception e) {
            e.printStackTrace();
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            return null;
        }
    }

    public void close() {
        if (emf != null) {
            emf.close();
        }
    }
}
