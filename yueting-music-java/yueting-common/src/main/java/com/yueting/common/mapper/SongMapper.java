package com.yueting.common.mapper;
import com.yueting.entity.dto.SongQueryDTO;
import com.yueting.entity.po.Song;
import org.apache.ibatis.annotations.Param;
import java.util.List;
public interface SongMapper {
  List<Song> selectList(SongQueryDTO query);
  Song selectById(@Param("id") Long id);
  int insert(Song song);
  int update(Song song);
  int deleteById(@Param("id") Long id);
}