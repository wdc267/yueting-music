package com.yueting.common.mapper;

import com.yueting.entity.dto.UserQueryDTO;
import com.yueting.entity.po.UserInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {
  UserInfo selectById(@Param("id") Long id);
  UserInfo selectByUsername(@Param("username") String username);
  int insert(UserInfo user);
  int update(UserInfo user);
  int updateAvatar(@Param("userId") Long userId, @Param("avatar") String avatar);
  List<UserInfo> selectList(@Param("query") UserQueryDTO query);
  int updatePassword(@Param("userId") Long userId, @Param("password") String password);
  int deleteById(@Param("id") Long id);
}
