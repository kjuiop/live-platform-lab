package org.giglab.live.commerce.api.dto.pagination;

public interface OffsetPagination {

  Integer page();

  Integer size();

  default int pageOrDefault() {
    return page() != null ? page() : 1;
  }

  default int sizeOrDefault() {
    return size() != null ? size() : 20;
  }
}
