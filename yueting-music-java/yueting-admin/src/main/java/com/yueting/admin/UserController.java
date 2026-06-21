package com.yueting.admin;
import com.yueting.common.service.UserService;
import com.yueting.entity.dto.UserQueryDTO;
import com.yueting.entity.dto.UserUpdateDTO;
import com.yueting.entity.dto.UserPasswordDTO;
import com.yueting.entity.vo.Result;
import com.yueting.entity.vo.UserVO;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/system/user")
public class UserController {
  private final UserService userService;
  public UserController(UserService userService) { this.userService = userService; }
  @PostMapping("/list")
  public Result<List<UserVO>> list(@RequestBody UserQueryDTO query) { return Result.success(userService.selectList(query)); }
  @GetMapping("/{id}")
  public Result<UserVO> getById(@PathVariable Long id) { UserVO vo = userService.getById(id); return vo != null ? Result.success(vo) : Result.error(404, "数据不存在"); }
  @PutMapping("/update")
  public Result<Void> update(@RequestBody UserUpdateDTO dto) { userService.update(dto); return Result.success(); }
  @PutMapping("/password")
  public Result<Void> updatePassword(@RequestBody UserPasswordDTO dto) { userService.updatePassword(dto); return Result.success(); }
  @DeleteMapping("/{id}")
  public Result<Void> delete(@PathVariable Long id) { userService.delete(id); return Result.success(); }
}
