package cn.polister.dianmeetingsystem.service.impl;

import cn.polister.dianmeetingsystem.entity.CancellationApplication;
import cn.polister.dianmeetingsystem.mapper.CancellationApplicationMapper;
import cn.polister.dianmeetingsystem.service.CancellationApplicationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * (CancellationApplication)表服务实现类
 *
 * @author Polister
 * @since 2025-04-22 19:40:30
 */
@Service("cancellationApplicationService")
public class CancellationApplicationServiceImpl extends ServiceImpl<CancellationApplicationMapper, CancellationApplication> implements CancellationApplicationService {

}
