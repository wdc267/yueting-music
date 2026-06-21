package com.yueting.common.mapper;

import com.yueting.entity.po.UserLike;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface UserLikeMapper {
  int insert(UserLike like);
  int deleteByUserAndResource(@Param("userId") Long userId, @Param("resourceType") String resourceType, @Param("resourceId") Long resourceId);
  UserLike selectByUserAndResource(@Param("userId") Long userId, @Param("resourceType") String resourceType, @Param("resourceId") Long resourceId);
  List<UserLike> selectByUserIdAndType(@Param("userId") Long userId, @Param("resourceType") String resourceType);
  int countByResource(@Param("resourceType") String resourceType, @Param("resourceId") Long resourceId);
}