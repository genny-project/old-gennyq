package life.genny.utils;


import java.security.Key;
import java.util.Date;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.InvalidKeyException;
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.apache.commons.codec.binary.Base64;

@RegisterForReflection
public class SecurityUtils {
    public static String decrypt(String key, String initVector, String encrypted) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted));

            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }
    public static String createJwt(String id, String issuer, String subject, long ttlMillis, String apiSecret,
                                   Map<String, Object> claims) {

        // The JWT signature algorithm we will be using to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        String aud = issuer;
        if (claims.containsKey("aud")) {
            aud = (String) claims.get("aud");
            claims.remove("aud");
        }
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        // We will sign our JWT with our ApiKey secret
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(apiSecret);


        // Let's set the JWT Claims
        JwtBuilder builder = Jwts.builder().setId(id).setIssuedAt(now).setSubject(subject).setIssuer(issuer)
                .setAudience(aud).setClaims(claims);

        Key key = null;

        try {
            key = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
            builder.signWith(key, SignatureAlgorithm.HS256);
        } catch (Exception e) {
            try {
                key = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
                builder.signWith(key, SignatureAlgorithm.HS256);
            } catch (Exception e1) {
// TODO Auto-generated catch block
                try {
                    Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
                    builder.signWith(signingKey, signatureAlgorithm);
                } catch (InvalidKeyException e2) {
                }
            }
        }

        // if it has been specified, let's add the expiration
        if (ttlMillis >= 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }

        // Builds the JWT and serializes it to a compact, URL-safe string
        return builder.compact();
    }
//
}