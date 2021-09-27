package com.hm.oauth.response;

import lombok.Data;

/**
 * @author whm
 * @version 1.0
 * @date 2021/7/10 16:50
 * oauth返回的token内容
 */
@Data
public class TokenContext {

    private String access_token;

    private String token_type;

    private String refresh_token;

    private String expires_in;

    private String scope;

}
