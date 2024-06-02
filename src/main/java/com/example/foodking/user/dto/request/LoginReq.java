package com.example.foodking.user.dto.request;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/*
    역직렬화 과정에서 기본생성자가 없더라도 다른 생성자가 있다면 그 생성자를 이용하여 직렬화과정을 거친다
    또한 생성자가 하나도 없으면 기본생성자를 컴파일과정에서 자동으로 생성해준다.(버전,빌드도구 등에 따라 차이는 있다.)

    그러나 Build를 Gradle로 하지않고 인텔리제이로 할 경우
    다른 생성자가 있더라도 기본생성자가 없으면 직렬화과정이 정상적으로 이루어지지 않는다.

    따라서 @NoArgsConstructor를 명시적으로 선언해 주어 빌드도구에 의존하지 않도록 했고
    가독성을 위해 @NoArgsConstructor를 명시해주었다.

    빌더 사용 시 @AllArgsConstructor을 쓰지않아도 컴파일 과정에서 생성자가 없다면 자동으로 모든 필드를
    파라미터로 가지는 생성자를 만들어 준다.

    하지만 기본생성자가 필요하기도 하고 가독성을 위해 @AllArgsConstructor을 명시해주었다.
*/
@Getter
@Builder // 테스트를 위한 빌더추가
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LoginReq {

    @Email(message = "이메일 형식이 올바르지 않습니다")
    @NotBlank(message = "이메일 정보를 입력해주세요")
    private String email;

    @NotBlank(message = "비밀번호를 입력해주세요")
    private String password;
}
