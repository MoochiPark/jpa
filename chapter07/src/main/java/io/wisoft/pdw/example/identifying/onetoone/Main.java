package io.wisoft.pdw.example.identifying.onetoone;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class Main {

  static EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpabook");
  static EntityManager em = emf.createEntityManager();
  static EntityTransaction et = em.getTransaction();
  public static void main(String... args) {
    et.begin();

    save();

    em.close();
    emf.close();
  }

  public static void save() {
    Board board = new Board();
    board.setTitle("제목");
    em.persist(board);

    BoardDetail boardDetail = new BoardDetail();
    boardDetail.setContent("내용");
    boardDetail.setBoard(board);
    em.persist(boardDetail);
  }

}
