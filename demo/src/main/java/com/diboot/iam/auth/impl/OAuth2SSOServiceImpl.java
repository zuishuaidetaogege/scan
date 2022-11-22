//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.diboot.iam.auth.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.exception.InvalidUsageException;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.core.vo.Status;
import com.diboot.iam.config.Cons.DICTCODE_AUTH_TYPE;
import com.diboot.iam.dto.AuthCredential;
import com.diboot.iam.dto.OAuth2SSOCredential;
import com.diboot.iam.entity.IamAccount;
import com.diboot.iam.shiro.IamAuthToken;
import com.diboot.iam.starter.IamProperties;
import com.diboot.iam.starter.IamProperties.Oauth2ClientProperties;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@ConditionalOnProperty(
    prefix = "diboot.iam.oauth2-client",
    name = {"client-id", "client-secret", "redirect-uri", "access-token-uri"}
)
public class OAuth2SSOServiceImpl extends BaseAuthServiceImpl {
    private static final Logger log = LoggerFactory.getLogger(OAuth2SSOServiceImpl.class);
    @Autowired(
        required = false
    )
    private RestTemplate restTemplate;
    @Autowired
    private IamProperties iamProperties;

    public OAuth2SSOServiceImpl() {
    }

    public String getAuthType() {
        return DICTCODE_AUTH_TYPE.SSO.name();
    }

    protected Wrapper buildQueryWrapper(IamAuthToken iamAuthToken) {
        LambdaQueryWrapper<IamAccount> queryWrapper = Wrappers.<IamAccount>lambdaQuery().select(IamAccount::getAuthAccount, IamAccount::getUserType, IamAccount::getUserId, IamAccount::getStatus).eq(IamAccount::getUserType, iamAuthToken.getUserType()).eq(IamAccount::getTenantId, iamAuthToken.getTenantId()).eq(IamAccount::getAuthAccount, iamAuthToken.getAuthAccount()).orderByDesc(IamAccount::getId);
        return queryWrapper;
    }

    public String applyToken(AuthCredential credential) {
        this.parseCode(credential);
        OAuth2SSOCredential ssoCredential = (OAuth2SSOCredential)credential;
        if (V.isEmpty(ssoCredential.getAuthAccount())) {
            throw new BusinessException(Status.FAIL_OPERATION, "认证中心验证失败");
        } else {
            return super.applyToken(credential);
        }
    }

    protected IamAuthToken initAuthToken(AuthCredential credential) {
        IamAuthToken token = new IamAuthToken(this.getAuthType(), credential.getUserTypeClass());
        token.setAuthAccount(credential.getAuthAccount());
        token.setTenantId(credential.getTenantId());
        token.setRememberMe(credential.isRememberMe());
        // 2.6版本BUG改 增加token时效设置，默认值是60
        token.setExpiresInMinutes(this.getExpiresInMinutes());
        return token.generateAuthtoken();
    }

    protected void parseCode(AuthCredential credential) {
        if (this.restTemplate == null) {
            throw new InvalidUsageException("请初始化 RestTemplate !");
        } else {
            OAuth2SSOCredential ssoCredential = (OAuth2SSOCredential)credential;
            Oauth2ClientProperties oauth2Client = this.iamProperties.getOauth2Client();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            byte[] authorization = (oauth2Client.getClientId() + ":" + oauth2Client.getClientSecret()).getBytes(StandardCharsets.UTF_8);
            String base64Auth = Base64.getEncoder().encodeToString(authorization);
            headers.add("Authorization", "Basic " + base64Auth);
            MultiValueMap<String, String> param = new LinkedMultiValueMap();
            param.add("grant_type", "authorization_code");
            param.add("code", ssoCredential.getCode());
            param.add("redirect_uri", oauth2Client.getRedirectUri());
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity(param, headers);
            ResponseEntity<Map> response = this.restTemplate.postForEntity(oauth2Client.getAccessTokenUri(), request, Map.class, new Object[0]);
            Map tokenMap = (Map)response.getBody();
            if (V.notEmpty(tokenMap)) {
                ssoCredential.setUserType(S.valueOf(tokenMap.get("userType")));
                ssoCredential.setAuthAccount(S.valueOf(tokenMap.get("username")));
            }

        }
    }
}
