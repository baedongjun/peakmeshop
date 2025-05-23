package com.peakmeshop.api.mapper;

import com.peakmeshop.api.dto.ProductDTO;
import com.peakmeshop.api.dto.ProductOptionValueDTO;
import com.peakmeshop.api.dto.ProductSummaryDTO;
import com.peakmeshop.domain.entity.*;
import org.mapstruct.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {BaseMapper.class, CategoryMapper.class, BrandMapper.class, ProductOptionMapper.class, ProductOptionValueMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface ProductMapper {

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "categorySlug", source = "category.slug")
    @Mapping(target = "brandId", source = "brand.id")
    @Mapping(target = "brandName", source = "brand.name")
    @Mapping(target = "inventoryId", source = "inventory.id")
    @Mapping(target = "supplierId", source = "supplier.id")
    @Mapping(target = "supplierName", source = "supplier.name")
    @Mapping(target = "options", source = "options")
    ProductDTO toDTO(Product product);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "brand", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "inventory", ignore = true)
    @Mapping(target = "options", ignore = true)
    Product toEntity(ProductDTO dto);

    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "brandName", source = "brand.name")
    ProductSummaryDTO toSummaryDTO(Product product);

    @AfterMapping
    default void afterToDTO(@MappingTarget ProductDTO target, Product source) {
        if (source.getImages() != null && !source.getImages().isEmpty()) {
            target.setMainImage(source.getMainImage());
        }

        if (source.getOptions() != null) {
            List<ProductOptionValueDTO> colorOptions = new ArrayList<>();
            List<ProductOptionValueDTO> sizeOptions = new ArrayList<>();

            for (ProductOption option : source.getOptions()) {
                if (!option.isEnabled()) continue;

                String name = option.getDisplayName() != null ? option.getDisplayName() : option.getName();
                if (option.getOptionValues() == null) continue;

                for (ProductOptionValue value : option.getOptionValues()) {
                    if (!value.isEnabled()) continue;

                    ProductOptionValueDTO dto = ProductOptionValueDTO.builder()
                            .id(value.getId())
                            .optionId(option.getId())
                            .name(name)
                            .value(value.getValue())
                            .stock(value.getStock())
                            .sku(value.getSku())
                            .isActive(value.isActive())
                            .additionalPrice(value.getAdditionalPrice())
                            .enabled(value.isEnabled())
                            .build();

                    if ("색상".equalsIgnoreCase(name) || "color".equalsIgnoreCase(name)) {
                        colorOptions.add(dto);
                    } else if ("사이즈".equalsIgnoreCase(name) || "size".equalsIgnoreCase(name)) {
                        sizeOptions.add(dto);
                    }
                }
            }

            target.setColorOptions(colorOptions);
            target.setSizeOptions(sizeOptions);
        }

        List<Integer> ratingCounts = new ArrayList<>(Arrays.asList(0, 0, 0, 0, 0));
        if (source.getReviews() != null) {
            for (Review productReview : source.getReviews()) {
                int rating = productReview.getRating(); // 1~5
                if (rating >= 1 && rating <= 5) {
                    ratingCounts.set(rating - 1, ratingCounts.get(rating - 1) + 1);
                }
            }
        }

        target.setRatingCounts(ratingCounts);
    }
} 