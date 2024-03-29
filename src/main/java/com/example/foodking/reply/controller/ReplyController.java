package com.example.foodking.reply.controller;

import com.example.foodking.common.CommonResDTO;
import com.example.foodking.reply.service.ReplyService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;

@RestController
@Validated
@RequiredArgsConstructor
@Api(tags = "Reply")
@Log4j2
public class ReplyController {

    private final ReplyService replyService;

    @PostMapping("/{recipeInfoId}/replys")
    public ResponseEntity<CommonResDTO> addReply(
            @AuthenticationPrincipal final Long userId,
            @PathVariable final Long recipeInfoId,
            @RequestParam(name = "content") @NotBlank(message = "댓글내용을 입력해주세요") String content){

        Long replyId = replyService.addReply(userId,recipeInfoId,content);
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResDTO.of("댓글 등록완료",replyId));
    }

    @PatchMapping("/replys/{replyId}")
    public ResponseEntity<CommonResDTO> updateReply(
            @AuthenticationPrincipal final Long userId,
            @PathVariable final Long replyId,
            @RequestParam(name = "content") @NotBlank(message = "댓글내용을 입력해주세요") String content){

        replyService.updateReply(userId,replyId,content);
        return ResponseEntity.status(HttpStatus.OK).body(CommonResDTO.of("댓글 수정완료",null));
    }

    @DeleteMapping ("/replys/{replyId}")
    public ResponseEntity<CommonResDTO> deleteReply(
            @AuthenticationPrincipal final Long userId,
            @PathVariable final Long replyId){

        log.info(userId);
        replyService.deleteReply(userId,replyId);
        return ResponseEntity.status(HttpStatus.OK).body(CommonResDTO.of("댓글 삭제완료",null));
    }
}
