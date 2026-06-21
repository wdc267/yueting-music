package com.yueting.common.mapper;

import com.yueting.entity.po.UserFavorite;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface UserFavoriteMapper {
  int insert(UserFavorite fav);
  int deleteByUserAndResource(@Param("userId") Long userId, @Param("resourceType") String resourceType, @Param("resourceId") Long resourceId);
  UserFavorite selectByUserAndResource(@Param("userId") Long userId, @Param("resourceType") String resourceType, @Param("resourceId") Long resourceId);
  List<UserFavorite> selectByUserIdAndType(@Param("userId") Long userId, @Param("resourceType") String resourceType);
  int countByResource(@Param("resourceType") String resourceType, @Param("resourceId") Long resourceId);
}