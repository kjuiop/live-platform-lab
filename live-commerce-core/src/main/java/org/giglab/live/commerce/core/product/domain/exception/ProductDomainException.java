package org.giglab.live.commerce.core.product.domain.exception;

import org.giglab.live.commerce.core.global.exception.DomainException;

public class ProductDomainException extends DomainException {

  public ProductDomainException(ProductErrorCode errorCode) {
    super(errorCode);
  }

  public ProductDomainException(ProductErrorCode errorCode, String message) {
    super(errorCode, message);
  }
}
