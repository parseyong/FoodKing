package com.example.foodking.reply.controller;

import com.example.foodking.common.CommonResDTO;
import com.example.foodking.reply.service.ReplyService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
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
public class ReplyController {

    private final ReplyService replyService;

    @PostMapping("/recipes/{recipeInfoId}/replies")
    public ResponseEntity<CommonResDTO> addReply(final @AuthenticationPrincipal Long userId,
                                                 final @PathVariable Long recipeInfoId,
                                                 @RequestParam @NotBlank(message = "댓글내용을 입력해주세요") String content){

        Long replyId = replyService.addReply(userId,recipeInfoId, content);
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResDTO.of("댓글 등록완료",replyId));
    }

    @PatchMapping("/replies/{replyId}")
    public ResponseEntity<CommonResDTO> updateReply(final @AuthenticationPrincipal Long userId,
                                                    final @PathVariable Long replyId,
                                                    @RequestParam @NotBlank(message = "댓글내용을 입력해주세요") String content){

        replyService.updateReply(userId,replyId, content);
        return ResponseEntity.status(HttpStatus.OK).body(CommonResDTO.of("댓글 수정완료",null));
    }

    @DeleteMapping ("/replies/{replyId}")
    public ResponseEntity<CommonResDTO> deleteReply(final @AuthenticationPrincipal Long userId,
                                                    final @PathVariable Long replyId){

        replyService.deleteReply(userId,replyId);
        return ResponseEntity.status(HttpStatus.OK).body(CommonResDTO.of("댓글 삭제완료",null));
    }
}
