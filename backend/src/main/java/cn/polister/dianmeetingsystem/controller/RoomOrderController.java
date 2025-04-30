package cn.polister.dianmeetingsystem.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.polister.dianmeetingsystem.constants.UserConstants;
import cn.polister.dianmeetingsystem.entity.ResponseResult;
import cn.polister.dianmeetingsystem.entity.RoomOrder;
import cn.polister.dianmeetingsystem.entity.dto.CancelOrderDto;
import cn.polister.dianmeetingsystem.entity.dto.RoomOrderDto;
import cn.polister.dianmeetingsystem.entity.dto.RoomRecommendDto;
import cn.polister.dianmeetingsystem.entity.vo.RefundNumberVo;
import cn.polister.dianmeetingsystem.entity.vo.RoomOrderVo;
import cn.polister.dianmeetingsystem.entity.vo.RoomRecommendVo;
import cn.polister.dianmeetingsystem.service.RoomOrderService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/order")
@Tag(name = "会议室预订模块")
public class RoomOrderController {

    @Resource
    private RoomOrderService roomOrderService;

    @PostMapping("/recommend")
    @SaCheckLogin
    @Operation(summary = "获取会议室推荐")
    public ResponseResult<List<RoomRecommendVo>> recommendRooms(@RequestBody RoomRecommendDto order) {
        return roomOrderService.recommendRooms(order);
    }

    @PostMapping
    @SaCheckLogin
    @Operation(summary = "创建预订订单")
    public ResponseResult<Long> createOrder(@RequestBody RoomOrderDto order) {
        Long userId = StpUtil.getLoginIdAsLong();
        return ResponseResult.okResult(roomOrderService.createOrder(order, userId));
    }

    @GetMapping("/list")
    @SaCheckLogin
    @Operation(summary = "分页获取用户订单列表")
    public ResponseResult<Page<RoomOrder>> listOrders(@RequestParam(defaultValue = "1") Integer pageNum,
                                                      @RequestParam(defaultValue = "10") Integer pageSize,
                                                      @RequestParam(required = false) String status) {
        Long userId = StpUtil.getLoginIdAsLong();
        return ResponseResult.okResult(roomOrderService.listOrders(userId, pageNum, pageSize, status));
    }

    @PostMapping("/pay/{orderId}")
    @SaCheckLogin
    @Operation(summary = "支付订单")
    public ResponseResult<Void> payOrder(@PathVariable Long orderId) {
        Long userId = StpUtil.getLoginIdAsLong();
        roomOrderService.payOrder(orderId, userId);
        return ResponseResult.okResult();
    }

    @PostMapping("/cancel/")
    @SaCheckLogin
    @Operation(summary = "申请取消订单")
    public ResponseResult<Void> cancelOrder(@RequestBody CancelOrderDto cancelOrderDto) {
        Long userId = StpUtil.getLoginIdAsLong();
        roomOrderService.cancelOrder(cancelOrderDto, userId);
        return ResponseResult.okResult();
    }

    @PostMapping("/cancel/canceled/{orderId}")
    @SaCheckLogin
    @Operation(summary = "取消取消订单的申请")
    public ResponseResult<Void> cancelCancelOrder(@PathVariable Long orderId) {
        Long userId = StpUtil.getLoginIdAsLong();
        roomOrderService.cancelCancelOrder(orderId, userId);
        return ResponseResult.okResult();
    }

    @GetMapping("/cancel/{orderId}")
    @SaCheckLogin
    @Operation(summary = "根据id获取取消订单的申请所有相关信息")
    public ResponseResult<RoomOrderVo> getCancelOrder(@PathVariable Long orderId) {
        Long userId = StpUtil.getLoginIdAsLong();
        return ResponseResult.okResult(roomOrderService.getCancelOrder(orderId, userId));
    }

    @GetMapping("/cancel/refund/{orderId}")
    @SaCheckLogin
    @Operation(summary = "根据id获取取消订单的退款金额")
    public ResponseResult<RefundNumberVo> getRefundAmount(@PathVariable Long orderId) {
        Long userId = StpUtil.getLoginIdAsLong();
        return ResponseResult.okResult(roomOrderService.getRefundAmount(orderId, userId));
    }
}
