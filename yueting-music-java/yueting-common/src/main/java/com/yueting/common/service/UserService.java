package com.yueting.common.service;

import com.yueting.entity.dto.*;
import com.yueting.entity.vo.UserLoginVO;
import com.yueting.entity.vo.UserVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {
  UserLoginVO login(UserLoginDTO dto);
  UserVO register(UserRegisterDTO dto);
  UserVO getById(Long id);
  String updateAvatar(Long userId, MultipartFile file);
  List<UserVO> selectList(UserQueryDTO query);
  void update(UserUpdateDTO dto);
  void updatePassword(UserPasswordDTO dto);
  void delete(Long id);
}
