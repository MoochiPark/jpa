package io.wisoft.daewon.main;

import io.wisoft.daewon.entity.Member;
import io.wisoft.daewon.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class Main {

  static EntityManagerFactory emf =
      Persistence.createEntityManagerFactory("jpabook");

  public static void main(String... args) {
    EntityManager em = emf.createEntityManager();
    EntityTransaction tx = em.getTransaction();
    tx.begin();

//    testSave(em);
//    updateRelation(em);
//    deleteRelation(em);
//    queryLogicJoin(em);
//    biDirection(em);
//    testSaveNonOwner(em);
    testPlainObjectBiDirection(em);
    tx.commit();
    em.close();
    emf.close();
  }

  private static void testSave(final EntityManager em) {
    // 팀1 저장
    Team team1 = new Team("team1", "팀1");
    em.persist(team1);

    // 회원1 저장
    Member member1 = new Member("member1", "회원1");
    member1.setTeam(team1);  // 연관관계 설정 member1 -> team1
    em.persist(member1);

    // 회원1 저장
    Member member2 = new Member("member2", "회원2");
    member2.setTeam(team1);  // 연관관계 설정 member1 -> team1
    em.persist(member2);
  }

  private static void queryLogicJoin(final EntityManager em) {
    String jpql = "select m from Member m join m.team t where t.name=:teamName";

    List<Member> resultList = em.createQuery(jpql, Member.class)
        .setParameter("teamName", "팀1")
        .getResultList();

    resultList.forEach(m -> System.out.println("[query] member.username=" + m.getUsername()));
  }

  private static void updateRelation(final EntityManager em) {
    // 새로운 팀2
    Team team2 = new Team("team2", "팀2");
    em.persist(team2);

    // 회원에 새로운 팀2 설정
    em.find(Member.class, "member1").setTeam(team2);
  }

  private static void deleteRelation(final EntityManager em) {
    Member member1 = em.find(Member.class, "member1");
    member1.setTeam(null);
  }

  private static void biDirection(final EntityManager em) {
    Team team = em.find(Team.class, "team1");
    team.getMembers().forEach(t -> System.out.println("member.username=" + t.getUsername()));
  }

  private static void testSaveNonOwner(final EntityManager em) {
    // 회원1 저장
    Member member1 = new Member("member1", "회원1");
    em.persist(member1);

    // 회원1 저장
    Member member2 = new Member("member2", "회원2");
    em.persist(member2);

    Team team1 = new Team("team1", "팀1");
    // 주인이 아닌 곳에만 연관관계 설정
    team1.getMembers().add(member1);
    team1.getMembers().add(member2);

    em.persist(team1);
  }

  private static void testPlainObjectBiDirection(final EntityManager em) {
    // 팀1 저장
    Team team1 = new Team("team1", "팀1");
    em.persist(team1);

    Member member1 = new Member("member1", "회원1");

    member1.setTeam(team1);          // 연관관계 설정 member1 -> team1
    team1.getMembers().add(member1); // 연관관계 설정 team1 -> member1
    em.persist(member1);

    Member member2 = new Member("member2", "회원2");

    member2.setTeam(team1);          // 연관관계 설정 member2 -> team1
    team1.getMembers().add(member2); // 연관관계 설정 team1 -> member2
    em.persist(member2);

    List<Member> members = team1.getMembers();
    System.out.println("members.size = " + members.size());
  }

}