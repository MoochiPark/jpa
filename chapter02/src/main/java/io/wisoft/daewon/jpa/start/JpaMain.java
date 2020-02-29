package io.wisoft.daewon.jpa.start;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class JpaMain {

  public static void main(String... args) {
    // [엔티티 매니저 팩토리] - 생성
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpabook");
    // [엔티티 매니저] - 생성
    EntityManager em = emf.createEntityManager();
    // [트랜잭션] - 획득
    EntityTransaction tx = em.getTransaction();

    try {
      tx.begin();     // [트랜잭션] - 시작
//      save(em, new Member("id1", "대원", 27));      // 비즈니스 로직 실행
//      update(em, "id1", 27);
      delete(em, "id2");
//      System.out.println(find(em, "id1"));
      tx.commit();    // [트랜잭션] - 커밋'
      delete(em, "id1");
    } catch (Exception e) {
      e.printStackTrace();
      tx.rollback();  // [트랜잭션] - 롤백
    } finally {
      em.close();     // [엔티티 매니저] - 종료
    }
    emf.close();      // [엔티티 매니저 팩토리] - 종료
  }

  private static void save(final EntityManager em, final Member member) {
    em.persist(member);
  }

  private static Member find(final EntityManager em, final String id) {
    return em.find(Member.class, id);
  }

  private static List<Member> findList(final EntityManager em, final String query) {
    return em.createQuery(query, Member.class).getResultList();
  }

  private static void update(final EntityManager em, final String id, final Integer age) {
    Member member1 = em.find(Member.class, id);
    member1.setAge(age);
  }

  private static void delete(final EntityManager em, final String id) {
    Member member = em.find(Member.class, id);
    if (member != null) em.remove(member);
  }

}
