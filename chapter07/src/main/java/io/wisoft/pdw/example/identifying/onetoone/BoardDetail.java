package io.wisoft.pdw.example.identifying.onetoone;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class BoardDetail {

  @Id
  private Long boardId;

  @MapsId  // BoardDetail.boardId 매핑
  @OneToOne
  @JoinColumn(name = "board_id")
  private Board board;

  private String content;

}
