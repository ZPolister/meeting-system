package cn.polister.dianmeetingsystem.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.polister.dianmeetingsystem.constants.AuthConstants;
import cn.polister.dianmeetingsystem.constants.UserConstants;
import cn.polister.dianmeetingsystem.entity.Account;
import cn.polister.dianmeetingsystem.entity.dto.LoginDto;
import cn.polister.dianmeetingsystem.entity.dto.RegisterDto;
import cn.polister.dianmeetingsystem.entity.vo.LoginVo;
import cn.polister.dianmeetingsystem.entity.vo.UserInfoVo;
import cn.polister.dianmeetingsystem.enums.AppHttpCodeEnum;
import cn.polister.dianmeetingsystem.exception.SystemException;
import cn.polister.dianmeetingsystem.mapper.AccountMapper;
import cn.polister.dianmeetingsystem.service.AccountService;
import cn.polister.dianmeetingsystem.utils.PasswordUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ThreadLocalRandom;

import static cn.polister.dianmeetingsystem.constants.AuthConstants.VERIFY_CODE_KEY;

/**
 * 账户表(Account)表服务实现类
 *
 * @author Polister
 * @since 2025-03-02 20:39:05
 */
@Service("accountService")
@RequiredArgsConstructor
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {
    private final AccountMapper accountMapper;
    private final RedisTemplate<String, String> redisTemplate;
    private final JavaMailSender mailSender;

    @Value("${mail.from}")
    private String fromMail;

    public void sendVerificationCode(String email) {

        checkRequestFrequency(email);

        String code = generateRandomCode();
        redisTemplate.opsForValue().set(AuthConstants.VERIFY_CODE_KEY + email, code, AuthConstants.CODE_EXPIRE);
        sendVerificationEmail(email, code);
    }

    private void checkRequestFrequency(String email) {
        String intervalKey = AuthConstants.VERIFY_CODE_INTERVAL_KEY + email;
        String lastSent = redisTemplate.opsForValue().get(intervalKey);

        if (lastSent != null) {
            long lastTime = Long.parseLong(lastSent);
            long remainSeconds = AuthConstants.CODE_INTERVAL.getSeconds() -
                    (System.currentTimeMillis() - lastTime) / 1000;

            if (remainSeconds > 0) {
                throw new SystemException(AppHttpCodeEnum.EMAIL_CODE_SEND);
            }
        }

        redisTemplate.opsForValue().set(
                intervalKey,
                String.valueOf(System.currentTimeMillis()),
                AuthConstants.CODE_INTERVAL
        );
    }

    private String generateRandomCode() {
        return String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
    }

    private void sendVerificationEmail(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromMail);
        message.setTo(to);
        message.setSubject("会议室预订系统注册验证码");
        message.setText("您的验证码是：" + code + "，有效期为5分钟");
        mailSender.send(message);
    }

    @Transactional
    public void register(RegisterDto dto) {
        validateVerificationCode(dto.getEmail(), dto.getCode());

        if (accountMapper.existsByUsername(dto.getUsername())) {
            throw new SystemException(AppHttpCodeEnum.USERNAME_EXIST);
        }

        if (accountMapper.existsByEmail(dto.getEmail())) {
            throw new SystemException(AppHttpCodeEnum.EMAIL_EXIST);
        }

        Account account = BeanUtil.toBean(dto, Account.class);
        account.setPassword(PasswordUtil.encode(dto.getPassword()));
        account.setRoleName(UserConstants.USER_ROLE_NORMAL);
        account.setBalance(0.0);
        account.setStatusType(UserConstants.USER_STATUS_WAIT_AUDIT);

        accountMapper.insert(account);
    }

    private void validateVerificationCode(String email, String code) {
        String storedCode = redisTemplate.opsForValue().get(VERIFY_CODE_KEY + email);
        if (storedCode == null || !storedCode.equals(code)) {
            throw new SystemException(AppHttpCodeEnum.EMAIL_CODE_ERROR);
        }
        redisTemplate.delete(VERIFY_CODE_KEY + email);
    }

    public LoginVo login(LoginDto dto) {
        Account account = accountMapper.selectOne(new QueryWrapper<Account>()
                .eq("username", dto.getIdentifier())
                .or().eq("email", dto.getIdentifier()));

        if (account == null || !PasswordUtil.matches(dto.getPassword(), account.getPassword())) {
            throw new SystemException(AppHttpCodeEnum.LOGIN_ERROR);
        }

        if (UserConstants.USER_STATUS_WAIT_AUDIT.equals(account.getStatusType())) {
            throw new SystemException(AppHttpCodeEnum.USER_WAIT_AUDIT);
        }

        if (UserConstants.USER_STATUS_AUDIT_FAIL.equals(account.getStatusType())) {
            throw new SystemException(AppHttpCodeEnum.USER_AUDIT_FAIL);
        }

        if (UserConstants.USER_STATUS_FREEZE.equals(account.getStatusType())) {
            throw new SystemException(AppHttpCodeEnum.USER_BANNED);
        }

        StpUtil.login(account.getId());
        return new LoginVo(StpUtil.getTokenValue(), BeanUtil.toBean(account, UserInfoVo.class));
    }
}
