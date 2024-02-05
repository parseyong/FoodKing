package com.example.foodking.Recipe.RecipeInfo.DTO.Response;

import com.example.foodking.Recipe.RecipeInfo.RecipeInfo;
import com.example.foodking.Recipe.RecipeInfo.RecipeInfoType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
/*
    직렬화 과정에서 기본생성자가 없더라도 다른 생성자가 있다면 그 생성자를 이용하여 직렬화과정을 거친다
    그러나 Build를 Gradle로 하지않고 인텔리제이로 할 경우에는
    다른 생성자가 있더라도 기본생성자가 없으면 직렬화과정이 정상적으로 이루어지지 않는다.
*/
//@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReadRecipeInfoResDTO {

    private String recipeName;
    private RecipeInfoType recipeInfoType;
    private Long ingredentCost;
    private Long cookingTime;
    private Long calogy;
    private String recipeTip;
    private Long recipeInfoId;
    private String recipeImageUrl;

    public static ReadRecipeInfoResDTO toDTO(RecipeInfo recipeInfo){
        return ReadRecipeInfoResDTO.builder()
                .calogy(recipeInfo.getCalogy())
                .recipeInfoType(recipeInfo.getRecipeInfoType())
                .recipeName(recipeInfo.getRecipeName())
                .cookingTime(recipeInfo.getCookingTime())
                .recipeTip(recipeInfo.getRecipeTip())
                .ingredentCost(recipeInfo.getIngredientCost())
                .recipeInfoId(recipeInfo.getRecipeInfoId())
                .recipeImageUrl(recipeInfo.getRecipeImage())
                .build();
    }
}
