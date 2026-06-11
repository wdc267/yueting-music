package com.yueting.common.mapper;
import com.yueting.entity.dto.SysDictQueryDTO;
import com.yueting.entity.po.SysDict;
import org.apache.ibatis.annotations.Param;
import java.util.List;
public interface SysDictMapper {
  List<SysDict> selectList(SysDictQueryDTO query);
  SysDict selectById(@Param("id") Long id);
  List<SysDict> selectByParentId(@Param("parentId") Long parentId);
  SysDict selectByCode(@Param("dictCode") String dictCode);
  int insert(SysDict dict);
  int update(SysDict dict);
  int deleteById(@Param("id") Long id);
  int countByParentId(@Param("parentId") Long parentId);
}