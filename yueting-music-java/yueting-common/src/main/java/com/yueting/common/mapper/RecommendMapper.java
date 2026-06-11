package com.yueting.common.mapper;
import com.yueting.entity.dto.RecommendQueryDTO;
import com.yueting.entity.po.Recommend;
import org.apache.ibatis.annotations.Param;
import java.util.List;
public interface RecommendMapper {
  List<Recommend> selectList(RecommendQueryDTO query);
  Recommend selectById(@Param("id") Long id);
  int insert(Recommend rec);
  int update(Recommend rec);
  int deleteById(@Param("id") Long id);
}