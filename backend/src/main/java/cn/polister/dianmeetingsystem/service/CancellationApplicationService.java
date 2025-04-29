package cn.polister.dianmeetingsystem.service;

import cn.polister.dianmeetingsystem.entity.CancellationApplication;
import cn.polister.dianmeetingsystem.entity.ResponseResult;
import cn.polister.dianmeetingsystem.entity.dto.ApplicationAuditDto;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;


/**
 * (CancellationApplication)表服务接口
 *
 * @author Polister
 * @since 2025-04-22 19:40:30
 */
public interface CancellationApplicationService extends IService<CancellationApplication> {

    ResponseResult<Page<CancellationApplication>> getCancelApplicationList(Integer pageNum, Integer pageSize, String status);

    ResponseResult<Boolean> approveCancelApplication(Long id, ApplicationAuditDto dto);

    ResponseResult<Boolean> rejectCancelApplication(Long id, ApplicationAuditDto dto);
}
