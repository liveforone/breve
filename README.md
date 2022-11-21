# Breve
> breve는 이탈리아어로 short라는 뜻을 지님.

## 기술 스택
* Language : Java17
* DB : MySql
* ORM : Spring Data Jpa
* Spring Security
* LomBok
* Gradle
* Spring Boot 2.7.5
* Apache commons lang3

# 1. 설명
* 사진도, 글도 짧게 작성해서 올리는 sns
* 텍스트는 300자 제한, 사진 1개이하.
* rest-api server
* 화면단 고려하여 설계하였다.

# 2. 설계
* 사용자는 닉네임을 만들어야함.
* 닉네임과 id는 다른것임.(별개)
* 첫 닉네임은 서버에서 임의로 지정해주며, 후에 사용자가 수정을 원할 시 수정가능.
* 닉네임과 이메일은 중복 체크한다.
* 팔로잉 팔로우 시스템은 한개의 테이블로 모두 처리하기.
* 게시글 작성시 사진을 넣어도 되고 안넣어도 된다.
* 다만 사진 갯수는 1개 이하로 제한하고, 글자수는 300자로 제한한다.
* 댓글은 글자수 100자로 제한한다.
* 검색은 사용자검색(닉네임), 게시글 검색 두종류가 있다.
* 게시글 홈(기본피드)는 오늘 작성된 게시글들을 가져온다.(createdDate로 체크해서)
* 해쉬태그를 만들어서 해쉬태그로 검색가능하다.
* 회원탈퇴, id & pw 변경 가능.
* 모든 과정에서 null을 체크하여 run time error를 방지한다.

