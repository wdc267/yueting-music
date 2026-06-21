package com.yueting.web.controller;

import com.yueting.common.service.UserService;
import com.yueting.entity.dto.UserLoginDTO;
import com.yueting.entity.dto.UserRegisterDTO;
import com.yueting.entity.vo.Result;
import com.yueting.entity.vo.UserLoginVO;
import com.yueting.entity.vo.UserVO;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/web/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO dto) {
        try {
            return Result.success(userService.login(dto));
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    @PostMapping("/register")
    public Result<UserVO> register(@RequestBody UserRegisterDTO dto) {
        try {
            return Result.success(userService.register(dto));
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    @GetMapping("/info/{id}")
    public Result<UserVO> info(@PathVariable Long id) {
        UserVO vo = userService.getById(id);
        return vo != null ? Result.success(vo) : Result.error(404, "用户不存在");
    }

    @PostMapping("/avatar")
    public Result<String> updateAvatar(@RequestParam Long userId, @RequestParam("file") MultipartFile file) {
        try {
            String avatarUrl = userService.updateAvatar(userId, file);
            return Result.success(avatarUrl);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }
}