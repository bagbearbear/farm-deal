package com.farmdeal.domain.product.controller;

import com.farmdeal.domain.product.enums.ProductCategoryEnum;
import com.farmdeal.domain.product.dto.ProductRequestDto;
import com.farmdeal.domain.product.dto.ProductResponseDto;
import com.farmdeal.domain.product.service.ProductService;
import com.farmdeal.global.dto.ApiResponseDto;
import com.farmdeal.global.dto.MessageResponseDto;
import com.farmdeal.global.external.AwsS3UploadService;
import com.farmdeal.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/product")
@Tag(name = "상품관리 API", description = "상품글 생성, 전체 상품글 조회, 상세 상품글 조회, 상품글 수정, 상품글 삭제")
public class ProductController {

  private final ProductService productService;
  private final AwsS3UploadService s3Service;


  // 게시글 작성
  @PostMapping
  @Operation(summary = "게시글 생성", description = "게시글 생성")
  @Parameter(name = "postRequestDto", description = "게시글 생성 정보", required = true)
  @Parameter(name = "userDetails", description = "게시글을 생성할 사용자의 정보", required = true)
  @Parameter(name = "multipartFiles", description = "게시글에 첨부할 이미지 파일(다중)", required = true)
  public ApiResponseDto<ProductResponseDto> createProduct(@Valid @RequestPart(value = "data", required = false) final ProductRequestDto productRequestDto,
                                                          @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                          @NotNull @RequestPart(value = "imageUrl", required = false) final List<MultipartFile> multipartFiles) {

    List<String> imgPaths = s3Service.upload(multipartFiles);
    return productService.createProduct(productRequestDto, userDetails.getUser(), imgPaths);
  }


  // 상세 게시글 가져오기
  @GetMapping("/{productId}")
  @Operation(summary = "상세 게시글 조회", description = "상세 게시글 조회")
  @Parameter(name = "postId", description = "조회할 게시글의 id", required = true)
  public ApiResponseDto<ProductResponseDto> getProduct(@PathVariable final Long productId) {
    return productService.getProduct(productId);
  }


  // 게시글 수정
  @PatchMapping("/{productId}")
  @Operation(summary = "게시글 수정", description = "게시글 수정")
  @Parameter(name = "postId", description = "수정할 게시글의 id", required = true)
  @Parameter(name = "postRequestDto", description = "게시글 수정 정보", required = true)
  @Parameter(name = "userDetails", description = "게시글을 수정할 사용자의 정보", required = true)
  @Parameter(name = "multipartFiles", description = "게시글에 첨부할 이미지 파일(다중)", required = true)
  public ApiResponseDto<ProductResponseDto> updateProduct(@PathVariable final Long productId,
                                                       @Valid@RequestPart(value = "data") final ProductRequestDto requestDto,
                                                       @RequestPart("imageUrl") final List<MultipartFile> multipartFiles,
                                                       @AuthenticationPrincipal UserDetailsImpl userDetails) {

    List<String> imgPaths = s3Service.upload(multipartFiles);
    return productService.updateProduct(productId, requestDto, userDetails.getUser(), imgPaths);
  }

  //게시글 삭제
  @DeleteMapping("/{productId}")
  @Operation(summary = "게시글 삭제", description = "게시글 삭제")
  @Parameter(name = "postId", description = "삭제할 게시글의 id", required = true)
  @Parameter(name = "userDetails", description = "게시글을 삭제할 사용자의 정보", required = true)
  public ApiResponseDto<MessageResponseDto> deleteProduct(@PathVariable final Long productId,
                                                       @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return productService.deleteProduct(productId, userDetails.getUser());
  }

}

