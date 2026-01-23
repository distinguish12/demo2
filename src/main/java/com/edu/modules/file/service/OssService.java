package com.edu.modules.file.service;

import java.io.InputStream;

/**
 * OSS服务接口
 */
public interface OssService {

    /**
     * 上传文件到OSS
     */
    String uploadFile(InputStream inputStream, String fileName);

    /**
     * 删除OSS文件
     */
    boolean deleteFile(String fileUrl);

    /**
     * 获取OSS文件访问URL
     */
    String getFileUrl(String filePath);

    /**
     * 生成OSS上传签名（用于前端直传）
     */
    OssSignature generateUploadSignature(String fileName);

    /**
     * OSS上传签名信息
     */
    class OssSignature {
        private String accessId;
        private String policy;
        private String signature;
        private String dir;
        private String host;
        private String expire;
        private String callback;

        public OssSignature() {}

        public OssSignature(String accessId, String policy, String signature, String dir,
                          String host, String expire, String callback) {
            this.accessId = accessId;
            this.policy = policy;
            this.signature = signature;
            this.dir = dir;
            this.host = host;
            this.expire = expire;
            this.callback = callback;
        }

        // Getters and Setters
        public String getAccessId() { return accessId; }
        public void setAccessId(String accessId) { this.accessId = accessId; }

        public String getPolicy() { return policy; }
        public void setPolicy(String policy) { this.policy = policy; }

        public String getSignature() { return signature; }
        public void setSignature(String signature) { this.signature = signature; }

        public String getDir() { return dir; }
        public void setDir(String dir) { this.dir = dir; }

        public String getHost() { return host; }
        public void setHost(String host) { this.host = host; }

        public String getExpire() { return expire; }
        public void setExpire(String expire) { this.expire = expire; }

        public String getCallback() { return callback; }
        public void setCallback(String callback) { this.callback = callback; }
    }
}