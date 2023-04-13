package cn.holelin.rpc.config;

import cn.holelin.rpc.utils.SerializerAlgorithm;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public abstract class Config {
    static Properties properties;
    static {
        try (InputStream in = Config.class.getResourceAsStream("/application.properties")) {
            properties = new Properties();
            properties.load(in);
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
    public static int getServerPort() {
        String value = properties.getProperty("server.port");
        if(value == null) {
            return 8080;
        } else {
            return Integer.parseInt(value);
        }
    }
    public static SerializerAlgorithm getSerializerAlgorithm() {
        String value = properties.getProperty("serializer.algorithm");
        if(value == null) {
            return SerializerAlgorithm.Java;
        } else {
            return SerializerAlgorithm.valueOf(value);
        }
    }
}