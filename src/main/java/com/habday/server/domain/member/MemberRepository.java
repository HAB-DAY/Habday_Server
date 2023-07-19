package com.habday.server.domain.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    //소셜 로그인으로 반환되는 email을 통해 이미 생성된 사용자인지 처음 가입하는 사용자인지 판단하기 위한 메소드
    Member findByEmail(String email);
    public Member findByNickName(String nickName);


}
