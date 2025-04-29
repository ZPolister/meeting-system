package cn.polister.dianmeetingsystem.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import cn.polister.dianmeetingsystem.constants.UserConstants;
import cn.polister.dianmeetingsystem.entity.CancellationApplication;
import cn.polister.dianmeetingsystem.entity.ResponseResult;
import cn.polister.dianmeetingsystem.entity.dto.ApplicationAuditDto;
import cn.polister.dianmeetingsystem.service.CancellationApplicationService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@Tag(name = "取消订单申请管理", description = "取消订单申请管理相关接口")
@RestController
@RequestMapping("/api/application/cancel")
public class CancelApplicationController {
    @Resource
    private CancellationApplicationService cancellationApplicationService;

    @Operation(summary = "获取取消订单申请列表", description = "分页获取取消订单申请列表")
    @GetMapping("/list")
    @SaCheckRole(value = {UserConstants.USER_ROLE_ADMIN, UserConstants.USER_ROLE_WORKER}, mode = SaMode.OR)
    public ResponseResult<Page<CancellationApplication>> getCancelApplicationList(
        @RequestParam Integer pageNum,
        @RequestParam Integer pageSize,
        @RequestParam(required = false) String status
    ) {
        return cancellationApplicationService.getCancelApplicationList(pageNum, pageSize, status);
    }

    @Operation(summary = "同意取消订单申请", description = "同意取消订单申请")
    @PostMapping("/approve")
    @SaCheckRole(value = {UserConstants.USER_ROLE_ADMIN, UserConstants.USER_ROLE_WORKER}, mode = SaMode.OR)
    public ResponseResult<Boolean> approveCancelApplication(@RequestBody ApplicationAuditDto dto) {
        Long id = StpUtil.getLoginIdAsLong();
        return cancellationApplicationService.approveCancelApplication(id, dto);
    }

    @Operation(summary = "拒绝取消订单申请", description = "拒绝取消订单申请")
    @PostMapping("/reject")
    @SaCheckRole(value = {UserConstants.USER_ROLE_ADMIN, UserConstants.USER_ROLE_WORKER}, mode = SaMode.OR)
    public ResponseResult<Boolean> rejectCancelApplication(@RequestBody ApplicationAuditDto dto) {
        Long id = StpUtil.getLoginIdAsLong();
        return cancellationApplicationService.rejectCancelApplication(id, dto);
    }

}
