package io.lihongin.ssl.model.dto;

import lombok.Data;

@Data
public class SaveClientSecretKeyDto {

    private String secretKey;

    private String iv;

}
