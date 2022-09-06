package com.sparta.picboy.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class RandomTopic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // pk 값

    @Column
    private String topic; // 제시어 랜덤 값

    public RandomTopic(String topic) {
        this.topic = topic;
    }
}
