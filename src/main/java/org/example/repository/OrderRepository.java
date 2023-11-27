package org.example.repository;

import org.example.entity.Order;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class OrderRepository {
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("dguBook");
    private EntityManager em = emf.createEntityManager();

    // 주문 저장
    public Order saveOrder(Order order) {
        try {
            em.getTransaction().begin(); // 트랜잭션 시작
            em.persist(order); // 주문 객체 저장
            em.getTransaction().commit(); // 트랜잭션 커밋
            return order;
        } catch (Exception e) {
            e.printStackTrace();
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback(); // 오류 발생시 롤백
            }
            return null;
        } finally {
            em.close(); // EntityManager 종료
        }
    }

    // 주문 조회 (예: ID로 조회)
    public Order findOrderById(Long id) {
        try {
            return em.find(Order.class, id); // 주문 ID로 조회
        } finally {
            em.close(); // EntityManager 종료
        }
    }

    // 기타 필요한 메서드들...

    // 리소스 정리
    public void close() {
        if (emf != null) {
            emf.close();
        }
    }
}
