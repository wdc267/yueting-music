package com.yueting.common.service.impl;

import com.yueting.common.mapper.UserMapper;
import com.yueting.common.service.UserService;
import com.yueting.entity.dto.*;
import com.yueting.entity.po.UserInfo;
import com.yueting.entity.vo.UserLoginVO;
import com.yueting.entity.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
    if (!dto.getPassword().equals(user.getPassword())) {
      throw new RuntimeException("密码错误");
    }
    user.setLastLoginTime(LocalDateTime.now());
    userMapper.update(user);

    UserLoginVO vo = new UserLoginVO();
    vo.setUser(toVO(user));
    vo.setToken(UUID.randomUUID().toString().replace("-", ""));
    return vo;
  }

  @Override
  @Transactional
  public UserVO register(UserRegisterDTO dto) {
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

  @Value("${project.folder:./}")
  private String projectFolder;

  @Override
  public String updateAvatar(Long userId, MultipartFile file) {
    try {
      String dir = projectFolder + "upload/images/";
      File dirFile = new File(dir);
      if (!dirFile.exists()) dirFile.mkdirs();
      String ext = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
      String filename = UUID.randomUUID().toString().replace("-", "") + ext;
      file.transferTo(new File(dir + filename));
      String avatarUrl = "/upload/images/" + filename;
      userMapper.updateAvatar(userId, avatarUrl);
      return avatarUrl;
    } catch (Exception e) {
      throw new RuntimeException("头像上传失败: " + e.getMessage());
    }
  }

  @Override
  public List<UserVO> selectList(UserQueryDTO query) {
    return userMapper.selectList(query).stream()
        .map(this::toVO)
        .collect(Collectors.toList());
  }

  @Override
  public void update(UserUpdateDTO dto) {
    UserInfo user = new UserInfo();
    user.setId(dto.getId());
    user.setNickname(dto.getNickname());
    user.setGender(dto.getGender());
    user.setStatus(dto.getStatus());
    userMapper.update(user);
  }

  @Override
  public void updatePassword(UserPasswordDTO dto) {
    if (dto.getNewPassword() == null || dto.getNewPassword().trim().isEmpty()) {
      throw new RuntimeException("密码不能为空");
    }
    userMapper.updatePassword(dto.getId(), dto.getNewPassword());
  }

  @Override
  public void delete(Long id) {
    userMapper.deleteById(id);
  }

  private UserVO toVO(UserInfo user) {
    UserVO vo = new UserVO();
    BeanUtils.copyProperties(user, vo);
    return vo;
  }
}
