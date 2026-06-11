package com.yueting.common.mapper;
import com.yueting.entity.dto.SingerQueryDTO;
import com.yueting.entity.po.Singer;
import org.apache.ibatis.annotations.Param;
import java.util.List;
public interface SingerMapper {
  List<Singer> selectList(SingerQueryDTO query);
  Singer selectById(@Param("id") Long id);
  int insert(Singer singer);
  int update(Singer singer);
  int updateSort(@Param("id") Long id, @Param("sortOrder") Integer sortOrder);
  int deleteById(@Param("id") Long id);
}