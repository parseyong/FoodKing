package com.example.foodking.recipe.service;

import com.example.foodking.exception.CommondException;
import com.example.foodking.exception.ExceptionCode;
import com.example.foodking.ingredient.dto.response.IngredientFindRes;
import com.example.foodking.ingredient.repository.IngredientRepository;
import com.example.foodking.recipe.domain.RecipeInfo;
import com.example.foodking.recipe.dto.recipe.response.RecipeFindRes;
import com.example.foodking.recipe.dto.recipeInfo.response.RecipeInfoFindRes;
import com.example.foodking.recipe.repository.RecipeInfoRepository;
import com.example.foodking.recipeWay.dto.response.RecipeWayFindRes;
import com.example.foodking.recipeWay.repository.RecipeWayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecipeCachingService {

    private final RecipeInfoRepository recipeInfoRepository;
    private final RecipeWayRepository recipeWayRepository;
    private final IngredientRepository ingredientRepository;

    @Qualifier("cacheRedis")
    private final RedisTemplate<String,String> cacheRedisTemplate;

    private final RedisCacheManager redisCacheManager;
    
    // 1차캐시 유지를 위한 트랜잭션 선언
    @Transactional(readOnly = true)
    @Cacheable(value = "recipeInfoCache", key = "#recipeInfoId", cacheManager = "redisCacheManager", condition = "#notReIssue")
    public Object findRecipeByCache(Long recipeInfoId, boolean notReIssue){

        // 레시피 정보 가져오기
        RecipeInfoFindRes recipeInfoFindRes = recipeInfoRepository.findRecipeInfo(recipeInfoId);
        RecipeInfo recipeInfo = recipeInfoRepository.findById(recipeInfoId)
                .orElseThrow(() -> new CommondException(ExceptionCode.NOT_EXIST_RECIPEINFO));

        // 조리법 리스트 가져오기
        List<RecipeWayFindRes> recipeWayFindResList = recipeWayRepository.findAllByRecipeInfo(recipeInfo).stream()
                .map(entity -> RecipeWayFindRes.toDTO(entity))
                .collect(Collectors.toList());

        // 재료 리스트 가져오기
        List<IngredientFindRes> ingredientFindResList = ingredientRepository.findAllByRecipeInfo(recipeInfo).stream()
                .map(entity -> IngredientFindRes.toDTO(entity))
                .collect(Collectors.toList());

        return RecipeFindRes.builder()
                .recipeInfoFindRes(recipeInfoFindRes)
                .recipeWayFindResList(recipeWayFindResList)
                .ingredientFindResList(ingredientFindResList)
                .isMyRecipe(true) // 직렬화 오류해결을 위해 임시값 삽입
                .build();
    }

    @Scheduled(cron = "0 5 * * * *") // 5분마다 캐시데이터 갱신
    public void reissueCache(){

        String cacheName = "recipeInfoCache"; // 캐시 이름

        // Redis 키 스캔 옵션 설정
        ScanOptions options = ScanOptions.scanOptions().match(cacheName + "::*").count(100).build();
        Set<String> processedKeys = new HashSet<>(); // 중복 키 처리를 위한 Set

        try (Cursor<byte[]> cursor = cacheRedisTemplate.executeWithStickyConnection(redisConnection -> redisConnection.scan(options))) {
            while (cursor.hasNext()) {
                String cacheKey = new String(cursor.next());
                if (processedKeys.contains(cacheKey)) {
                    continue; // 이미 처리된 키 생략
                }
                processedKeys.add(cacheKey);
                
                // recipeInfoId 추출
                String recipeInfoId[] = cacheKey.split("::");

                // 캐시 데이터를 갱신
                RecipeFindRes recipeFindRes = (RecipeFindRes) findRecipeByCache(Long.valueOf(recipeInfoId[1]),false);

                // 캐시 갱신 로직 예시: 캐시 매니저를 사용하여 캐시를 업데이트합니다.
                redisCacheManager.getCache(cacheName).put(cacheKey, recipeFindRes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
