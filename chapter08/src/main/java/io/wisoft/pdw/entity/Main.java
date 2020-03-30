package io.wisoft.pdw.entity;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class Main {

  static EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpabook");
  static EntityManager em = emf.createEntityManager();

  public static void main(String... args) {


    em.persist(new Member("", new Team()));

    Member member = em.find(Member.class, "member1");
    Team team = member.getTeam();
    em.close();
    emf.close();
  }


  public void printUserAndTeam(final String memberId) {
    Member member = em.find(Member.class, memberId);
    Team team = member.getTeam();
    System.out.println("회원 이름: " + member.getUsername());
    System.out.println("소속 팀: " + team.getName());
  }

  public void printUser(final String memberId) {
    Member member = em.find(Member.class, memberId);
    System.out.println("회원 이름: " + member.getUsername());
  }

}
