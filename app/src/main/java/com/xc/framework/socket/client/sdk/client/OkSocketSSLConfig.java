package com.xc.framework.socket.client.sdk.client;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

/**
 * Author：ZhangXuanChen
 * Time：2020/4/13 14:43
 * Description：OkSocket
 */

public class OkSocketSSLConfig {
    /**
     * 安全协议名称(缺省为 SSL)
     */
    private String mProtocol;
    /**
     * 信任证书管理器(缺省为 X509)
     */
    private TrustManager[] mTrustManagers;
    /**
     * 证书秘钥管理器(缺省为 null)
     */
    private KeyManager[] mKeyManagers;
    /**
     * 自定义 SSLFactory(缺省为 null)
     */
    private SSLSocketFactory mCustomSSLFactory;

    private OkSocketSSLConfig() {

    }

    public static class Builder {
        private OkSocketSSLConfig mConfig;

        public Builder() {
            mConfig = new OkSocketSSLConfig();
        }

        public Builder setProtocol(String protocol) {
            mConfig.mProtocol = protocol;
            return this;
        }

        public Builder setTrustManagers(TrustManager[] trustManagers) {
            mConfig.mTrustManagers = trustManagers;
            return this;
        }

        public Builder setKeyManagers(KeyManager[] keyManagers) {
            mConfig.mKeyManagers = keyManagers;
            return this;
        }

        public Builder setCustomSSLFactory(SSLSocketFactory customSSLFactory) {
            mConfig.mCustomSSLFactory = customSSLFactory;
            return this;
        }

        public OkSocketSSLConfig build() {
            return mConfig;
        }
    }

    public KeyManager[] getKeyManagers() {
        return mKeyManagers;
    }

    public String getProtocol() {
        return mProtocol;
    }

    public TrustManager[] getTrustManagers() {
        return mTrustManagers;
    }

    public SSLSocketFactory getCustomSSLFactory() {
        return mCustomSSLFactory;
    }
}
