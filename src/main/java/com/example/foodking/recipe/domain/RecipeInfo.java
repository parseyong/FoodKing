package com.example.foodking.recipe.domain;

import com.example.foodking.common.TimeEntity;
import com.example.foodking.recipe.common.RecipeInfoType;
import com.example.foodking.reply.domain.Reply;
import com.example.foodking.user.domain.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecipeInfo extends TimeEntity {

    @Id
    @Column(name = "recipe_info_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recipeInfoId;

    @Column(nullable = false, name = "recipe_name")
    private String recipeName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "recipe_type")
    private RecipeInfoType recipeInfoType;

    @Column(nullable = false, name = "ingredient_cost")
    private Long ingredientCost;

    @Column(nullable = false, name = "cooking_time")
    private Long cookingTime;

    @Column(nullable = false)
    private Long calogy;

    @Column(name = "recipe_image")
    private String recipeImage;

    @Column(nullable = false, name = "recipe_tip")
    private String recipeTip;

    @Column(nullable = false, name = "visit_cnt")
    private Long visitCnt;

    @OneToMany(mappedBy = "recipeInfo", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<Reply> replyList;

    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @OneToMany(mappedBy = "recipeInfo", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)
    private List<RecipeWayInfo> recipeWayInfoList;

    @OneToMany(mappedBy = "recipeInfo", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)
    private List<Ingredient> ingredientList;

    @Builder
    public RecipeInfo(String recipeName, RecipeInfoType recipeInfoType, Long ingredientCost, Long cookingTime, Long calogy, String recipeTip,
                      User user, List<RecipeWayInfo> recipeWayInfoList, List<Ingredient> ingredientList){
        this.recipeName=recipeName;
        this.recipeInfoType=recipeInfoType;
        this.ingredientCost=ingredientCost;
        this.cookingTime=cookingTime;
        this.calogy=calogy;
        this.recipeTip=recipeTip;
        this.user=user;
        this.recipeWayInfoList=recipeWayInfoList;
        this.ingredientList=ingredientList;
        this.visitCnt = 0L;
    }
    public void changeRecipeName(String recipeName){
        this.recipeName = recipeName;
    }
    public void changeRecipeInfoType(RecipeInfoType recipeInfoType){
        this.recipeInfoType = recipeInfoType;
    }
    public void changeIngredientCost(Long ingredientCost){
        this.ingredientCost = ingredientCost;
    }
    public void changeCookingTime(Long cookingTime){
        this.cookingTime = cookingTime;
    }
    public void changeCalogy(Long calogy){
        this.calogy = calogy;
    }
    public void addRecipeImage(String recipeImage){
        this.recipeImage=recipeImage;
    }
    public void deleteRecipeImage(){
        this.recipeImage=null;
    }
    public void changeRecipeTip(String recipeTip){
        this.recipeTip=recipeTip;
    }
    public void changeRecipeWayInfoList(List<RecipeWayInfo> recipeWayInfoList){
        this.recipeWayInfoList=recipeWayInfoList;
    }
    public void changeIngredientList(List<Ingredient> ingredientList){
        this.ingredientList=ingredientList;
    }

    public void addVisitCnt(){
        this.visitCnt += 1;
    }
}
