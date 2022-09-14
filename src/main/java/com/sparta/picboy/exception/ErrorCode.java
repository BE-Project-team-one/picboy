package com.sparta.picboy.exception;

import com.sparta.picboy.dto.response.ResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    NOT_FOUND(404,"COMMON-ERR-404","PAGE NOT FOUND"),
    INTER_SERVER_ERROR(500,"COMMON-ERR-500","INTER SERVER ERROR"),
    NOT_FOUND_MEMBER(400, "MEMBER-ERR-400", "유저를 찾을 수 없습니다."),
    NOT_FOUNT_POST(400, "POST-ERR-400", "게시물을 찾을 수 업습니다."),
    FAIL_FILE_UPLOAD(400, "FILE-ERR-400","파일 업로드를 실패했습니다."),
    NOT_FOUND_TOPIC(400, "TOPIC-ERR-400","추천할 제시어가 없습니다."),
    ALREADY_COMPLETED_POST(400, "POST-ERR-400", "이미 완료된 게시물입니다."),
    INVALID_VALUE(400, "PASSWORD-ERR-400", "패스워드가 다릅니다."),
    ONLY_AUTHOR_ACCESSIBLE(400,"POST-ERR-400", "작성자만 접근할 수 있습니다."),
    NOT_FOUNT_COMMENT(400, "POST-ERR-400", "댓글을 찾을 수 업습니다."),
    UNAUTHORIZED (401, "LOGIN-ERR-401", "로그인이 필요합니다.(권한 없음)"),
    FORBIDDEN(401,"LOGIN-ERR-401", "로그인이 필요합니다.(FORBIDDEN)"),
    CERTIFICATION_NOT_MATCH(400, "CERTIFICATION-ERR-400", "인증 번호가 일치 하지 않습니다."),
    ALREADY_EXIST_USERNAME(400,"MEMBER-ERR-400", "이미 존재하는 아이디입니다."),
    ALREADY_EXIST_NICKNAME(400,"MEMBER-ERR-400", "이미 존재하는 닉네임입니다.")
    NOT_FOUNT_COMMENT(400, "POST-ERR-400", "댓글을 찾을 수 업습니다."),

    //
    ALREADY_EXIST_USERNAME(400,"MEMBER-ERR-400", "이미 존재하는 아이디입니다."),
    ALREADY_EXIST_NICKNAME(400,"MEMBER-ERR-400", "이미 존재하는 닉네임입니다."),
    NOT_CORRECT_PASSWORD(400,"MEMBER-ERR-400", "비밀번호가 일치하지 않습니다."),
    NOT_CORRECT_CERTIFINUM(400,"MEMBER-ERR-400", "인증 번호가 일치하지 않습니다.")
    ;



    private final int status;
    private final String errorCode;
    private final String message;
}