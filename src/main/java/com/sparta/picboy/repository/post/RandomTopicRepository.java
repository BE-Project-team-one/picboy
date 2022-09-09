package com.sparta.picboy.repository.post;

import com.sparta.picboy.domain.RandomTopic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RandomTopicRepository extends JpaRepository<RandomTopic, Long> {
}
