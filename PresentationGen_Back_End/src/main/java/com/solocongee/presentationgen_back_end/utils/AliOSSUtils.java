package com.solocongee.presentationgen_back_end.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.UUID;

/**
 * 阿里云 OSS 工具类
 */
@Slf4j
@Component
public class AliOSSUtils {
    private final AliOSSProperties aliOSSProperties;

    public AliOSSUtils(AliOSSProperties aliOSSProperties) {
        this.aliOSSProperties = aliOSSProperties;
    }

    private OSS createOSSClient() {
        //获取阿里云OSS参数
        String endpoint = aliOSSProperties.getEndpoint();
        String accessKeyId = aliOSSProperties.getAccessKeyId();
        String accessKeySecret = aliOSSProperties.getAccessKeySecret();

        //创建 OSS Client
        return new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }

    private String generateURL(String bucketName, String fileName) {
        String endpoint = aliOSSProperties.getEndpoint();
        //构造文件访问路径
        return endpoint.split("//")[0] + "//" + bucketName + "." + endpoint.split("//")[1] + "/" + fileName;
    }
    /**
     * 上传图片
     *
     * @param file
     */
    public String upload(Path file) throws IOException {
        InputStream inputStream = new FileInputStream(file.toFile());
        String originalFilename = String.valueOf(file.getFileName());
        String fileName = UUID.randomUUID() + originalFilename.substring(originalFilename.lastIndexOf("."));
        String bucketName = aliOSSProperties.getBucketName();

        OSS ossClient = createOSSClient();
        try {
            ossClient.putObject(bucketName, fileName, inputStream);
        } finally {
            inputStream.close();
            ossClient.shutdown();
        }

        String url = generateURL(bucketName, fileName);
        log.info("文件{},上传成功:{}", fileName, url);
        return url;
    }
    /**
     * 上传PPT
     *
     * @param pptxData 上传的PPT的字节数组
     */
    public String uploadPPT(byte[] pptxData) throws IOException {
        InputStream inputStream = new ByteArrayInputStream(pptxData);
        String fileName = "PPT" + UUID.randomUUID() + ".pptx";
        String bucketName = aliOSSProperties.getBucketName();

        OSS ossClient = createOSSClient();
        try {
            ossClient.putObject(bucketName, fileName, inputStream);
        } finally {
            inputStream.close();
            ossClient.shutdown();
        }

        String url = generateURL(bucketName, fileName);
        log.info("PPT文件{},上传成功:{}", fileName, url);
        return url;
    }

    /**
     * 删除文件
     *
     * @param fileURL
     */
    public void deleteFile(String fileURL) {
        String bucketName = aliOSSProperties.getBucketName();
        String objectName = fileURL.substring(fileURL.lastIndexOf("/") + 1);

        OSS ossClient = createOSSClient();
        try {
            ossClient.deleteObject(bucketName, objectName);
            log.info("文件{}已被从OSS删除", objectName);
        } catch (Exception e) {
            log.error("删除文件时发生错误: {}", e.getMessage(), e);
        } finally {
            ossClient.shutdown();
        }
    }
}
