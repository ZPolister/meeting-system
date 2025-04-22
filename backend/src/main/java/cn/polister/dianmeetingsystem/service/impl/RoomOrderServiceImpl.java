package cn.polister.dianmeetingsystem.service.impl;

import cn.polister.dianmeetingsystem.entity.RoomOrder;
import cn.polister.dianmeetingsystem.mapper.RoomOrderMapper;
import cn.polister.dianmeetingsystem.service.RoomOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * (RoomOrder)表服务实现类
 *
 * @author Polister
 * @since 2025-04-22 19:40:30
 */
@Service("roomOrderService")
public class RoomOrderServiceImpl extends ServiceImpl<RoomOrderMapper, RoomOrder> implements RoomOrderService {

}
