package com.yueting.common.service.impl;

import com.yueting.common.mapper.UserMapper;
import com.yueting.common.service.UserService;
import com.yueting.entity.dto.UserLoginDTO;
import com.yueting.entity.dto.UserRegisterDTO;
import com.yueting.entity.po.UserInfo;
import com.yueting.entity.vo.UserLoginVO;
import com.yueting.entity.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

  private final UserMapper userMapper;

  public UserServiceImpl(UserMapper userMapper) {
    this.userMapper = userMapper;
  }

  @Override
  public UserLoginVO login(UserLoginDTO dto) {
    UserInfo user = userMapper.selectByUsername(dto.getUsername());
    if (user == null) {
      throw new RuntimeException("用户不存在");
    }
    if (user.getStatus() == 0) {
      throw new RuntimeException("账号已被禁用");
    }
    // 简单密码校验（生产环境应使用 BCrypt 等加密）
    if (!dto.getPassword().equals(user.getPassword())) {
      throw new RuntimeException("密码错误");
    }
    // 更新最后登录时间
    user.setLastLoginTime(LocalDateTime.now());
    userMapper.update(user);

    UserLoginVO vo = new UserLoginVO();
    vo.setUser(toVO(user));
    // 简单 token：实际应用应使用 JWT
    vo.setToken(UUID.randomUUID().toString().replace("-", ""));
    return vo;
  }

  @Override
  @Transactional
  public UserVO register(UserRegisterDTO dto) {
    // 检查用户名是否已存在
    UserInfo exist = userMapper.selectByUsername(dto.getUsername());
    if (exist != null) {
      throw new RuntimeException("用户名已存在");
    }

    UserInfo user = new UserInfo();
    user.setUsername(dto.getUsername());
    user.setPassword(dto.getPassword());
    user.setNickname(dto.getNickname() != null ? dto.getNickname() : dto.getUsername());
    user.setGender(dto.getGender() != null ? dto.getGender() : 0);
    user.setStatus(1);
    userMapper.insert(user);
    return toVO(user);
  }

  @Override
  public UserVO getById(Long id) {
    UserInfo user = userMapper.selectById(id);
    return user != null ? toVO(user) : null;
  }

  private UserVO toVO(UserInfo user) {
    UserVO vo = new UserVO();
    BeanUtils.copyProperties(user, vo);
    return vo;
  }
}