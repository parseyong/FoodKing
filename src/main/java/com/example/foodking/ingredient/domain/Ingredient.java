package com.example.foodking.ingredient.domain;

import com.example.foodking.recipe.domain.RecipeInfo;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ingredient {

    @Id
    @Column(name = "ingredient_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ingredientId;

    @Column(nullable = false, name = "ingredient_name")
    private String ingredientName;

    @Column(nullable = false, name = "ingredient_amount")
    private String ingredientAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "recipe_Info_id",nullable = false)
    private RecipeInfo recipeInfo;

    @Builder
    private Ingredient(String ingredientName, String ingredientAmount, RecipeInfo recipeInfo){
        this.ingredientName=ingredientName;
        this.ingredientAmount=ingredientAmount;
        this.recipeInfo=recipeInfo;
    }
    public void updateIngredientName(String ingredientName){
        this.ingredientName=ingredientName;
    }
    public void updateIngredientAmount(String ingredientAmount){
        this.ingredientAmount=ingredientAmount;
    }
}
