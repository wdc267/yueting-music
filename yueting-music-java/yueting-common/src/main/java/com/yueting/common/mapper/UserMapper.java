package com.yueting.common.mapper;

import com.yueting.entity.po.UserInfo;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
  UserInfo selectById(@Param("id") Long id);
  UserInfo selectByUsername(@Param("username") String username);
  int insert(UserInfo user);
  int update(UserInfo user);
}