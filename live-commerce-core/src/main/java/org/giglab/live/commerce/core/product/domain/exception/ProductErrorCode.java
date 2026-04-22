package org.giglab.live.commerce.core.product.domain.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.core.global.exception.DomainErrorCode;

@Getter
@RequiredArgsConstructor
public enum ProductErrorCode implements DomainErrorCode {
  PRODUCT_NOT_FOUND("PRODUCT-4401", "존재하지 않는 상품입니다."),
  PDF_NOT_FOUND("PRODUCT-4402", "PDF 파일이 존재하지 않습니다."),
  PDF_ALREADY_EMBEDDED("PRODUCT-4403", "이미 임베딩된 PDF 문서입니다."),
  PDF_NOT_LINKED_TO_PRODUCT("PRODUCT-4404", "상품에 연결되지 않은 PDF 문서는 임베딩할 수 없습니다."),
  PDF_ALREADY_LINKED_TO_PRODUCT("PRODUCT-4405", "이미 다른 상품에 연결된 PDF 문서입니다."),
  PDF_READ_FAILED("PRODUCT-5001", "PDF 파일 처리 중 오류가 발생했습니다.");

  private final String code;
  private final String message;
}
