package cn.polister.dianmeetingsystem.controller;


import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import cn.polister.dianmeetingsystem.constants.UserConstants;
import cn.polister.dianmeetingsystem.entity.Account;
import cn.polister.dianmeetingsystem.entity.ResponseResult;
import cn.polister.dianmeetingsystem.entity.dto.UserInfoAdminDto;
import cn.polister.dianmeetingsystem.service.AccountService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户管理模块", description = "用户管理相关接口")
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Resource
    private AccountService accountService;

    @Operation(summary = "分页获取用户列表", description = "分页获取列表（管理员用）")
    @GetMapping("/page")
    @SaCheckRole(UserConstants.USER_ROLE_ADMIN)
    public ResponseResult<Page<Account>> getUserListByPage(@RequestParam Integer pageNum,
                                                           @RequestParam Integer pageSize,
                                                           @RequestParam(required = false) String username,
                                                           @RequestParam(required = false) String mail,
                                                           @RequestParam(required = false) String userStatus) {
        return accountService.getUserListByPage(pageNum, pageSize,
                                            username, mail, userStatus);
    }

    @Operation(summary = "审批用户注册", description = "通过待审批或未通过的用户的注册申请")
    @PostMapping("/audit/{userId}")
    @SaCheckRole(value = {UserConstants.USER_ROLE_ADMIN, UserConstants.USER_ROLE_WORKER}, mode = SaMode.OR)
    public ResponseResult<Boolean> auditRegister(@PathVariable Long userId) {
        return ResponseResult.okResult(accountService.auditRegister(userId));
    }

    @Operation(summary = "拒绝用户注册", description = "拒绝用户注册")
    @PostMapping("/reject/{userId}")
    @SaCheckRole(UserConstants.USER_ROLE_ADMIN)
    public ResponseResult<Boolean> rejectRegister(@PathVariable Long userId) {
        return ResponseResult.okResult(accountService.rejectRegister(userId));
    }

    @Operation(summary = "冻结用户", description = "冻结用户")
    @PostMapping("/ban/{userId}")
    @SaCheckRole(UserConstants.USER_ROLE_ADMIN)
    public ResponseResult<Boolean> banUser(@PathVariable Long userId) {
        return ResponseResult.okResult(accountService.banUser(userId));
    }

    @Operation(summary = "解冻用户", description = "解冻用户")
    @PostMapping("/recover/{userId}")
    @SaCheckRole(UserConstants.USER_ROLE_ADMIN)
    public ResponseResult<Boolean> recoverUser(@PathVariable Long userId) {
        return ResponseResult.okResult(accountService.recoverUser(userId));
    }

    @Operation(summary = "更新用户信息", description = "更新用户信息")
    @PutMapping("/")
    @SaCheckRole(UserConstants.USER_ROLE_ADMIN)
    public ResponseResult<Boolean> updateUserInfo(@RequestBody UserInfoAdminDto userInfoAdminDto) {
        return ResponseResult.okResult(accountService.updateUserInfo(userInfoAdminDto));
    }

    @Operation(summary = "添加用户", description = "直接添加用户，无需审批")
    @PostMapping("/")
    @SaCheckRole(UserConstants.USER_ROLE_ADMIN)
    public ResponseResult<Boolean> addUser(@RequestBody UserInfoAdminDto userInfoAdminDto) {
        return ResponseResult.okResult(accountService.addUser(userInfoAdminDto));
    }
}
