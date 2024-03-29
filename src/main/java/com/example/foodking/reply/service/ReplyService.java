package com.example.foodking.reply.service;

import com.example.foodking.emotion.service.EmotionService;
import com.example.foodking.exception.CommondException;
import com.example.foodking.exception.ExceptionCode;
import com.example.foodking.recipe.domain.RecipeInfo;
import com.example.foodking.recipe.repository.RecipeInfoRepository;
import com.example.foodking.reply.common.ReplySortType;
import com.example.foodking.reply.domain.Reply;
import com.example.foodking.reply.dto.response.ReadReplyResDTO;
import com.example.foodking.reply.repository.ReplyRepository;
import com.example.foodking.user.domain.User;
import com.example.foodking.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final EmotionService emotionService;
    private final UserRepository userRepository;
    private final RecipeInfoRepository recipeInfoRepository;

    @Transactional
    public Long addReply(Long userId, Long recipeInfoId, String content){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommondException(ExceptionCode.NOT_EXIST_USER));
        RecipeInfo recipeInfo = recipeInfoRepository.findById(recipeInfoId)
                .orElseThrow(() -> new CommondException(ExceptionCode.NOT_EXIST_RECIPEINFO));

        Reply reply = Reply.builder()
                .content(content)
                .user(user)
                .recipeInfo(recipeInfo)
                .build();
        return replyRepository.save(reply).getReplyId();
    }

    public List<ReadReplyResDTO> readReply(RecipeInfo recipeInfo, Long userId, ReplySortType replySortType){
        List<Reply> replyList = recipeInfo.getReplyList();
        List<ReadReplyResDTO> readReplyResDTOList = replyList.stream()
                .map(entity -> {
                    Long replyEmotionCnt = emotionService.readReplyEmotionCnt(entity);
                    User user = entity.getUser();
                    if(isMyReply(userId,user))
                        return ReadReplyResDTO.toDTO(entity, user.getNickName(), true,replyEmotionCnt);
                    else
                        return ReadReplyResDTO.toDTO(entity, user.getNickName(), false,replyEmotionCnt);
                })
                .sorted(getComparator(replySortType))
                .collect(Collectors.toList());
        return  readReplyResDTOList;
    }

    @Transactional
    public void deleteReply(Long userId, Long replyId){
        Reply reply = findReplyById(replyId);

        if(!isMyReply(userId,reply.getUser()))
            throw new CommondException(ExceptionCode.ACCESS_FAIL_REPLY);;
        replyRepository.delete(reply);
    }

    @Transactional
    public void updateReply(Long userId, Long replyId, String content){
        Reply reply = findReplyById(replyId);

        if(!isMyReply(userId,reply.getUser()))
            throw new CommondException(ExceptionCode.ACCESS_FAIL_REPLY);;
        reply.changeContent(content);
        replyRepository.save(reply);
    }

    private Reply findReplyById(Long replyId){
        return replyRepository.findById(replyId)
                .orElseThrow(() -> new CommondException(ExceptionCode.NOT_EXIST_REPLY));
    }

    private boolean isMyReply(Long userId, User user){
        if( user ==null || !userId.equals(user.getUserId()) )
            return false;

        return true;
    }

    private Comparator<ReadReplyResDTO> getComparator(ReplySortType replySortType) {
        switch (replySortType) {
            case LIKE:
                return Comparator.comparing(ReadReplyResDTO::getEmotionCnt).reversed();
            // 다른 정렬 기준에 따른 case 추가
            default:
                return Comparator.comparing(ReadReplyResDTO::getRegDate);
        }
    }

}
