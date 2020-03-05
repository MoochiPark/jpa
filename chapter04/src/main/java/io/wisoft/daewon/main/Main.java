package io.wisoft.daewon.main;

import io.wisoft.daewon.entity.Member;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class Main {

  static EntityManagerFactory emf =
      Persistence.createEntityManagerFactory("jpabook");

  public static void main(String... args) {

    EntityManager em = emf.createEntityManager();
    EntityTransaction tx = em.getTransaction();
    tx.begin();
    logic(em);
    tx.commit();
  }

  private static void logic(final EntityManager em) {
    Member member = new Member();
    member.setUsername("pdw");
    em.persist(member);
    System.out.println("member.id = " + member.getId());
  }

}