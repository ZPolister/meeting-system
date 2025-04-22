package cn.polister.dianmeetingsystem.service.impl;

import cn.polister.dianmeetingsystem.entity.Account;
import cn.polister.dianmeetingsystem.mapper.AccountMapper;
import cn.polister.dianmeetingsystem.service.AccountService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * (Account)表服务实现类
 *
 * @author Polister
 * @since 2025-04-22 19:40:28
 */
@Service("accountService")
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

}
