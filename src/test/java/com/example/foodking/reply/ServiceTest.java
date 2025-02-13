package com.example.foodking.reply;

import com.example.foodking.emotion.service.EmotionService;
import com.example.foodking.exception.CommondException;
import com.example.foodking.exception.ExceptionCode;
import com.example.foodking.recipe.domain.RecipeInfo;
import com.example.foodking.recipe.repository.RecipeInfoRepository;
import com.example.foodking.reply.domain.Reply;
import com.example.foodking.reply.dto.request.ReplyAddReq;
import com.example.foodking.reply.dto.request.ReplyUpdateReq;
import com.example.foodking.reply.dto.response.ReplyFindRes;
import com.example.foodking.reply.enums.ReplySortType;
import com.example.foodking.reply.repository.ReplyRepository;
import com.example.foodking.reply.service.ReplyService;
import com.example.foodking.user.domain.User;
import com.example.foodking.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ServiceTest {

    @InjectMocks
    private ReplyService replyService;
    @Mock
    private ReplyRepository replyRepository;
    @Mock
    private EmotionService emotionService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RecipeInfoRepository recipeInfoRepository;

    private User user;
    private RecipeInfo recipeInfo;
    private Reply reply;

    @BeforeEach
    void beforeEach(){
        this.user = spy(User.builder()
                .email("test@google.com")
                .nickName("test")
                .password("1234")
                .phoneNum("01011111111")
                .build());
        this.recipeInfo = RecipeInfo.builder()
                .user(user)
                .recipeName("testRecipeName")
                .recipeTip("testRecipeTip")
                .build();
        this.reply = Reply.builder()
                .user(user)
                .recipeInfo(recipeInfo)
                .content("testReplyContent")
                .build();
    }

    @Test
    @DisplayName("댓글 등록 테스트 -> 성공")
    public void addReplySuccess(){
        //given
        given(replyRepository.save(any(Reply.class))).willReturn(reply);
        given(userRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(user));
        given(recipeInfoRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(recipeInfo));

        //when
        replyService.addReply(1L,1L, ReplyAddReq.builder().content("댓글내용").build());

        //then
        verify(replyRepository,times(1)).save(any(Reply.class));
        verify(userRepository,times(1)).findById(any(Long.class));
        verify(recipeInfoRepository,times(1)).findById(any(Long.class));
    }

    @Test
    @DisplayName("댓글 등록 테스트 -> 존재하지 않는 유저")
    public void addReplyFail1(){
        //given
        given(userRepository.findById(any(Long.class))).willReturn(Optional.empty());

        //when,then
        try{
            replyService.addReply(1L,1L, ReplyAddReq.builder().content("댓글내용").build());
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.NOT_EXIST_USER);
            verify(replyRepository,times(0)).save(any(Reply.class));
            verify(userRepository,times(1)).findById(any(Long.class));
            verify(recipeInfoRepository,times(0)).findById(any(Long.class));
        }
    }

    @Test
    @DisplayName("댓글 등록 테스트 -> 존재하지 않는 레시피")
    public void addReplyFail2(){
        //given
        given(userRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(user));
        given(recipeInfoRepository.findById(any(Long.class))).willReturn(Optional.empty());

        //when,then
        try{
            replyService.addReply(1L,1L,ReplyAddReq.builder().content("댓글내용").build());
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.NOT_EXIST_RECIPEINFO);
            verify(replyRepository,times(0)).save(any(Reply.class));
            verify(userRepository,times(1)).findById(any(Long.class));
            verify(recipeInfoRepository,times(1)).findById(any(Long.class));
        }
    }

    @Test
    @DisplayName("댓글 조회 테스트 -> 성공")
    public void readReplySuccess(){
        //given
        given(replyRepository.findReplyPaging(any(),any(),any(Long.class)))
                .willReturn(List.of(ReplyFindRes.toDTO(reply,"test",true)));

        //when
        replyService.findReplyPaging(1L,1L, ReplySortType.LIKE,1L,12,false);

        //then
        verify(replyRepository,times(1)).findReplyPaging(any(),any(),any(Long.class));
    }

    @Test
    @DisplayName("댓글 조회 테스트 -> (실패 : 존재하지 않는 페이지)")
    public void readReplyFail(){
        //given

        //when,then
        try{
            replyService.findReplyPaging(1L,1L, ReplySortType.LIKE,1L,12,false);
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.NOT_EXIST_PAGE);
            verify(replyRepository,times(1)).findReplyPaging(any(),any(),any(Long.class));
        }
    }

    @Test
    @DisplayName("댓글 수정 테스트 -> 성공")
    public void updateReplySuccess(){
        //given
        given(replyRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(reply));
        given(user.getUserId()).willReturn(1L);

        //when
        replyService.updateReply(1L,1L, ReplyUpdateReq.builder().content("수정된 댓글").build());

        //then
        assertThat(reply.getContent()).isEqualTo("수정된 댓글");
        verify(replyRepository,times(1)).findById(any(Long.class));
        verify(replyRepository,times(1)).save(any(Reply.class));
    }

    @Test
    @DisplayName("댓글 수정 테스트 -> (실패 : 존재하지 않는 댓글)")
    public void updateReplyFail1(){
        //given
        given(replyRepository.findById(any(Long.class))).willReturn(Optional.empty());

        //when,then
        try{
            replyService.updateReply(1L,1L,ReplyUpdateReq.builder().content("수정된 댓글").build());
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.NOT_EXIST_REPLY);
            assertThat(reply.getContent()).isEqualTo("testReplyContent");
            verify(replyRepository,times(1)).findById(any(Long.class));
            verify(replyRepository,times(0)).save(any(Reply.class));
        }
    }

    @Test
    @DisplayName("댓글 수정 테스트 -> (실패 : 댓글 수정권한 없음)")
    public void updateReplyFail2(){
        //given
        given(replyRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(reply));
        given(user.getUserId()).willReturn(2L);

        //when,then
        try{
            replyService.updateReply(1L,1L,ReplyUpdateReq.builder().content("수정된 댓글").build());
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.ACCESS_FAIL_REPLY);
            assertThat(reply.getContent()).isEqualTo("testReplyContent");
            verify(replyRepository,times(1)).findById(any(Long.class));
            verify(replyRepository,times(0)).save(any(Reply.class));
        }
    }

    @Test
    @DisplayName("댓글 삭제 테스트 -> 성공")
    public void deleteReplySuccess(){
        //given
        given(replyRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(reply));
        given(user.getUserId()).willReturn(1L);

        //when
        replyService.deleteReply(1L,1L);

        //then
        verify(replyRepository,times(1)).findById(any(Long.class));
        verify(replyRepository,times(1)).delete(any(Reply.class));
    }

    @Test
    @DisplayName("댓글 삭제 테스트 -> (실패 : 존재하지 않는 댓글)")
    public void deleteReplyFail1(){
        //given
        given(replyRepository.findById(any(Long.class))).willReturn(Optional.empty());

        //when,then
        try{
            replyService.deleteReply(1L,1L);
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.NOT_EXIST_REPLY);
            verify(replyRepository,times(1)).findById(any(Long.class));
            verify(replyRepository,times(0)).delete(any(Reply.class));
        }
    }

    @Test
    @DisplayName("댓글 삭제 테스트 -> (실패 : 댓글 삭제권한 없음)")
    public void deleteReplyFail2(){
        //given
        given(replyRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(reply));
        given(user.getUserId()).willReturn(2L);

        //when,then
        try{
            replyService.deleteReply(1L,1L);
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.ACCESS_FAIL_REPLY);
            verify(replyRepository,times(1)).findById(any(Long.class));
            verify(replyRepository,times(0)).delete(any(Reply.class));
        }
    }

}
