package io.wisoft.pdw.example.nonidentifying.idclass;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class Main {

  static EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpabook");

  public static void main(String... args) {
    EntityManager em = emf.createEntityManager();
    EntityTransaction et = em.getTransaction();
    et.begin();

    Parent parent = new Parent();
    parent.setId1("myId1");
    parent.setId2("myId2");
    parent.setName("parentName");
    em.persist(parent);

    ParentId parentId = new ParentId("myId1", "myId2");
    Parent parent2 = em.find(Parent.class, parentId);
    System.out.println(parent2);

    et.commit();
    em.close();
    emf.close();
  }

}
