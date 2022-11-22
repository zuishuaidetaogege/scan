//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.diboot.mobile.auth.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.diboot.iam.auth.impl.BaseAuthServiceImpl;
import com.diboot.iam.config.Cons.DICTCODE_AUTH_TYPE;
import com.diboot.iam.dto.AuthCredential;
import com.diboot.iam.entity.IamAccount;
import com.diboot.iam.shiro.IamAuthToken;
import com.diboot.mobile.dto.MobileCredential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class WxAuthServiceImpl extends BaseAuthServiceImpl {
    private static final Logger log = LoggerFactory.getLogger(WxAuthServiceImpl.class);

    public WxAuthServiceImpl() {
    }

    public String getAuthType() {
        return DICTCODE_AUTH_TYPE.WX_MP.name();
    }

    protected Wrapper buildQueryWrapper(IamAuthToken iamAuthToken) {
        return Wrappers.<IamAccount>lambdaQuery().select(IamAccount::getAuthAccount, IamAccount::getUserType, IamAccount::getUserId, IamAccount::getStatus)
                .eq(IamAccount::getUserType, iamAuthToken.getUserType())
                .eq(IamAccount::getAuthType, iamAuthToken.getAuthType())
                .eq(IamAccount::getAuthAccount, iamAuthToken.getAuthAccount())
                .orderByDesc(IamAccount::getId);
    }

    protected IamAuthToken initAuthToken(AuthCredential credential) {
        MobileCredential wxMpCredential = (MobileCredential)credential;
        IamAuthToken token = new IamAuthToken(this.getAuthType(), wxMpCredential.getUserTypeClass());
        token.setAuthAccount(wxMpCredential.getAuthAccount());
        token.setRememberMe(wxMpCredential.isRememberMe());
        // 增加token时效设置，默认值是60
        token.setExpiresInMinutes(this.getExpiresInMinutes());
        return token.generateAuthtoken();
    }
}
