package com.sparta.picboy.repository.user;

import com.sparta.picboy.domain.user.Certification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface CertificationRepository extends JpaRepository<Certification, Long> {

    Optional<Certification> findByNumStr(String numStr);
    void deleteByPhoneNumAndNumStr(String phoneNum, String numStr);
}
