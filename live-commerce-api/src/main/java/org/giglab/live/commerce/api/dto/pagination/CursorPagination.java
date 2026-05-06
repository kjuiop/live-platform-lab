package org.giglab.live.commerce.api.dto.pagination;

public interface CursorPagination {

  Long cursor();

  Integer size();

  default int sizeOrDefault() {
    return size() != null ? size() : 20;
  }
}
