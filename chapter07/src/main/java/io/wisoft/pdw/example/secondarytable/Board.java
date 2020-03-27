package io.wisoft.pdw.example.secondarytable;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode
@Table(name = "board")
@SecondaryTable(name = "board_detail",
    pkJoinColumns = @PrimaryKeyJoinColumn(name = "board_detail_id"))
public class Board {

  @Id @GeneratedValue
  @Column(name = "board_id")
  private Long id;

  private String title;

  @Column(table = "board_detail")
  private String content;

}
