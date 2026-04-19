package org.giglab.live.commerce.core.loader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.giglab.live.commerce.core.category.domain.entity.Category;
import org.giglab.live.commerce.core.category.infrastructure.persistence.CategoryRepository;
import org.giglab.live.commerce.core.global.jpa.entity.types.YnType;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@Profile({"local", "dev"})
@RequiredArgsConstructor
public class TestDataLoader implements ApplicationRunner {

  private final CategoryRepository categoryRepository;

  @Override
  @Transactional
  public void run(ApplicationArguments args) {
    ensureCategories();
  }

  /** 카테고리 20건 (3 depth): 없을 때만 생성. 기존 init-data.sql과 동일 구조. */
  private void ensureCategories() {
    if (categoryRepository.count() > 0) {
      return;
    }
    // 1 depth (4)
    Category catApparel =
        categoryRepository.save(
            Category.builder()
                .code("CAT_APPAREL")
                .parentCodePath(null)
                .name("의류")
                .level(1)
                .sortOrder(1)
                .activeYn(YnType.Y)
                .deleteYn(YnType.N)
                .build());
    Category catElectronics =
        categoryRepository.save(
            Category.builder()
                .code("CAT_ELECTRONICS")
                .parentCodePath(null)
                .name("가전디지털")
                .level(1)
                .sortOrder(2)
                .activeYn(YnType.Y)
                .deleteYn(YnType.N)
                .build());
    Category catFood =
        categoryRepository.save(
            Category.builder()
                .code("CAT_FOOD")
                .parentCodePath(null)
                .name("식품")
                .level(1)
                .sortOrder(3)
                .activeYn(YnType.Y)
                .deleteYn(YnType.N)
                .build());
    Category catLiving =
        categoryRepository.save(
            Category.builder()
                .code("CAT_LIVING")
                .parentCodePath(null)
                .name("생활건강")
                .level(1)
                .sortOrder(4)
                .activeYn(YnType.Y)
                .deleteYn(YnType.N)
                .build());
    // 2 depth (8)
    Category catApparelMen =
        categoryRepository.save(
            Category.builder()
                .code("CAT_APPAREL_MEN")
                .parentCodePath("/CAT_APPAREL")
                .name("남성의류")
                .level(2)
                .sortOrder(1)
                .activeYn(YnType.Y)
                .deleteYn(YnType.N)
                .parent(catApparel)
                .build());
    categoryRepository.save(
        Category.builder()
            .code("CAT_APPAREL_WOMEN")
            .parentCodePath("/CAT_APPAREL")
            .name("여성의류")
            .level(2)
            .sortOrder(2)
            .activeYn(YnType.Y)
            .deleteYn(YnType.N)
            .parent(catApparel)
            .build());
    Category catElectronicsLarge =
        categoryRepository.save(
            Category.builder()
                .code("CAT_ELECTRONICS_LARGE")
                .parentCodePath("/CAT_ELECTRONICS")
                .name("대형가전")
                .level(2)
                .sortOrder(1)
                .activeYn(YnType.Y)
                .deleteYn(YnType.N)
                .parent(catElectronics)
                .build());
    Category catElectronicsSmall =
        categoryRepository.save(
            Category.builder()
                .code("CAT_ELECTRONICS_SMALL")
                .parentCodePath("/CAT_ELECTRONICS")
                .name("소형가전")
                .level(2)
                .sortOrder(2)
                .activeYn(YnType.Y)
                .deleteYn(YnType.N)
                .parent(catElectronics)
                .build());
    categoryRepository.save(
        Category.builder()
            .code("CAT_FOOD_FRESH")
            .parentCodePath("/CAT_FOOD")
            .name("신선식품")
            .level(2)
            .sortOrder(1)
            .activeYn(YnType.Y)
            .deleteYn(YnType.N)
            .parent(catFood)
            .build());
    categoryRepository.save(
        Category.builder()
            .code("CAT_FOOD_PROCESSED")
            .parentCodePath("/CAT_FOOD")
            .name("가공식품")
            .level(2)
            .sortOrder(2)
            .activeYn(YnType.Y)
            .deleteYn(YnType.N)
            .parent(catFood)
            .build());
    Category catLivingGoods =
        categoryRepository.save(
            Category.builder()
                .code("CAT_LIVING_GOODS")
                .parentCodePath("/CAT_LIVING")
                .name("생활용품")
                .level(2)
                .sortOrder(1)
                .activeYn(YnType.Y)
                .deleteYn(YnType.N)
                .parent(catLiving)
                .build());
    categoryRepository.save(
        Category.builder()
            .code("CAT_LIVING_HEALTH")
            .parentCodePath("/CAT_LIVING")
            .name("헬스/건강")
            .level(2)
            .sortOrder(2)
            .activeYn(YnType.Y)
            .deleteYn(YnType.N)
            .parent(catLiving)
            .build());
    // 3 depth (8)
    categoryRepository.save(
        Category.builder()
            .code("CAT_APPAREL_MEN_SHIRT")
            .parentCodePath("/CAT_APPAREL/CAT_APPAREL_MEN")
            .name("셔츠")
            .level(3)
            .sortOrder(1)
            .activeYn(YnType.Y)
            .deleteYn(YnType.N)
            .parent(catApparelMen)
            .build());
    categoryRepository.save(
        Category.builder()
            .code("CAT_APPAREL_MEN_PANTS")
            .parentCodePath("/CAT_APPAREL/CAT_APPAREL_MEN")
            .name("바지")
            .level(3)
            .sortOrder(2)
            .activeYn(YnType.Y)
            .deleteYn(YnType.N)
            .parent(catApparelMen)
            .build());
    categoryRepository.save(
        Category.builder()
            .code("CAT_APPAREL_WOMEN_DRESS")
            .parentCodePath("/CAT_APPAREL/CAT_APPAREL_WOMEN")
            .name("원피스")
            .level(3)
            .sortOrder(1)
            .activeYn(YnType.Y)
            .deleteYn(YnType.N)
            .parent(categoryRepository.findByCode("CAT_APPAREL_WOMEN").orElseThrow())
            .build());
    categoryRepository.save(
        Category.builder()
            .code("CAT_ELECTRONICS_LARGE_FRIDGE")
            .parentCodePath("/CAT_ELECTRONICS/CAT_ELECTRONICS_LARGE")
            .name("냉장고")
            .level(3)
            .sortOrder(1)
            .activeYn(YnType.Y)
            .deleteYn(YnType.N)
            .parent(catElectronicsLarge)
            .build());
    categoryRepository.save(
        Category.builder()
            .code("CAT_ELECTRONICS_LARGE_TV")
            .parentCodePath("/CAT_ELECTRONICS/CAT_ELECTRONICS_LARGE")
            .name("TV")
            .level(3)
            .sortOrder(2)
            .activeYn(YnType.Y)
            .deleteYn(YnType.N)
            .parent(catElectronicsLarge)
            .build());
    categoryRepository.save(
        Category.builder()
            .code("CAT_ELECTRONICS_SMALL_MICROWAVE")
            .parentCodePath("/CAT_ELECTRONICS/CAT_ELECTRONICS_SMALL")
            .name("전자레인지")
            .level(3)
            .sortOrder(1)
            .activeYn(YnType.Y)
            .deleteYn(YnType.N)
            .parent(catElectronicsSmall)
            .build());
    categoryRepository.save(
        Category.builder()
            .code("CAT_FOOD_FRESH_MEAT")
            .parentCodePath("/CAT_FOOD/CAT_FOOD_FRESH")
            .name("육류")
            .level(3)
            .sortOrder(1)
            .activeYn(YnType.Y)
            .deleteYn(YnType.N)
            .parent(categoryRepository.findByCode("CAT_FOOD_FRESH").orElseThrow())
            .build());
    categoryRepository.save(
        Category.builder()
            .code("CAT_LIVING_GOODS_CLEAN")
            .parentCodePath("/CAT_LIVING/CAT_LIVING_GOODS")
            .name("세제/청소")
            .level(3)
            .sortOrder(1)
            .activeYn(YnType.Y)
            .deleteYn(YnType.N)
            .parent(catLivingGoods)
            .build());
    log.debug("카테고리 시드 20건 생성 완료");
  }
}
