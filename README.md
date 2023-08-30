# Habday 서버
### 사용 기술

<img src="https://img.shields.io/badge/Spring Boot-6DB33F?style=flat&logo=springboot&logoColor=white"/>  <img src="https://img.shields.io/badge/MySql-4479A1?style=flat&logo=mysql&logoColor=white"/>  <img src="https://img.shields.io/badge/Java-007396?style=flat&logo=Java&logoColor=white" />
- 포트원 rest api를 통한 결제 연동 - 나이스페이
- 소셜 로그인
----
### API 목록
https://translucent-ulna-ff6.notion.site/Habday-API-6a7d9ed169f84da09ecfe3f91c3f55a6?pvs=4


----
### 생일 펀딩 참여 로직
1. 생성된 생일 펀딩에 참여(생일 전날까지 참여 가능)

   펀딩 참여 시 입력된 카드 번호로 결제 예약
2. 생일 당일 00시 5분 펀딩 성공 여부 확인

   성공 시 성공 메일 전송
   실패 시 결제 예약 취소 + 실패 메일 전송
3. 생일 당일 00시 30분 펀딩한 금액 결제

   포트원 웹훅을 통해 결제 상태를 db에 업데이트
4. 생일 당일 ~ 14일 이내에 펀딩 인증
   
   (인증 안할 시 추후 펀딩 생성 불가)
