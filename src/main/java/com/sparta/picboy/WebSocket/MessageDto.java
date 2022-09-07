package com.sparta.picboy.WebSocket;

import com.sparta.picboy.domain.user.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class MessageDto {
    private List<Member>members;
    private String content;

    private Long postId;

    public MessageDto(List<Member> members, String content, Long postId) {
        this.members = members;
        this.content = content;
        this.postId = postId;
    }
}
