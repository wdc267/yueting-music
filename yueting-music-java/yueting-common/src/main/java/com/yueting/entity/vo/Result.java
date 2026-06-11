package com.yueting.entity.vo;
import lombok.Data;
@Data
public class Result<T> {
  private int code;private String msg;private T data;
  public static <T> Result<T> success(T d) { Result<T> r = new Result<>(); r.code = 200; r.msg = "success"; r.data = d; return r; }
  public static <T> Result<T> success() { return success(null); }
  public static <T> Result<T> error(int c, String m) { Result<T> r = new Result<>(); r.code = c; r.msg = m; return r; }
}