package com.fei.web.component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.impl.PublicClaims;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fei.annotations.component.Component;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jianying9
 */
@Component
public class JwtBean
{

    public final static JwtBean INSTANCE = new JwtBean();

    private final Map<String, Object> header = new HashMap();

    //最大存活时间(秒)
    private long timeToLive = 900;

    private String publicKey = "fei";

    private String privateKey = "fei";

    private final Algorithm algorithm = Algorithm.HMAC256(this.privateKey);

    public void init()
    {
        //默认加密HS256
        this.header.put(PublicClaims.TYPE, "JWT");
        this.header.put(PublicClaims.ALGORITHM, "HS256");
    }

    public String createToken(String userId, String userName)
    {
        Date expireTime = new Date(System.currentTimeMillis() + this.timeToLive * 1000);
        return this.createToken(userId, userName, expireTime);
    }

    public String createToken(String userId, String userName, Date expireTime)
    {
        return JWT.create()
                .withHeader(header)
                .withClaim("userId", userId)
                .withClaim("userName", userName)
                .withExpiresAt(expireTime)
                .sign(algorithm);
    }

    public Token verifyToken(String auth)
    {
        Token token = new Token();
        try {
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(auth);
            token.auth = auth;
            token.userId = jwt.getClaim("userId").asString();
            token.userName = jwt.getClaim("userName").asString();
            token.expireTime = jwt.getExpiresAt();
            token.expired = false;
        } catch (TokenExpiredException e) {
            token.expired = true;
        } catch (JWTVerificationException e) {
            token = null;
        }
        return token;
    }

}
