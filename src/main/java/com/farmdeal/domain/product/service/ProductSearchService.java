package com.farmdeal.domain.product.service;


import com.farmdeal.domain.product.dto.ProductResponseDto;
import com.farmdeal.domain.product.dto.ProductSearchDto;
import com.farmdeal.domain.product.repository.ProductSearchRepository;
import com.farmdeal.global.dto.ApiResponseDto;
import com.farmdeal.global.enums.ErrorCode;
import com.farmdeal.global.enums.SuccessCode;
import com.farmdeal.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductSearchService {

    private final ProductSearchRepository productSearchRepository;


    public ApiResponseDto<Slice<ProductResponseDto>> searchProduct(
            Pageable pageable, ProductSearchDto productSearchDto) {
        // 공백제거

        Slice<ProductResponseDto> productResponseDtoList =
                productSearchRepository.findProductByFullTextSearch(pageable, productSearchDto);

        if(productResponseDtoList.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND_PRODUCT_SEARCH_RESULT);
        }
        return ApiResponseDto.success(SuccessCode.SUCCESS.getMessage(),
                productResponseDtoList);
    }
}
