package com.example.foodking.CoolSms;

import com.example.foodking.Auth.JwtProvider;
import com.example.foodking.Config.SecurityConfig;
import com.example.foodking.CoolSms.DTO.PhoneAuthReqDTO;
import com.example.foodking.Exception.CommondException;
import com.example.foodking.Exception.ExceptionCode;
import com.google.gson.Gson;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = CoolSmsController.class)
@Import(SecurityConfig.class)
public class ControllerTest {

    @MockBean
    private CoolSmsService coolSmsService;
    @Autowired
    private MockMvc mockMvc;
    /*
        JwtAuthenticationFilter클래스는 Filter이므로 @WebMvcTest에 스캔이 되지만 JwtProvider클래스는
        @Component로 선언되어있으므로 @WebMvcTest의 스캔대상이 아니다.
        따라서 JwtAuthenticationFilter클래스에서 JwtProvider 빈을 가져올 수 없어 테스트가 정상적으로 수행되지 않는다.
        따라서 JwtProvider를 Mock객체로 대체하여 해당 문제를 해결하였다.
    */
    @MockBean
    private JwtProvider jwtProvider;

    @Test
    @DisplayName("문자보내기 테스트 -> (성공)")
    public void sendMessageSuccess() throws Exception {
        //given

        //when,then
        this.mockMvc.perform(get("/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("phoneNum","01056962173"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("인증번호 전송"))
                .andDo(print());

        verify(coolSmsService,times(1)).sendMessage(any(String.class));
    }

    @Test
    @DisplayName("문자보내기 테스트 -> (실패 : 전화번호 형식 예외)")
    public void sendMessageFail1() throws Exception {
        //given
        given(coolSmsService.sendMessage(any(String.class))).willThrow(new CommondException(ExceptionCode.NOT_PHONENUM));

        //when,then
        this.mockMvc.perform(get("/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("phoneNum","전화번호아님"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("올바른 전화번호 형식이 아닙니다"))
                .andDo(print());

        verify(coolSmsService,times(1)).sendMessage(any(String.class));
    }

    @Test
    @DisplayName("문자보내기 테스트 -> (실패 : Coolsms 내부 문제)")
    public void sendMessageFail2() throws Exception {
        //given
        given(coolSmsService.sendMessage(any(String.class))).willThrow(new CommondException(ExceptionCode.COOLSMS_EXCEPTION));

        //when,then
        this.mockMvc.perform(get("/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("phoneNum","01056962173"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("인증시스템에 문제가 발생했습니다"))
                .andDo(print());

        verify(coolSmsService,times(1)).sendMessage(any(String.class));
    }

    @Test
    @DisplayName("문자보내기 테스트 -> (실패 : 입력값 없음)")
    public void sendMessageFail3() throws Exception {
        //given

        //when,then
        this.mockMvc.perform(get("/messages")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("올바르지 않은 입력값입니다"))
                .andExpect(jsonPath("$.data.phoneNum").value("Required request parameter 'phoneNum' for method parameter type String is not present(관리자에게 문의하세요)"))
                .andDo(print());

        verify(coolSmsService,times(0)).sendMessage(any(String.class));
    }

    @Test
    @DisplayName("문자보내기 테스트 -> (실패 : 입력값 공백)")
    public void sendMessageFail4() throws Exception {
        //given

        //when,then
        this.mockMvc.perform(get("/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("phoneNum",""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("올바르지 않은 입력값입니다"))
                .andExpect(jsonPath("$.data.phoneNum").value("전화번호를 입력해주세요"))
                .andDo(print());

        verify(coolSmsService,times(0)).sendMessage(any(String.class));
    }

    @Test
    @DisplayName("인증번호 확인 테스트 -> (성공)")
    public void authNumCheckSuccess() throws Exception {
        //given
        PhoneAuthReqDTO phoneAuthReqDTO = PhoneAuthReqDTO.builder()
                .phoneNum("01056962173")
                .authenticationNumber("1234")
                .build();
        Gson gson = new Gson();
        String requestBody = gson.toJson(phoneAuthReqDTO);

        //when ,then
        this.mockMvc.perform(post("/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("인증 성공!"))
                .andDo(print());
        verify(coolSmsService,times(1)).authNumCheck(any(PhoneAuthReqDTO.class));
    }

    @Test
    @DisplayName("인증번호 확인 테스트 -> (실패 : 입력값 공백)")
    public void authNumCheckFail1() throws Exception {
        //given
        PhoneAuthReqDTO phoneAuthReqDTO = PhoneAuthReqDTO.builder()
                .phoneNum("")
                .authenticationNumber("")
                .build();
        Gson gson = new Gson();
        String requestBody = gson.toJson(phoneAuthReqDTO);

        //when ,then
        this.mockMvc.perform(post("/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("올바르지 않은 입력값입니다"))
                .andExpect(jsonPath("$.data.phoneNum").value("전화번호를 입력하세요"))
                .andDo(print());
        verify(coolSmsService,times(0)).authNumCheck(any(PhoneAuthReqDTO.class));
    }

    @Test
    @DisplayName("인증번호 확인 테스트 -> (실패 : 인증번호 불일치)")
    public void authNumCheckFail2() throws Exception {
        //given
        PhoneAuthReqDTO phoneAuthReqDTO = PhoneAuthReqDTO.builder()
                .phoneNum("01056962173")
                .authenticationNumber("1234")
                .build();
        Gson gson = new Gson();
        String requestBody = gson.toJson(phoneAuthReqDTO);
        doThrow(new CommondException(ExceptionCode.SMS_AUTHENTICATION_FAIL)).when(coolSmsService).authNumCheck(any(PhoneAuthReqDTO.class));


        //when ,then
        this.mockMvc.perform(post("/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("인증번호가 올바르지 않습니다"))
                .andDo(print());
        verify(coolSmsService,times(1)).authNumCheck(any(PhoneAuthReqDTO.class));
    }
}
