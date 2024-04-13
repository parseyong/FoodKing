package com.example.foodking.auth;

import com.example.foodking.common.CommonResDTO;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@Validated
@Api(tags = "JWT")
public class JwtController {

    private final JwtProvider jwtProvider;

    @PostMapping("/refreshToken/reissue")
    public ResponseEntity<CommonResDTO> reissueToken(HttpServletRequest request){

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResDTO.of("로그인 성공!",jwtProvider.reissueToken(request)));
    }

    @PostMapping("/logout")
    public ResponseEntity<CommonResDTO> logOut(
            @AuthenticationPrincipal final Long userId,
            HttpServletRequest request){

        jwtProvider.logOut(userId,request.getHeader("Authorization"));
        return ResponseEntity.status(HttpStatus.OK).body(CommonResDTO.of("로그아웃 성공!",null));
    }
}
