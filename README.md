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
* 검색은 사용자검색(닉네임), 게시글 검색 두종류가 있다.
* 게시글 홈(기본피드)는 오늘 작성된 게시글들을 가져온다.(createdDate로 체크해서)
* 해쉬태그를 만들어서 해쉬태그로 검색가능하다.
* 회원탈퇴, id & pw 변경 가능.

## ERD Diagram 설계

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

## API 설계
### users
* / - get
* /user/signup - get/post
* /user/login - get/post
* /user/prohibition - get
* /user/mypage - get
* /user/profile/{nickname} - get
* /user/nickname-post - post
* /user/search - get, param : nickname
* /admin - get, auth : admin
* /user/change-email - post
* /user/change-password - post
* /user/withdraw - post, body : string password
### board
### comment
### follow

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
## 파일 저장
* 파일 저장은 두 가지 조건이 존재한다.
* 첫번째 : 파일 없이 저장
* 두번째 : 파일 있게 저장
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

## map 전송 규약
* map으로 객체를 전송시 string으로 객체의 이름을 표시하는데,
* 현재 유저 : user
* 현재 보낼 객체(데이터) : body
* 객체가 두개일경우 각각의 이름을 작성
* 나머지 이름은 키와 값의 이름이 동일.

# 4. 나의 고민
팔로잉 고민한것 넣기, 널체크 고민한것 넣기, 중복체크 고민한것 넣기, 파일 고민한것 넣기

트위터 처럼 사용자 검색, 게시글 검색 나누고
사용자 이름을 등록가능하게 그리고 수정도 가능하게, 처음 이름은 서버에서 지정
닉네임 중복 체크(찾아서 안된다고 response return 하면 끝)
피드 고민해보기 -> 팔로잉 기반으로 가져오는데, 다가져오지말고 localdate.now() 체킹하고 가져오던가
전체 게시글중 localdate.now()로 가져오던가
텍스트 기반 sns 인 만큼 사진을 1개 넣던가 않넣던가 가능
아예 파일업로드 안하기엔 뭐하고
팔로잉에 유저칼럼 두개 넣고싶으면 다대다로 빼지말고
일대다 다대일 두개 테이블로 빼야함
팔로잉같은 경우 칼럼네임 다르게해서 조인해주면 됨
유튜브처럼 굳이 팔로우 리스트를 다른사람에게 보여주어야하나? 싶음 그냥 수만 리턴해줘도 될것 같고 writerpage에(이름바꾸기 writer말고)

게시글 저장시 파일 있게 저장, 파일 없게 저장
게시글 수정시 파일 있다 - 1. 없었는데 생김, 2. 있었는데 바꿈
게시글 수정시 파일 없다 - 1. 원래 없다, 2. 있었는데 안바꿈 -> dto에 기존꺼 set하면됨.
뷰에서 if문으로 saveFileName이 있으면 이미지 태그를 넣고 없으면 안넣는데
이것을 과연 json으로 보낼때 null을 그대로 반환해주어야하나, 아니면 null칼럼을 빼고 보내야하나
고민해야한다.
파일 테이블은 따로 빼지않는다. 이유는 sns의 특징이 짧은 글, 사진 하나가 포인트 이기때문이다.
확장성을 고려한 시스템이라면 테이블을 빼는게 맞겠지만 해당 sns의 전략은 무조건 사진 하나이기때문에
조인 테이블이 하나 늘어난것은 성능상 나쁘며, 또한 sns의 전략이 바뀌진 않기 때문에 
당장 파일 테이블을 따로 만들어 관리 할 필요가없다.
따라서 게시글 테이블에 칼럼을 생성하고 거기에 저장한다.

https://cantcoding.tistory.com/m/52

- 문서작성 할일
마이페이지와 작성자 페이지 문서 작성
널체크 문서 작성(유저, 게시글) (리스트는 검증 안해도 되고 객체 하나는 해야함 얘기하기)
회원탈퇴 문서작성(뷰에서 검증 끝남)
비밀번호 복호화 문서작성하기
id & pw 변경 문서작성

북마크 기능추가하기 팔로잉과 비슥하게 연관관계 필드만 존재할것같음.
북마크는 삭제하면 아예 삭제시켜버림
enum으로 status 걸 필요가 없음
특정 값만 받는 dto가 필요하다면 만들어야한다 를 고민에다가 문서작성하기

feat fix docs