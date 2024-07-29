package com.piter.match.api.web;

public record RequestParams(
    int page,
    int size,
    Order order
) {

  public static final int DEFAULT_PAGE = 0;
  public static final int DEFAULT_SIZE = 50;

  public RequestParams {
    if (size < 0) {
      throw new IllegalArgumentException("Size must be greater than 0");
    }
    if (page < 0) {
      throw new IllegalArgumentException("Page must be greater than 0");
    }
  }

  public RequestParams(Integer page, Integer size, Order order) {
    this(page == null ? DEFAULT_PAGE : page, size == null ? DEFAULT_SIZE : size, order);
  }

  public enum Order {
    MATCH_TIME_ASC,
    MATCH_TIME_DESC,
    ROUND_TIME_ASC,
    ROUND_TIME_DESC
  }
}
