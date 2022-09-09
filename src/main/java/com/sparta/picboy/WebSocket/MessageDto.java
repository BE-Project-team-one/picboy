package com.sparta.picboy.WebSocket;

import com.sparta.picboy.domain.user.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;


import java.util.Set;

@Getter
@NoArgsConstructor
public class MessageDto {
    private Set<Member> memberSet;
    private String content;

    private Long postId;

    public MessageDto(Set<Member> memberSet, String content, Long postId) {
        this.memberSet = memberSet;
        this.content = content;
        this.postId = postId;
    }
}
