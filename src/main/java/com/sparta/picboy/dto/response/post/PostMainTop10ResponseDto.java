package com.sparta.picboy.dto.response.post;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostMainTop10ResponseDto {
    private Long id; // 게시물 아이디
    private String gifUrl; // gif 이미지 링크
    private int likeCount; // 좋아요 수
    private String topic; // 제시어
    private String username; // 게시글 작성자
    private int memberCount; // 참여자 수

    public PostMainTop10ResponseDto(Long id, String gifUrl, int likeCount, String topic, String username, int memberCount) {
        this.id = id;
        this.gifUrl = gifUrl;
        this.likeCount = likeCount;
        this.topic = topic;
        this.username = username;
        this.memberCount = memberCount;

    }

}
