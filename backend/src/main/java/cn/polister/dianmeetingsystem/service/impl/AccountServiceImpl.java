package cn.polister.dianmeetingsystem.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.polister.dianmeetingsystem.constants.AuthConstants;
import cn.polister.dianmeetingsystem.constants.UserConstants;
import cn.polister.dianmeetingsystem.entity.Account;
import cn.polister.dianmeetingsystem.entity.ResponseResult;
import cn.polister.dianmeetingsystem.entity.dto.LoginDto;
import cn.polister.dianmeetingsystem.entity.dto.RegisterDto;
import cn.polister.dianmeetingsystem.entity.dto.ResetPasswordDto;
import cn.polister.dianmeetingsystem.entity.dto.UserInfoAdminDto;
import cn.polister.dianmeetingsystem.entity.vo.LoginVo;
import cn.polister.dianmeetingsystem.entity.vo.UserInfoVo;
import cn.polister.dianmeetingsystem.enums.AppHttpCodeEnum;
import cn.polister.dianmeetingsystem.exception.SystemException;
import cn.polister.dianmeetingsystem.mapper.AccountMapper;
import cn.polister.dianmeetingsystem.service.AccountService;
import cn.polister.dianmeetingsystem.utils.PasswordUtil;
import cn.polister.dianmeetingsystem.utils.RedissonLock;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

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
    private final RedissonLock redissonLock;

    @Value("${mail.from}")
    private String fromMail;

    public void sendVerificationCode(String email, String type) {
        if ("register".equals(type)) {
            if (accountMapper.existsByEmail(email)) {
                throw new SystemException(AppHttpCodeEnum.EMAIL_EXIST);
            }
        }

        checkRequestFrequency(email, type);

        String code = generateRandomCode();
        String keyPrefix = type.equals("register") ? AuthConstants.REGISTER_CODE_KEY : AuthConstants.RESET_CODE_KEY;
        redisTemplate.opsForValue().set(keyPrefix + email, code, AuthConstants.CODE_EXPIRE);
        sendVerificationEmail(email, code, type);
    }

    private void checkRequestFrequency(String email, String type) {
        String keyPrefix = type.equals("register") ? AuthConstants.REGISTER_CODE_INTERVAL_KEY
                : AuthConstants.RESET_CODE_INTERVAL_KEY;
        String lastSent = redisTemplate.opsForValue().get(keyPrefix + email);

        if (lastSent != null) {
            long lastTime = Long.parseLong(lastSent);
            long remainSeconds = AuthConstants.CODE_INTERVAL.getSeconds() -
                    (System.currentTimeMillis() - lastTime) / 1000;

            if (remainSeconds > 0) {
                throw new SystemException(AppHttpCodeEnum.EMAIL_CODE_SEND);
            }
        }

        redisTemplate.opsForValue().set(
                keyPrefix + email,
                String.valueOf(System.currentTimeMillis()),
                AuthConstants.CODE_INTERVAL
        );
    }

    private String generateRandomCode() {
        return String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
    }

    private void sendVerificationEmail(String to, String code, String type) {
        String subject = "注册验证码";
        String content = "您的注册验证码是：" + code + "，有效期5分钟，请勿泄露给他人";
        
        if ("reset".equals(type)) {
            subject = "密码重置验证码";
            content = "您正在重置密码，验证码：" + code + "，有效期为5分钟，请勿泄露给他人";
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromMail);
        message.setTo(to);
        message.setSubject("【Dian-Meeting】" + subject);
        message.setText(content);
        mailSender.send(message);
    }

    @Transactional
    public void register(RegisterDto dto) {

        if (accountMapper.existsByUsername(dto.getUsername())) {
            throw new SystemException(AppHttpCodeEnum.USERNAME_EXIST);
        }

        validateVerificationCode(dto.getEmail(), dto.getCode(), "register");

        if (accountMapper.existsByEmail(dto.getEmail())) {
            throw new SystemException(AppHttpCodeEnum.EMAIL_EXIST);
        }

        Account account = BeanUtil.toBean(dto, Account.class);
        account.setPassword(PasswordUtil.encode(dto.getPassword()));
        account.setRoleName(UserConstants.USER_ROLE_NORMAL);
        account.setBalance(new BigDecimal(0));
        account.setStatusType(UserConstants.USER_STATUS_WAIT_AUDIT);

        accountMapper.insert(account);
    }

    private void validateVerificationCode(String email, String code, String type) {
        String keyPrefix = type.equals("register") ? AuthConstants.REGISTER_CODE_KEY : AuthConstants.RESET_CODE_KEY;
        String storedCode = redisTemplate.opsForValue().get(keyPrefix + email);
        if (storedCode == null || !storedCode.equals(code)) {
            throw new SystemException(AppHttpCodeEnum.EMAIL_CODE_ERROR);
        }
        redisTemplate.delete(keyPrefix + email);
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

        if (StpUtil.isDisable(account.getId())) {
            throw new SystemException(AppHttpCodeEnum.USER_BANNED);
        }

        StpUtil.login(account.getId());
        return new LoginVo(StpUtil.getTokenValue(), BeanUtil.toBean(account, UserInfoVo.class));
    }

    @Override
    public ResponseResult<Page<Account>> getUserListByPage(Integer pageNum,
                                                           Integer pageSize,
                                                           String username,
                                                           String mail,
                                                           String userStatus) {
        LambdaQueryWrapper<Account> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(username), Account::getUsername, username)
                .like(StringUtils.hasText(mail), Account::getEmail, mail)
                .eq(StringUtils.hasText(userStatus), Account::getStatusType, userStatus);

        Page<Account> page = new Page<>(pageNum, pageSize);
        this.page(page, wrapper);
        return ResponseResult.okResult(page);
    }

    @Override
    public Boolean auditRegister(Long userId) {
        Account account = this.getById(userId);
        if (Objects.isNull(account)) {
            throw new SystemException(AppHttpCodeEnum.USER_NOT_FOUND);
        }
        if (UserConstants.USER_STATUS_WAIT_AUDIT.equals(account.getStatusType())
        || UserConstants.USER_STATUS_AUDIT_FAIL.equals(account.getStatusType())) {
            account.setStatusType(UserConstants.USER_STATUS_NORMAL);
            return this.updateById(account);
        } else {
            throw new SystemException(AppHttpCodeEnum.USER_ALREADY_AUDIT);
        }
    }

    @Override
    public Boolean rejectRegister(Long userId) {
        Account account = this.getById(userId);
        if (Objects.isNull(account)) {
            throw new SystemException(AppHttpCodeEnum.USER_NOT_FOUND);
        }

        if (!UserConstants.USER_STATUS_WAIT_AUDIT.equals(account.getStatusType())) {
            throw new SystemException(AppHttpCodeEnum.USER_ALREADY_AUDIT);
        }

        account.setStatusType(UserConstants.USER_STATUS_AUDIT_FAIL);
        return this.updateById(account);
    }

    @Override
    public Boolean banUser(Long userId) {
        Account account = this.getById(userId);
        if (Objects.isNull(account)) {
            throw new SystemException(AppHttpCodeEnum.USER_NOT_FOUND);
        }

        if (!UserConstants.USER_STATUS_NORMAL.equals(account.getStatusType())) {
            throw new SystemException(AppHttpCodeEnum.USER_NOT_ALLOW_BAN);
        }

        StpUtil.kickout(userId);
        account.setStatusType(UserConstants.USER_STATUS_FREEZE);
        return this.updateById(account);
    }

    @Override
    public Boolean recoverUser(Long userId) {
        Account account = this.getById(userId);
        if (Objects.isNull(account)) {
            throw new SystemException(AppHttpCodeEnum.USER_NOT_FOUND);
        }

        if (!UserConstants.USER_STATUS_FREEZE.equals(account.getStatusType())) {
            throw new SystemException(AppHttpCodeEnum.USER_NOT_ALLOW_RECOVER);
        }

        account.setStatusType(UserConstants.USER_STATUS_NORMAL);
        StpUtil.untieDisable(userId);
        return this.updateById(account);
    }

    @Override
    public Boolean updateUserInfo(UserInfoAdminDto userInfoAdminDto) {
        Account account = BeanUtil.toBean(userInfoAdminDto, Account.class);
        if (Objects.nonNull(account.getPassword())) {
            account.setPassword(PasswordUtil.encode(account.getPassword()));
        }
        return this.updateById(account);
    }

    @Override
    public Boolean addUser(UserInfoAdminDto userInfoAdminDto) {
        Account account = BeanUtil.toBean(userInfoAdminDto, Account.class);
        account.setPassword(PasswordUtil.encode(account.getPassword()));

        LambdaQueryWrapper<Account> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Account::getUsername, account.getUsername());
        if (accountMapper.selectCount(wrapper) > 0) {
            throw new SystemException(AppHttpCodeEnum.USERNAME_EXIST);
        }
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Account::getEmail, account.getEmail());
        if (accountMapper.selectCount(wrapper) > 0) {
            throw new SystemException(AppHttpCodeEnum.EMAIL_EXIST);
        }

        return this.save(account);
    }

    @Override
    public Void resetPassword(ResetPasswordDto dto) {
        validateVerificationCode(dto.getEmail(), dto.getCode(), "reset");
        LambdaQueryWrapper<Account> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Account::getEmail, dto.getEmail());
        Account account = accountMapper.selectOne(wrapper);
        if (Objects.isNull(account)) {
            throw new SystemException(AppHttpCodeEnum.USER_NOT_FOUND);
        }
        account.setPassword(PasswordUtil.encode(dto.getNewPassword()));
        accountMapper.updateById(account);
        return null;
    }

    @Override
    public void payOrder(BigDecimal totalPrice, Long userId) {
        Account account = this.getById(userId);
        if (account.getBalance().compareTo(totalPrice) < 0) {
            throw new SystemException(AppHttpCodeEnum.BALANCE_NOT_ENOUGH);
        }
        account.setBalance(account.getBalance().subtract(totalPrice));
        this.updateById(account);
    }

    @Override
    public void refundOrder(BigDecimal totalPrice, Long userId) {
        Account account = this.getById(userId);
        account.setBalance(account.getBalance().add(totalPrice));
        this.updateById(account);
    }

    @Override
    public void rechargeBalance(long loginIdAsLong, BigDecimal amount) {
        if (!redissonLock.lock(UserConstants.USER_REDIS_LOCK_KEY + loginIdAsLong, 10000L)) {
            throw new SystemException(AppHttpCodeEnum.RECHARGE_FAIL);
        }
        try {
            Account account = this.getById(loginIdAsLong);
            account.setBalance(account.getBalance().add(amount));
            this.updateById(account);
        } finally {
            redissonLock.unlock(UserConstants.USER_REDIS_LOCK_KEY + loginIdAsLong);
        }
    }


}
