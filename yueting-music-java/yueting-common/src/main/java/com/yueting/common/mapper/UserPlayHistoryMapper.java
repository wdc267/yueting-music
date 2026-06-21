package com.yueting.common.mapper;

import com.yueting.entity.po.UserPlayHistory;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface UserPlayHistoryMapper {
  int insert(UserPlayHistory history);
  List<UserPlayHistory> selectByUserId(@Param("userId") Long userId);
}