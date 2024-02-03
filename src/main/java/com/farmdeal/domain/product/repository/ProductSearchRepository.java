package com.farmdeal.domain.product.repository;


import com.farmdeal.domain.product.dto.ProductSearchDto;
import com.farmdeal.domain.product.dto.ProductResponseDto;
import com.farmdeal.domain.product.enums.ProductApprovalEnum;
import com.farmdeal.domain.product.model.Product;

import com.farmdeal.global.enums.ErrorCode;
import com.farmdeal.global.exception.CustomException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import static com.farmdeal.domain.product.enums.SortConditionEnum.*;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ProductSearchRepository {

    private static final String DEFAULT_SORT = PRODUCT_ID.getQuery();
    private static final String APPROVED = "'" + ProductApprovalEnum.APPROVED.name() + "'";

    private final EntityManager entityManager;

    public Slice<ProductResponseDto> findProductByFullTextSearch(
            Pageable pageable, ProductSearchDto productSearchDto) {

        // keyword 조건 검사 및 정렬 조건 체크
        // 검색 조건은 제목 또는 상품명을 필수적으로 선택하도록 한다.
        String column = validateSearchCondition(productSearchDto);
        // 정렬 조건을 가져온다. 지정하지 않았다면 상품번호로 정렬
        String sort = getSortCondition(productSearchDto);
        // 검색 키워드 설정. 검색 키워드가 없다면 예외 발생
        String keyword = validateKeyword(productSearchDto);
        log.info("keyword: {}", keyword);
        // 카테고리 조건 설정
        String categoryCondition = getCategoryCondition(productSearchDto);
        int paramIndex = categoryCondition.isEmpty() ? 3 : 4;

        String sql = createQueryBuilder(column, sort, categoryCondition);

        Query nativeQuery = executeQuery(sql, keyword, pageable, productSearchDto, paramIndex);

        List<Product> responses = nativeQuery.getResultList();
        // 다음페이지 확인
        boolean hasNext = responses.size() > pageable.getPageSize();
        // 다음페이지가 있다면 마지막 항목이 제거 되도록 설정
        if (hasNext) {
            responses.remove(responses.size() - 1);
        }

        List<ProductResponseDto> dtoList = responses.stream()
                .map(ProductResponseDto::getAllFromEntity)
                .collect(Collectors.toList());

        return new SliceImpl<>(dtoList, pageable, hasNext);
    }

    // 제목, 상품명 검색 기준 확인
    private String validateSearchCondition(ProductSearchDto productSearchDto) {
        return Optional.ofNullable(productSearchDto.getSearchCondition().getColumn())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_KEYWORD));
    }
    // sortCondition 체크, null이라면 id로 정렬되도록 설정
    private String getSortCondition(ProductSearchDto productSearchDto) {
        return productSearchDto.getSortCondition() != null ?
                productSearchDto.getSortCondition().getQuery() : DEFAULT_SORT;
    }
    // 검색 키워드가 없다면 예외 발생
    private String validateKeyword(ProductSearchDto productSearchDto) {
        String keyword = Optional.ofNullable(productSearchDto.getKeyword())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_KEYWORD));
        return getKeyword(keyword);
    }
    // 카테고리 설정 없이도 검색이 가능하도록 설정
    // 설정이 있다면 카테고리 조건을 추가
    private String getCategoryCondition(ProductSearchDto productSearchDto) {
        return productSearchDto.getProductCategory() == null ? "" : "AND category = ?4 ";
    }

    // SQL 쿼리 생성
    private String createQueryBuilder(String column, String sort, String categoryCondition) {
        return "SELECT * FROM product WHERE " +
                "MATCH(" + column + ") AGAINST(?1 IN BOOLEAN MODE) " +
                "AND product_approval = "+ APPROVED +
                categoryCondition + "ORDER BY " + sort + " " +
                "LIMIT ?3 OFFSET ?2";
    }
    // 쿼리를 실행, SQL인젝션 방지를 위해 파라미터 바인딩
    private Query executeQuery(String sql, String keyword, Pageable pageable,
                               ProductSearchDto productSearchDto, int paramIndex) {
        Query nativeQuery = entityManager.createNativeQuery(sql, Product.class);
        nativeQuery.setParameter(1, keyword);
        nativeQuery.setParameter(2, pageable.getOffset());
        nativeQuery.setParameter(3, pageable.getPageSize());

        if (paramIndex == 4) {
            nativeQuery.setParameter(paramIndex, productSearchDto.getProductCategory().name());
        }

        return nativeQuery;
    }
    // 검색 키워드를 ngram과 Boolean mode에서 사용이 용이한 형태로 변환
    // ex) "제주도 사과입니다." -> "+제주 +주도 +도* +사과 +입니 +니다"
    private String getKeyword(String keyword) {
        keyword = keyword.trim();
        String[] keywords = keyword.split(" ");
        StringBuilder ngramTokenBuilder = new StringBuilder();
        for (String token : keywords) {
            if (token.length() == 1) {
                ngramTokenBuilder.append("+").append(token).append("* ");
            } else if (token.length() == 2) {
                ngramTokenBuilder.append("+").append(token).append(" ");
            } else {
                for (int i = 0; i < token.length() - 1; i++) {
                    String ngramToken = token.substring(i, i + 2);
                    ngramTokenBuilder.append("+").append(ngramToken).append(" ");
                }
            }
        }
        return ngramTokenBuilder.toString().trim();
    }



}
