package io.lihongin.ssl.converter;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.lihongin.ssl.config.SslConfig;
import io.lihongin.ssl.utils.AesUtil;
import io.lihongin.ssl.utils.RSAUtil;
import io.lihongin.ssl.utils.ServletUtils;
import lombok.SneakyThrows;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.StreamUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class SslHttpMessageConverter extends MappingJackson2HttpMessageConverter {

    private SslConfig sslConfig;

    public SslHttpMessageConverter(SslConfig sslConfig) {
        this.sslConfig = sslConfig;
    }

    @Override
    public boolean canRead(Type type, Class<?> contextClass, MediaType mediaType) {
        return true;
    }

    @SneakyThrows
    @Override
    public Object read(Type type, Class<?> contextClass, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {

        HttpServletRequest request = ServletUtils.getRequest();
        if (SslConfig.SAVE_CLIENT_SECRET_KEY_URL.equals(request.getServletPath())) {
            return rsa(type, contextClass, inputMessage);
        } else {
            return aes(type, contextClass, inputMessage);
        }
    }

    private Object rsa(Type type, Class<?> contextClass, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        byte[] bytes = readBody(inputMessage);

        byte[] decrypt = RSAUtil.decrypt(Base64.getDecoder().decode(bytes), Base64.getDecoder().decode(sslConfig.getPrivateKey()));

        JavaType javaType = getJavaType(type, contextClass);
        return this.defaultObjectMapper.readValue(decrypt, javaType);
    }

    private Object aes(Type type, Class<?> contextClass, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException {
        SslConfig.SecretKey key = SslConfig.getSecretKeyId();

        byte[] bytes = readBody(inputMessage);

        String decrypt = AesUtil.decrypt(bytes, key.secretKey, key.iv);

        JavaType javaType = getJavaType(type, contextClass);
        return this.defaultObjectMapper.readValue(decrypt, javaType);
    }

    private byte[] readBody(HttpInputMessage inputMessage) throws IOException {
        InputStream body = inputMessage.getBody();
        byte[] bytes = new byte[body.available()];
        int len = body.read(bytes);
        return bytes;
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        return true;
    }

    @Override
    public boolean canWrite(Type type, Class<?> clazz, MediaType mediaType) {
        return true;
    }

    @Override
    protected boolean canWrite(MediaType mediaType) {
        return true;
    }

    @SneakyThrows
    @Override
    protected void writeInternal(Object object, Type type, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        HttpServletRequest request = ServletUtils.getRequest();
        String servletPath = request.getServletPath();
        if ("/error".equals(servletPath) || SslConfig.GET_SERVER_PUBLIC_KEY_URL.equals(servletPath) || SslConfig.SAVE_CLIENT_SECRET_KEY_URL.equals(servletPath)) {
            super.writeInternal(object, type, outputMessage);
            return;
        }
        SslConfig.SecretKey key = SslConfig.getSecretKeyId();

        String result = this.defaultObjectMapper.writeValueAsString(object);
        String encrypt = AesUtil.encrypt(result, key.secretKey, key.iv);

        MediaType contentType = outputMessage.getHeaders().getContentType();
        JsonEncoding encoding = getJsonEncoding(contentType);
        OutputStream outputStream = StreamUtils.nonClosing(outputMessage.getBody());
        JsonGenerator generator = this.defaultObjectMapper.getFactory().createGenerator(outputStream, encoding);
        ObjectWriter objectWriter = this.defaultObjectMapper.writer();

        objectWriter.writeValue(generator, encrypt);

        generator.flush();
        generator.close();
    }
}
