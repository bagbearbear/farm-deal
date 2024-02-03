package com.farmdeal.domain.product.controller;

import com.farmdeal.domain.product.dto.ProductResponseDto;
import com.farmdeal.domain.product.dto.ProductSearchDto;
import com.farmdeal.domain.product.service.ProductSearchService;
import com.farmdeal.global.dto.ApiResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class ProductSearchController {

    private final ProductSearchService productSearchService;

    @GetMapping("/product")
    public ApiResponseDto<Slice<ProductResponseDto>> searchProduct(@RequestParam int page, @RequestParam int size,
                                                                  @RequestPart(value = "searchCondition", required = false)
                                                            ProductSearchDto productSearchDto) {
        Pageable pageable = PageRequest.of(page, size);

        return productSearchService.searchProduct(pageable, productSearchDto);
    }
}
