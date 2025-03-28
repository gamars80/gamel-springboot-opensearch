package com.example.gamelspringbootopensearch.component;


import com.example.gamelspringbootopensearch.entity.*;
import com.example.gamelspringbootopensearch.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductCategoryMappingRepository productCategoryMappingRepository;
    private final ProductPriceRepository productPriceRepository;
    private final ProductHashtagRepository productHashtagRepository;
    private final ProductHashtagMappingRepository productHashtagMappingRepository;

    public DataInitializer(ProductRepository productRepository,
                           CategoryRepository categoryRepository,
                           ProductCategoryMappingRepository productCategoryMappingRepository,
                           ProductPriceRepository productPriceRepository,
                           ProductHashtagRepository productHashtagRepository,
                           ProductHashtagMappingRepository productHashtagMappingRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productCategoryMappingRepository = productCategoryMappingRepository;
        this.productPriceRepository = productPriceRepository;
        this.productHashtagRepository = productHashtagRepository;
        this.productHashtagMappingRepository = productHashtagMappingRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 카테고리 2개 생성
        Category category1 = new Category();
        category1.setName("Category 1");
        categoryRepository.save(category1);

        Category category2 = new Category();
        category2.setName("Category 2");
        categoryRepository.save(category2);

        // 해시태그 20개 생성
        List<ProductHashtag> hashtags = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            ProductHashtag tag = new ProductHashtag();
            tag.setTagName("Tag " + i);
            productHashtagRepository.save(tag);
            hashtags.add(tag);
        }

        // 상품 10개 생성 (가격, 카테고리 매핑, 해시태그 매핑 포함)
        for (int i = 1; i <= 10; i++) {
            Product product = new Product();
            product.setName("Product " + i);
            product.setDescription("Description for product " + i);
            productRepository.save(product);

            // 상품 가격 생성
            ProductPrice productPrice = new ProductPrice();
            productPrice.setPrice(new BigDecimal("100.00").add(new BigDecimal(i)));
            productPrice.setProduct(product);
            productPriceRepository.save(productPrice);

            // 카테고리 매핑 (1~5: category1, 6~10: category2)
            ProductCategoryMapping categoryMapping = new ProductCategoryMapping();
            categoryMapping.setProduct(product);
            categoryMapping.setCategory(i <= 5 ? category1 : category2);
            productCategoryMappingRepository.save(categoryMapping);

            // 해시태그 매핑 (1~5번 상품: 1~10번 태그, 6~10번 상품: 11~20번 태그)
            List<ProductHashtagMapping> hashtagMappings = new ArrayList<>();
            if (i <= 5) {
                for (int j = 0; j < 10; j++) {
                    ProductHashtagMapping mapping = new ProductHashtagMapping();
                    mapping.setProduct(product);
                    mapping.setHashtag(hashtags.get(j)); // index 0~9
                    productHashtagMappingRepository.save(mapping);
                    hashtagMappings.add(mapping);
                }
            } else {
                for (int j = 10; j < 20; j++) {
                    ProductHashtagMapping mapping = new ProductHashtagMapping();
                    mapping.setProduct(product);
                    mapping.setHashtag(hashtags.get(j)); // index 10~19
                    productHashtagMappingRepository.save(mapping);
                    hashtagMappings.add(mapping);
                }
            }
            product.setHashtagMappings(hashtagMappings);
            productRepository.save(product);
        }
    }
}