## ERD Diagram 설계
![스크린샷(139)](https://user-images.githubusercontent.com/88976237/201334830-5116dd5e-38f8-4149-a836-22b7bffda90b.png)

## Json Body 설계
### users
```
{
    "email" : "yc1234@gmail.com",
    "password" : "1234"
}
{
    "email" : "admin@breve.com",
    "password" : "1234"
}
// userChangeEmailRequest도 회원가입, 로그인과 같은 json body를 쓴다.
{
    "email" : "ms1234@gmail.com",
    "password" : "1234"
}
{
    "oldPassword" : "1234",
    "newPassword" : "1111"
}
```
### board
```
{
    "title" : "test1",
    "content" : "this is content"
}
{
    "title" : "test2",
    "content" : "this is content",
    "hashTag" : "it"
}
{
    "title" : "updated title",
    "content" : "this is updated content",
    "hashTag" : "it"
}
{
    "title" : "test3",
    "content" : "this is content",
    "hashTag" : "it"
} + uploadFile에 파일 뺴고 post 하기.(uploadFile이 없으면 에러뜸.)
```
### comment
```
{
    "content" : "this is comment1"
}
updated comment - text raw, edit
```

## API 설계
### users
```
/ - get
/user/signup - get/post
/user/login - get/post
/user/prohibition - get
/user/mypage - get
/user/profile/{nickname} - get
/user/nickname-post - post
/user/search - get, param : string nickname
/admin - get, auth : admin
/user/change-email - post
/user/change-password - post
/user/withdraw - post, body : string password
```
### board
```
/board/today - get
/board/best - get
/board/search - get, param : string keyword
/board/hashtag/{hashtag} - get
/board/post - get/post
/board/{id} - get
/board/image/{saveFileName} - get, file
/board/good/{id} - post
/board/edit/{id} - get/post
/board/delete/{id} - post
```
### comment
```
/comment/{boardId} - get 
/comment/post/{boardId} - post
/comment/edit/{id} - get/post
/comment/good/{id} - post
/comment/delete/{id} - post
```
### follow
```
/follow/{nickname} - post
/unfollow/{nickname} - post
/follow/my-follow - get
/follow/my-follower - get
/follow/profile-follow/{nickname} - get
/follow/profile-follower/{nickname} - get
```
### bookmark
```
/my-bookmark - get
/bookmark/post/{boardId} - post
/bookmark/cancel/{boardId} - post
```

# 3. 상세설명
## 게시판별 정렬 기준
* today - 조회수 -> 좋아요 -> 최신순
* best - 좋아요
* search - 조회수
* hashtag - 최신순
## 이메일 & 닉네임 중복 체크
* 중복체크 함수는 서비스 단에서 선언해주었다.
* 객체를 반환해서 controller단에서 if null로 체크하게되면
* entity를 dto로 변환하는 함수에 NullPointerException이 발생하게된다.
* 따라서 서비스단에서 바로 db에서 찾아 if null 체크를 해주고,
* 중복이 아니라면(== null) 1을 리턴해주고,
* 중복이라면(!= null) 0을 리턴해주었다.
* 두개의 함수는 회원가입(post)과 닉네임 수정부분에서 호출하여 사용했다.
## 무작위 닉네임 생성
* Apache Commons Lang 라이브러리를 사용했다.
* implementation 'org.apache.commons:commons-lang3:3.12.0' 을 gradle에 추가했다.
* 여러 메소드중에 나는 숫자와 문자를 출력하는 randomAlphanumeric()를 호출해서 사용했다.
## 회원 탈퇴
* 회원탈퇴시 비밀번호를 입력받는다.
* 서비스 로직의 passwordDecode() 함수로 비밀번호가 같은지 확인한다.
## 파일 저장
* 파일 저장은 두 가지 조건이 존재한다.
* 첫번째 : 파일 없이 저장
* 두번째 : 파일 있게 저장
* 파일 수정시 네가지 조건이 존재한다.
* 첫번째 : 없었는데 생김
* 두번째 : 있었는데 바꿈
* 세번쨰 : 원래 없다.
* 네번쨰 : 있었는데 안바꿈.
## 해쉬태그
* 해쉬태그 는 string 칼럼으로 만들고 1개 입력가능하게 한다.
* 짧은것이 포인트 무조건 짧고, 간결하게 하는 것이 해당 플랫폼의 특징이다.
* 해쉬태그는 필수가 아니다.
* 해쉬태그가 null 이면 @JsonInclude(JsonInclude.Include.NON_NULL)로 뷰에 넘기지 않는다.
* 게시글 상세(detail)에서 해쉬태그를 클릭하면 해쉬태그 게시판으로 이동한다.
## 글자수 제한
* 글자수를 제한하는것은 이번 프로젝트에서 상당히 중요한 부분 중 하나이다.
* @Column 어노테이션에서 length로 컬럼 데이터 크기를 지정해 줄 수 있는데,
* 이것은 dll 한정이고 지속적으로 글을 작성할때마다 validation 해주는 무언가가 필요하다.
* 따라서 org.springframework.boot:spring-boot-starter-validation 어노테이션의 
* @Size를 사용해서 지속적인 validation을 해주었다.
## 엔티티 직접리턴
* 서비스 로직을 보면 get무엇Entity() 와 같은 함수가 있다.
* 해당 함수는 엔티티를 직접 리턴하는 함수인데, 쓰임새는 다음과 같다.
* 컨트롤러에 상세조회 같은 부분에서 현재 로그인되어있는 유저와 게시글 작성자를 map으로 리턴하는 경우가 있다.
* 이럴때 연관관계를 활용해서 .getUsers().getEmail()과 같은 호출을 할때 사용하려고 만든것이다.
* 이것을 절대로 뷰로 리턴해서는안된다. 뷰 리턴은 오로지 dto로만 한다.
* 필드의 값이 id를 제외하고 한 개일지라도 반드시 dto로 리턴한다.
* dto리턴은 순환참조 방지 등 다양한 장점을 가졌기때문이다.
## 연관관계
* Board -> Users : ManyToOne, 단방향
* Comment -> Board : ManyToOne, 단방향
* Follow ->  Users, Users : ManyToOne, 단방향, 컬럼이름 다름
* Bookmark -> Users, Board : ManyToOne, 단방향
## map 전송 규약
* map으로 객체를 전송시 string으로 객체의 이름을 표시하는데,
* 현재 유저 : user
* 현재 보낼 객체(데이터) : body
* 객체가 두개일경우 각각의 이름을 작성
* 나머지 이름은 키와 값의 이름이 동일.
## 팔로잉
* 팔로잉을 할때에 상대방은 닉네임으로 입력을 받는다.
* 기본적인 프로필 조회 전략이 닉네임이기 때문이다.
* 현재 유저는 이메일로(principal)로 입력받는다.
* 해당 방법은 언팔로우 시에도 마찬가지로 적용된다.
* 내가 팔로우하는 사람들 & 프로필 주인이 팔로우하는 사람들은 이메일 & 닉네임으로 입력받아
* 해당 유저의 닉네임을 추출해서 List<String> 형태로 return한다.
* 나를 팔로우하는 사람들 & 프로필 주인을 팔로우하는 사람들도 마찬가지로 한다.
## jpql and query
* jpql도 여러개 조건을 거는 것이 가능하다.
* 방법은 아래와 같다.
```
@Query("select f from Follow f join fetch f.follower join fetch f.users where f.follower = :follower and f.users = :users")
    Follow findByFollowerAndUsers(@Param("follower") Users follower, @Param("users") Users users);
```
## 비밀번호 복호화
* 테스트하다가 놀란점은 암호화된 비밀번호를 db에서 그냥 꺼내면 암호화된 상태로 꺼내어진다.
* 이것을 복호화 하는 과정이 필요한데, encoder.matches(입력된 pw, 기존 pw)
* 를 사용하면 디코딩이 가능하다. 이것은 비밀번호가 같은지 확인하는 로직에서도 사용된다.

# 4. 나의 고민
## 널체크
* postman으로 테스트 하던 도중 이상만 문제를 발견했다.
* 그것은 NullPointerException 과 같은 런타임 에러이다.
* 런타임에러는 당연히 좋지않은 에러이다. 그중 상당수가 널포인터 에러이다.
* 이러한 에러를 어떻게 사전에 방지할까 하다가 널체크를 하기로 했다.
* 서비스로직에서 엔티티를 직접 조회하는 함수를 호출한다.
* 그리고 컨트롤러단에서 해당 함수로 가져온것이 null인지 if-else 문으로 체크한다.
* 또한 뷰로 넘기는 경우에 엔티티를 dto로 변환하는 함수가 있다.
* 해당 함수 안에서도 널인경우를 if-else 문으로 체크하여 널 문제를 해결했다.
* 리스트나 페이징은 널체크를 하지 않아도 된다. empty 값으로 return 되기때문이다.
* 그러나 객체 하나, 즉 detail 같은 경우에는 반드시 널을 체크해야한다.
## 중복 체크
* 회원가입이나 이메일 변경, 닉네임 변경 등을 할때 중복되는 문제가 발생했다.
* 사용자로 부터 입력받은 값을 서비스로직의 중복체크 함수로 넘긴다.
* 함수는 해당 값으로 db에서 조회해서 객체를 꺼내보고, 널이라면 = 중복이 아니라면 1을 리턴,
* 널이 아니라면 = 중복이라면 0을 리턴해준다.
* 컨트롤러에서는 해당 값을 판별해 ResponseEntity로 메세지를 보내준다
## 파일 고민
* 파일 테이블을 따로 빼야하나 고민하였다.
* 그런데 해당 프로젝트의 특징이 짧은 글, 사진 하나가 포인트이다.
* 파일 하나에서 그치는 것이 아니라 여러개를 앞으로 올리게될 확장성을 고려한 플랫폼이라면
* 파일 테이블을 따로 빼야 맞겠지만 해당 프로젝트는 그렇지 않기 때문에 빼지 않도록 한다.

# 5. 깨달은점
* 기존에는 널체크, 중복체크 등을 하지 않았다.
* 그렇지만 런타임 에러를 경험하는 다양한 api 테스트를 하고나서 중요성을 알게되었다.
* 앞으로의 프로젝트에 귀찮고 조금은 더러워(if문 때문에..)지도라도 널체크와 중복체크를 반드시 넣을것이다.

# 6. 추가사항
* if-else 를 기존의 버블 스타일에서 gate way 스타일로 변경
* 신청 서비스(팔로우, 북마크) 중복체크
* and query 네이밍 수정 -> findOne엔티티이름
* dto -> entity 메소드 서비스로직으로 이동 후 서비스 로직에서 처리
* 반복되는 entity -> dto builder 를 함수화 해서 불필요한 반복을 줄임.