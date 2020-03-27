package io.wisoft.pdw.example.nonidentifying.embededid;

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
    ParentId parentId = new ParentId("myId1", "myId2");
    parent.setId(parentId);
    parent.setName("parentName");
    em.persist(parent);

//    Parent parent1 = em.find(Parent.class, parentId);
//    System.out.println(parent1);

    et.commit();
    em.close();
    emf.close();
  }

}
