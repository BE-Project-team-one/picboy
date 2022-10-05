package com.sparta.picboy.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    NOT_FOUND(404,"COMMON-ERR-404","PAGE NOT FOUND"),
    INTER_SERVER_ERROR(500,"COMMON-ERR-500","INTER SERVER ERROR"),
    NOT_FOUND_MEMBER(500, "MEMBER-ERR-500", "유저를 찾을 수 없습니다."),
    NOT_FOUNT_POST(500, "POST-ERR-500", "게시물을 찾을 수 없습니다."),
    FAIL_FILE_UPLOAD(500, "FILE-ERR-500","파일 업로드를 실패했습니다."),
    NOT_FOUND_TOPIC(500, "TOPIC-ERR-500","추천할 제시어가 없습니다."),
    ALREADY_COMPLETED_POST(500, "POST-ERR-500", "이미 완료된 게시물입니다."),
    INVALID_VALUE(500, "PASSWORD-ERR-500", "패스워드가 다릅니다."),
    ONLY_AUTHOR_ACCESSIBLE(500,"POST-ERR-500", "작성자만 접근할 수 있습니다."),
    NOT_FOUNT_COMMENT(500, "POST-ERR-500", "댓글을 찾을 수 없습니다."),
    UNAUTHORIZED (401, "LOGIN-ERR-401", "로그인이 필요합니다.(권한 없음)"),
    FORBIDDEN(401,"LOGIN-ERR-401", "로그인이 필요합니다.(FORBIDDEN)"),
    CERTIFICATION_NOT_MATCH(500, "CERTIFICATION-ERR-400", "인증 번호가 일치 하지 않습니다."),
    ALREADY_EXIST_USERNAME(500,"MEMBER-ERR-500", "이미 존재하는 아이디입니다."),
    ALREADY_EXIST_NICKNAME(500,"MEMBER-ERR-500", "이미 존재하는 닉네임입니다."),
    NOT_CORRECT_PASSWORD(500,"MEMBER-ERR-500", "비밀번호가 일치하지 않습니다."),
    NOT_CORRECT_CERTIFINUM(500,"MEMBER-ERR-500", "인증 번호가 일치하지 않습니다."),

    SUSPENDED_ACCOUNT(500, "MEMBER-ERR-500", "정지된 계정입니다.")
    ;



    private final int status;
    private final String errorCode;
    private final String message;
}