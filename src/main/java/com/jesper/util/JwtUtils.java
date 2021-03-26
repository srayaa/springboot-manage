package com.jesper.util;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.jesper.model.Member;
import com.jesper.model.User;

import lombok.Data;

@Data
@Component
public class JwtUtils {

    private static final long EXPIRE_TIME = 24 * 60 * 60;

    private static final String SECRET = "HYBI%&J)(UJJ&TG&";

    private Long expire;

    private String secret;


    /**
     * @return 加密的token
     */
    public String sign(User userInfo) {
        Date date = new Date(System.currentTimeMillis() +  EXPIRE_TIME * 1000);
        Algorithm algorithm;
		try {
			algorithm = Algorithm.HMAC256( SECRET);
			// 附带username信息
	        return JWT.create()
	                .withSubject(userInfo.getId()+"")
	                .withIssuer(userInfo.getUserName())
	                .withExpiresAt(date)
	                .sign(algorithm);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return "";
    }

    public String sign(Member userInfo) {
        Date date = new Date(System.currentTimeMillis() +  EXPIRE_TIME * 1000);
        Algorithm algorithm;
		try {
			algorithm = Algorithm.HMAC256( SECRET);
			// 附带username信息
	        return JWT.create()
	                .withSubject(userInfo.getId()+"")
	                .withIssuer(userInfo.getPname())
	                .withExpiresAt(date)
	                .sign(algorithm);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return "";
    }
    
    /**
     * 校验token是否正确
     *
     * @param token 密钥
     * @return 是否正确
     */
    public Map<String, Claim> verify(String token, String username) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            JWTVerifier verifier = JWT.require(algorithm).withIssuer(username).build();
            DecodedJWT jwt = verifier.verify(token);
            return jwt.getClaims();
        } catch (Exception e) {
        	e.printStackTrace();
            //throw AppointException.errorMessage("鉴权失败,无效的用户");
        }
        return null;
    }

    /**
     * 获得token中的信息无需secret解密也能获得
     *
     * @return token中包含的用户名
     */
    public String getUsername(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getIssuer();
        } catch (JWTDecodeException e) {
        	e.printStackTrace();
            //throw AppointException.errorMessage("无效的token，请重新登录");
        }
        return "";
    }

    /**
     * 获得token中的信息无需secret解密也能获得
     *
     * @return token中包含的id
     */
    public Integer getUserId(String token) {
        try {
        	
            DecodedJWT jwt = JWT.decode(token);
            return Integer.parseInt(jwt.getSubject());
        } catch (JWTDecodeException e) {
        	e.printStackTrace();
            //throw AppointException.errorMessage("无效的token，请重新登录");
        }
        return -1;
    }
    public static void main(String[] args) {
    	/*User user = new User();
    	user.setUserName("admin");
    	user.setId(6);
    	JwtUtils ju = new JwtUtils();
    	System.out.println(ju.sign(user));*/
    	//eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI2IiwiaXNzIjoiYWRtaW4iLCJleHAiOjE2MTY2NjEzNzN9.xlulBbUdZn6MApeFBLRKsv-_nDEwOSDAoNziGEdnRVE
    	String tk = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI2IiwiaXNzIjoiYWRtaW4iLCJleHAiOjE2MTY2NjEzNzN9.xlulBbUdZn6MApeFBLRKsv-_nDEwOSDAoNziGEdnRVE";
    	JwtUtils ju = new JwtUtils();
    	ju.verify(tk, "admin");
    	
    }
}

