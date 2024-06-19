package com.example.foodking.recipe;

import com.example.foodking.recipe.repository.RecipeInfoRepository;
import com.example.foodking.recipe.service.RecipeService;
import com.example.foodking.reply.common.ReplySortType;
import edu.emory.mathcs.backport.java.util.concurrent.ExecutorService;
import edu.emory.mathcs.backport.java.util.concurrent.Executors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;

import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
public class DistributedLockTest {

    @Autowired
    private RecipeService recipeService;
    @Autowired
    private RecipeInfoRepository recipeInfoRepository;

    @Test
    void 동시성100명_테스트() throws InterruptedException {
        int numberOfThreads = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    // 분산락 적용 메서드 호출
                    recipeService.readRecipe(1L,1L, ReplySortType.LATEST,any(),any());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
    }
}
