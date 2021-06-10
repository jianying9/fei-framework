package com.fei.web.component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.impl.PublicClaims;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fei.module.Component;
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

    private Map<String, Object> header = new HashMap();

    //最大存活时间(秒)
    private long timeToLive = 900;

    private String publicKey = "fei";

    private String privateKey = "fei";

    private Algorithm algorithm = Algorithm.HMAC256(this.privateKey);

    public void init()
    {
        //默认加密HS256
        this.header.put(PublicClaims.TYPE, "JWT");
        this.header.put(PublicClaims.ALGORITHM, "HS256");
    }

    public String createToken(Session session)
    {
        Date expireDate = new Date(System.currentTimeMillis() + this.timeToLive * 1000);
        return JWT.create()
                .withHeader(header)
                .withClaim("userId", session.userId)
                .withClaim("userName", session.userName)
                .withExpiresAt(expireDate)
                .sign(algorithm);
    }

    public Session verifyToken(String token)
    {
        Session session = null;
        try {
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);
            session = new Session();
            session.userId = jwt.getClaim("userId").asString();
            session.userName = jwt.getClaim("userName").asString();
        } catch (Exception e) {
        }
        return session;
    }

}
