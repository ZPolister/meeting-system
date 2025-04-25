package cn.polister.dianmeetingsystem.config;

import cn.dev33.satoken.model.wrapperInfo.SaDisableWrapperInfo;
import cn.dev33.satoken.stp.StpInterface;
import cn.polister.dianmeetingsystem.constants.UserConstants;
import cn.polister.dianmeetingsystem.entity.Account;
import cn.polister.dianmeetingsystem.mapper.AccountMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 自定义权限加载接口实现类
 */
@Component
public class StpInterfaceImpl implements StpInterface {


    @Resource
    private AccountMapper accountMapper;

    /**
     * 返回一个账号所拥有的权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return null;
    }

    /**
     * 返回一个账号所拥有的角色标识集合 (权限与角色可分开校验)
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        Account account = accountMapper.selectById(Long.parseLong(String.valueOf(loginId)));
        return new ArrayList<>(List.of(new String[]{account.getRoleName()}));
    }

    @Override
    public SaDisableWrapperInfo isDisabled(Object loginId, String service) {
        Account account = accountMapper.selectById(Long.parseLong(String.valueOf(loginId)));
        if (Objects.isNull(account)
                || !UserConstants.USER_STATUS_FREEZE.equals(account.getStatusType())) {
            return SaDisableWrapperInfo.createNotDisabled();
        }
        return SaDisableWrapperInfo.createDisabled(-1, 1);
    }
}
