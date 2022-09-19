package com.sparta.picboy.repository.user;

import com.sparta.picboy.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository< RefreshToken, Long> {

    RefreshToken findByMember_Username(String username);

}
