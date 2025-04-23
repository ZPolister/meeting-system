package cn.polister.dianmeetingsystem.service;

import cn.polister.dianmeetingsystem.entity.Account;
import cn.polister.dianmeetingsystem.entity.dto.LoginDto;
import cn.polister.dianmeetingsystem.entity.dto.RegisterDto;
import cn.polister.dianmeetingsystem.entity.vo.LoginVo;
import com.baomidou.mybatisplus.extension.service.IService;


/**
 * 账户表(Account)表服务接口
 *
 * @author Polister
 * @since 2025-03-02 20:39:05
 */
public interface AccountService extends IService<Account> {
    void sendVerificationCode(String email);
    void register(RegisterDto dto);
    LoginVo login(LoginDto dto);

}
