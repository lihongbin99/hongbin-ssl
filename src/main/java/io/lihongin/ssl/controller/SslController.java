package io.lihongin.ssl.controller;

import io.lihongin.ssl.config.SslConfig;
import io.lihongin.ssl.model.dto.SaveClientSecretKeyDto;
import io.lihongin.ssl.model.dto.SaveContextDto;
import io.lihongin.ssl.model.vo.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
public class SslController {

    private SslConfig sslConfig;

    public SslController(SslConfig sslConfig) {
        this.sslConfig = sslConfig;
    }

    @GetMapping(SslConfig.GET_SERVER_PUBLIC_KEY_URL)
    public R<String> getServerPublicKey() {
        return R.success(sslConfig.getPublicKey());
    }

    @PostMapping(SslConfig.SAVE_CLIENT_SECRET_KEY_URL)
    public R<String> saveClientPublicKey(@RequestBody SaveClientSecretKeyDto saveClientPublicKeyDto, String testString) {
        String uuid = UUID.randomUUID().toString().replace("-", "");

        SslConfig.CLIENT_SECRET_KEY_MAP.put(uuid, new SslConfig.SecretKey(saveClientPublicKeyDto.getSecretKey(), saveClientPublicKeyDto.getIv()));

        log.info("client: {}, secretKey: {}", uuid, saveClientPublicKeyDto.getSecretKey());

        return R.success(uuid);
    }

    @PostMapping("/saveContext")
    public R<Object> saveContext(@RequestBody SaveContextDto saveContextDto) {
        SslConfig.SecretKey secretKey = SslConfig.getSecretKeyId();
        SslConfig.CLIENT_CONTEXT_MAP.put(secretKey.secretKey, saveContextDto.getContext());
        log.info("secretKeyId: {}, context: {}", secretKey.secretKey, saveContextDto.getContext());
        return R.success();
    }

    @GetMapping("/getContext")
    public R<String> getContext() {
        SslConfig.SecretKey secretKey = SslConfig.getSecretKeyId();
        String result = SslConfig.CLIENT_CONTEXT_MAP.get(secretKey.secretKey);
        log.info("secretKeyId: {}, result: {}", secretKey.secretKey, result);
        return R.success(result);
    }

}
