package io.lihongin.ssl.config;

import io.lihongin.ssl.utils.ServletUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Data
@Slf4j
@Component
@ConfigurationProperties(prefix = "ssl")
public class SslConfig {

    public final static String SECRET_KEY_ID = "secretKeyId";

    public final static String GET_SERVER_PUBLIC_KEY_URL = "/getServerPublicKey";
    public final static String SAVE_CLIENT_SECRET_KEY_URL = "/saveClientSecretKey";

    private String privateKey;
    private String publicKey;

    public final static Map<String, SecretKey> CLIENT_SECRET_KEY_MAP = new HashMap<>();
    public final static Map<String, String> CLIENT_CONTEXT_MAP = new HashMap<>();

    public static SecretKey getSecretKeyId() {
        SecretKey secretKey = null;
        HttpServletRequest request = ServletUtils.getRequest();
        String secretKeyId = request.getHeader(SslConfig.SECRET_KEY_ID);
        if (StringUtils.hasText(secretKeyId)) {
            secretKey = SslConfig.CLIENT_SECRET_KEY_MAP.get(secretKeyId);
        }
        if (null == secretKey) {
            // TODO 异常处理
            throw new RuntimeException("没有找到客户端密钥");
        }
        return secretKey;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    static public class SecretKey {
        public String secretKey;
        public String iv;
    }

}
