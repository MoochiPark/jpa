# Chapter 07. 고급 매핑

이 장에서 배울 고급 매핑은 다음과 같다.

- **상속 관계 매핑**: 객체의 상속 관계를 어떻게 데이터베이스에 매핑하는지 배운다.
- **@MappedSuperclass**: 등록일, 수정일 같이 여러 엔티티에서 공통으로 사용하는 매핑 정보만 상속 받고 싶으면
  이 기능을 사용하면 된다.
- **복합 키와 식별 관계 매핑**: 데이터베이스의 식별자가 하나 이상일 때 매핑하는 방법을 다룬다.
  그리고 데이터베이스 설계에서 이야기하는 식별 관계와 비식별 관계에 대해서도 다룬다.
- **조인 테이블**: 테이블은 외래 키 하나로 연관관계를 맺을 수 있지만 연관관계를 관리하는 연결 테이블을 두는 방법도 있다.
  여기서는 이 연결 테이블을 매핑하는 방법을 다룬다.
- **엔티티 하나에 여러 테이블 매핑하기**: 보통 엔티티 하나에 테이블 하나를 매핑하지만 엔티티 하나에 여러 테이블을 매핑하는 방법도 있다. 





## 7.1 상속 관계 매핑

관계형 데이터베이스에는 객체지향 언어에서 다루는 상속이라는 개념이 없다. 
대신 슈퍼타입 서브타입 관계<sup>Super-Type Sub-Type Relationship</sup>라는 모델링 기법이 객체의 상속 개념과 가장 유사하다.
ORM에서의 상속 관계 매핑은 객체의 상속 구조와 데이터베이스의 슈퍼타입 서브타입 관계를 매핑하는 것이다.

> *슈퍼타입 서브타입 논리 모델*

