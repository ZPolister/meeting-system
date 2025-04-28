package cn.polister.dianmeetingsystem.service;

import cn.polister.dianmeetingsystem.entity.ResponseResult;
import cn.polister.dianmeetingsystem.entity.RoomOrder;
import cn.polister.dianmeetingsystem.entity.dto.CancelOrderDto;
import cn.polister.dianmeetingsystem.entity.dto.RoomOrderDto;
import cn.polister.dianmeetingsystem.entity.dto.RoomRecommendDto;
import cn.polister.dianmeetingsystem.entity.vo.RoomRecommendVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;


/**
 * (RoomOrder)表服务接口
 *
 * @author Polister
 * @since 2025-04-22 19:40:30
 */
public interface RoomOrderService extends IService<RoomOrder> {

    ResponseResult<List<RoomRecommendVo>> recommendRooms(RoomRecommendDto order);

    Long createOrder(RoomOrderDto order, Long userId);

    Page<RoomOrder> listOrders(Long userId, Integer pageNum, Integer pageSize, String status);

    void payOrder(Long orderId, Long userId);

    void cancelOrder(CancelOrderDto cancelOrderDto, Long userId);

    void cancelCancelOrder(Long orderId, Long userId);
}
