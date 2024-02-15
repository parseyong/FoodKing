package com.example.foodking.reply.controller;

import com.example.foodking.auth.JwtProvider;
import com.example.foodking.common.CommonResDTO;
import com.example.foodking.reply.service.ReplyService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;

@RestController
@Validated
@RequiredArgsConstructor
@Api(tags = "Reply")
public class ReplyController {

    private final ReplyService replyService;

    @PostMapping("/{recipeInfoId}/replys")
    public ResponseEntity<CommonResDTO> addReply(@PathVariable final Long recipeInfoId,
                                                  @RequestParam(name = "content") @NotBlank(message = "내용을 입력해주세요") String content){
        final Long userId = JwtProvider.getUserId();
        Long replyId = replyService.addReply(userId,recipeInfoId,content);
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResDTO.of("댓글 등록완료",replyId));
    }

    @PatchMapping("/replys/{replyId}")
    public ResponseEntity<CommonResDTO> updateReply(@PathVariable final Long replyId,
                                                    @RequestParam(name = "content") @NotBlank(message = "내용을 입력해주세요") String content){
        final Long userId = JwtProvider.getUserId();
        replyService.updateReply(userId,replyId,content);
        return ResponseEntity.status(HttpStatus.OK).body(CommonResDTO.of("댓글 수정완료",null));
    }

    @DeleteMapping ("/replys/{replyId}")
    public ResponseEntity<CommonResDTO> deleteReply(@PathVariable final Long replyId){
        final Long userId = JwtProvider.getUserId();
        replyService.deleteReply(userId,replyId);
        return ResponseEntity.status(HttpStatus.OK).body(CommonResDTO.of("댓글 삭제완료",null));
    }

}