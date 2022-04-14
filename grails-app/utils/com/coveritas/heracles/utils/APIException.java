package com.coveritas.heracles.utils;

import java.sql.SQLException;

public class APIException extends Exception {
  private int httpStatus = 400;

  public APIException(String message) {
    super(message);
  }

  public APIException(String message, int httpStatus) {
    super(message);
    this.httpStatus = httpStatus;
  }

  public APIException(String message, SQLException e) {
    super(message, e);
  }

  public int getHttpStatus() {
    return httpStatus;
  }

  public void setHttpStatus(int httpStatus) {
    this.httpStatus = httpStatus;
  }
}
