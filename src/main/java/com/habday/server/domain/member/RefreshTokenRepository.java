package com.habday.server.domain.member;

import net.bytebuddy.dynamic.DynamicType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    DynamicType.Builder.RecordComponentDefinition.Optional<RefreshToken> findByRefreshToken(String refreshToken);

}
