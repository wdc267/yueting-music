package com.yueting.web.controller;

import com.yueting.common.service.UserService;
import com.yueting.entity.dto.UserLoginDTO;
import com.yueting.entity.dto.UserRegisterDTO;
import com.yueting.entity.vo.Result;
import com.yueting.entity.vo.UserLoginVO;
import com.yueting.entity.vo.UserVO;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/web/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
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

    @GetMapping("/user/{id}")
    public Result<UserVO> getUser(@PathVariable Long id) {
        UserVO vo = userService.getById(id);
        if (vo == null) {
            return Result.error(404, "用户不存在");
        }
        return Result.success(vo);
    }
}