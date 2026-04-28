package org.giglab.live.commerce.core.category.domain.exception;

import org.giglab.live.commerce.core.global.exception.DomainException;
import org.giglab.live.commerce.core.product.domain.exception.ProductErrorCode;

public class CategoryDomainException extends DomainException {

  public CategoryDomainException(CategoryErrorCode errorCode) {
    super(errorCode);
  }

  public CategoryDomainException(ProductErrorCode errorCode, String message) {
    super(errorCode, message);
  }
}
