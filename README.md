breve - short 라는 뜻을 가짐
사진도, 글도 짧게 작성해서 올리는 sns
텍스트는 300자 제한, 사진 1개이하.
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

프로필 작성, 관계맺기, 커뮤니케이션, 콘텐츠 - sns 4대 요소
https://cantcoding.tistory.com/m/52