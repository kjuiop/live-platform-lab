package org.giglab.live.commerce.core.category.domain.exception;

import org.giglab.live.commerce.core.global.exception.DomainException;

public class CategoryDomainException extends DomainException {

  public CategoryDomainException(CategoryErrorCode errorCode) {
    super(errorCode);
  }
}
