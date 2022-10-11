package com.sparta.picboy.domain.user;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Certification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String numStr;

    @Column
    private String phoneNum;

    public Certification(String phoneNum, String numStr) {
        this.numStr = numStr;
        this.phoneNum = phoneNum;
    }

    public void update(String numStr) {
        this.numStr = numStr;
    }
}