![image](https://user-images.githubusercontent.com/43429667/76974938-2ae59300-6975-11ea-92b0-3a7843f0e2d2.png)



> *객체 상속 모델*

![image](https://user-images.githubusercontent.com/43429667/76975202-929bde00-6975-11ea-87cf-e748307619cf.png)

슈퍼타입 서브타입 논리 모델을 실제 물리 모델인 테이블로 구현할 때는 3가지 방법이 있다.

- **각각의 테이블로 변환**: 각각의 테이블로 만들고 조회할 때 조인을 사용. JPA에서는 **조인 전략** 이라고 한다.
- **통합 테이블로 변환**: 테이블 하나만 사용해서 통합한다. JPA에선 **단일 테이블 전략**이라 한다.
- **서브타입 테이블로 변환**: 서브 타입마다 하나의 테이블을 만든다. JPA에선 **구현 클래스마다 테이블 전략**이라 한다.



위 그림의 객체 상속 모델을 위 3가지 방법으로 매핑해보자.



### 7.1.1 조인 전략

조인 전략<sup>Joined Strategy</sup>은 엔티티 각각을 모두 테이블로 만들고 자식 테이블이 부모 테이블의 기본 키를 받아서 기본 키 + 외래 키로 사용하는 전략이다. 따라서 조회할 때 조인을 자주 사용한다. 
이 전략을 사용할 때 주의할 점은 객체는 타입으로 구분할 수 있지만 테이블은 타입의 개념이 없으므로 타입을 구분하는 컬럼을
추가해야 한다. (여기서는 DTYPE 컬럼을 구분 컬럼으로 사용한다.)

> *JOINED TABLE*

![image](https://user-images.githubusercontent.com/43429667/77292212-334a1f00-6d23-11ea-8595-90ed127328ef.png)

> *조인 전략 매핑 코드*

<script src="https://gist.github.com/3d05ad5c2159600649c4bfb5dd3f294e.js"/>

<script src="https://gist.github.com/bc16f95894c7e6041565828c87821f37.js"/>

매핑 정보를 분석해보자.

- @Inheritance(strategy = InheritanceType.JOINED): 상속 매핑은 부모 클래스에 @Inheritance를 사용해야 한다.
  그리고 매핑 전략을 조인 전략으로 지정해 주었다.
- @DiscriminatorColumn(name = "DTYPE"): 부모 클래스에 구분 컬럼을 지정한다. 이 컬럼으로 저장된 자식 테이블을 
  구분할 수 있다. 기본값이 DTYPE이므로 @DiscriminatorColumn으로 줄여서 사용해도 된다.
- @DiscriminatorValue("M"): 엔티티를 저장할 때 구분 컬럼에 입력할 값을 지정한다. 만약 영화 엔티티를 저장하면 구분 컬럼인 DTYPE에 M이 저장된다.



기본 값으로 자식 테이블은 부모 테이블의 ID 컬럼명을 그대로 사용하는데, 만약 자식 테이블의 기본 키 컬럼명을 변경하고 싶으면
@PrimaryKeyJoinColumn을 사용하면 된다.

<script src="https://gist.github.com/d55bff911b1cde6710ed08d443f0a9cf.js"/>

BOOK 테이블의 item_id 기본 키 컬럼명을 book_id로 변경했다.

조인 전략을 정리해보자.

- **장점**
  - 테이블이 정규화된다.
  - 외래 키 참조 무결성 제약조건을 활용할 수 있다.
  - 저장공간을 효율적으로 사용한다.
- **단점**
  - 조회할 때 조인이 많이 사용되므로 성능이 저하될 수 있다.
  - 조회 쿼리가 복잡하다.
  - 데이터를 등록하는 INSERT를 두 번 실행한다.

- **특징**
  - JPA 표준 명세는 구분 컬럼을 사용하도록 하지만 하이버네이트를 포함한 몇몇 구현체는 구분 컬럼<sup>@DiscriminatorColumn</sup>없이도 동작한다.
- **관련 애노테이션**
  - @PrimaryKeyJoinColumn, @DiscriminatorColumn, @DiscriminatorValue



### 7.1.2 단일 테이블 전략

단일 테이블 전략<sup>Single-Table Strategy</sup>은 이름 그래도 테이블을 하나만 사용한다. 그리고 구분 컬럼<sup>DTYPE</sup>으로 어떤 자식 데이터가 저장되었는지 저장한다. 조회할 때 조인을 사용하지 않으므로 일반적으로 가장 빠르다. 

> *SINGLE TABLE*

![image](https://user-images.githubusercontent.com/43429667/77297547-cf2c5880-6d2c-11ea-9ba2-51b30498d7b7.png)

이 전략을 사용할 때 주의점은 자식 엔티티가 매핑한 컬럼은 모두 null을 허용해야 한다는 점이다.
Book 엔티티를 저장하면 ITEM 테이블의 AUTHOR, ISBN 컬럼을 제외한 컬럼은 사용하지 않으므로 null이 입력되기 때문이다.

> *단일 테이블 전략 매핑*

<script src="https://gist.github.com/ccaa73e246a7504591c188ae5c7f365a.js"/>

<script src="https://gist.github.com/29f826813d947edd502a91f415a1fbb5.js"/>

단일 테이블 전략은 테이블 하나에 모든 것을 통합하므로 구분 컬럼을 필수로 사용해야 한다. 단일 테이블 전략의 장단점은
하나의 테이블을 사용하는 특징과 관련이 있다.

- **장점**
  - 조인이 필요 없으므로 일반적으로 조회 성능이 빠르다.
  - 조회 쿼리가 단순하다.
- **단점**
  - 자식 엔티티가 매핑한 컬럼은 모두 null을 허용해야 한다.
  - 단일 테이블에 모든 것을 저장하므로 테이블이 커질 수 있다. 상황에 따라서 조회 성능이 오히려 느려질 수 있다.
- **특징**
  - 구분 컬럼을 꼭 사용해야 한다. 따라서 @DiscriminatorColumn을 꼭 설정해야 한다.
  - @DiscriminatorValue를 지정하지 않으면 기본으로 엔티티 이름을 사용한다. <sup>예: Movie, Album, Book</sup>





### 7.1.3 구현 클래스마다 테이블 전략

구현 클래스마다 테이블 전략<sup>Table-per-Concrete-Class Strategy</sup>은 자식 엔티티마다 테이블을 만든다. 그리고 자식 테이블 각각에
필요한 컬럼이 모두 있다.

> *CONCRETE TABLE*

![image](https://user-images.githubusercontent.com/43429667/77299032-30552b80-6d2f-11ea-8cef-2dee90e7f672.png)

> *구현 클래스마다 테이블 전략 매핑*

