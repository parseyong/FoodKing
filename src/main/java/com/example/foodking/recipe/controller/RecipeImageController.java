package com.example.foodking.recipe.controller;

import com.example.foodking.common.CommonResDTO;
import com.example.foodking.recipe.service.RecipeImageService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Validated
@RequiredArgsConstructor
@Api(value = "RecipeImage")
public class RecipeImageController {

    private final RecipeImageService recipeImageService;

    @PostMapping("/recipes/images/{recipeInfoId}")
    public ResponseEntity<CommonResDTO> saveImage(
            @AuthenticationPrincipal final Long userId,
            @RequestParam(name = "recipeImage") MultipartFile recipeImage,
            @PathVariable final Long recipeInfoId) {

        recipeImageService.saveImage(recipeImage,recipeInfoId,userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResDTO.of("이미지 등록완료",null));
    }

    @DeleteMapping("/recipes/images/{recipeInfoId}")
    public ResponseEntity<CommonResDTO> deleteImage(
            @AuthenticationPrincipal final Long userId,
            @PathVariable final Long recipeInfoId) {

        recipeImageService.deleteImage(recipeInfoId,userId);
        return ResponseEntity.status(HttpStatus.OK).body(CommonResDTO.of("이미지 삭제완료",null));
    }
}
