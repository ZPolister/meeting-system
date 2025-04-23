package cn.polister.dianmeetingsystem.service;

import cn.polister.dianmeetingsystem.entity.Account;
import cn.polister.dianmeetingsystem.entity.ResponseResult;
import cn.polister.dianmeetingsystem.entity.dto.LoginDto;
import cn.polister.dianmeetingsystem.entity.dto.RegisterDto;
import cn.polister.dianmeetingsystem.entity.dto.ResetPasswordDto;
import cn.polister.dianmeetingsystem.entity.dto.UserInfoAdminDto;
import cn.polister.dianmeetingsystem.entity.vo.LoginVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;


/**
 * 账户表(Account)表服务接口
 *
 * @author Polister
 * @since 2025-03-02 20:39:05
 */
public interface AccountService extends IService<Account> {
    void sendVerificationCode(String email, String type);
    void register(RegisterDto dto);
    LoginVo login(LoginDto dto);

    ResponseResult<Page<Account>> getUserListByPage(Integer pageNum, Integer pageSize
                            , String username, String mail, String userStatus);

    Boolean auditRegister(Long userId);

    Boolean rejectRegister(Long userId);

    Boolean banUser(Long userId);

    Boolean recoverUser(Long userId);

    Boolean updateUserInfo(UserInfoAdminDto userInfoAdminDto);

    Boolean addUser(UserInfoAdminDto userInfoAdminDto);

    Void resetPassword(ResetPasswordDto dto);
}
