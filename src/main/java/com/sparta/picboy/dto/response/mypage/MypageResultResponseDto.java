package com.sparta.picboy.dto.response.mypage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MypageResultResponseDto {
    private MypageUserInfoResponseDto mypageUserInfoResponseDto;
    private List<MypageResponseDto> mypageResponseDto;


}
