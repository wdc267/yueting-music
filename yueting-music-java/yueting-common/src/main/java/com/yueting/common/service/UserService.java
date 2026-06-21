package com.yueting.common.service;

import com.yueting.entity.dto.UserLoginDTO;
import com.yueting.entity.dto.UserRegisterDTO;
import com.yueting.entity.vo.UserLoginVO;
import com.yueting.entity.vo.UserVO;

public interface UserService {
  UserLoginVO login(UserLoginDTO dto);
  UserVO register(UserRegisterDTO dto);
  UserVO getById(Long id);
}