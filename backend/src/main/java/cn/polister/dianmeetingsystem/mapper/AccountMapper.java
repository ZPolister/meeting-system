package cn.polister.dianmeetingsystem.mapper;

import cn.polister.dianmeetingsystem.entity.Account;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;


/**
 * (Account)表数据库访问层
 *
 * @author Polister
 * @since 2025-04-22 19:40:22
 */
public interface AccountMapper extends BaseMapper<Account> {
    default boolean existsByUsername(String username) {
        return selectCount(new QueryWrapper<Account>().eq("username", username)) > 0;
    }

    default boolean existsByEmail(String email) {
        return selectCount(new QueryWrapper<Account>().eq("email", email)) > 0;
    }
}
