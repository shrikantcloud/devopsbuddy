package com.devopsbuddy.backend.service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.Permission;
import com.amazonaws.services.s3.model.PutObjectRequest;

@Service
public class S3Service {

    private static final Logger LOG = LoggerFactory.getLogger(S3Service.class);
    
    private static final String PROFILE_PICTURE_FILE_NAME = "profilePicture";
    
    @Value("${aws.s3.root.bucket.name}")
    private String bucketName;
    
    @Value("${aws.s3.profile}")
    private String awsProfileName;
    
    @Value("${image.store.tmp.folder}")
    private String tempImageStore;
    
    @Autowired
    private AmazonS3Client s3Client;
    
    //stores the file in Amazon S3 and returns the key under which the file has been created
    public String storeProfileImage(MultipartFile uploadedFile, String username) throws IOException{
        String profileImageUrl = null;
        if(uploadedFile != null && !uploadedFile.isEmpty()) {
            byte[] bytes = uploadedFile.getBytes();
            //Root of our tem store. Will create if doesnot exist
            File tmpImageStoredFolder = new File(tempImageStore + File.separatorChar + username);
            if(!tmpImageStoredFolder.exists()) {
                LOG.info("creating the temporary root for S3 assets");
                tmpImageStoredFolder.mkdirs();
            }
            
            //The temporary file where the profile image will be stored
            File tmpProfileImageFile = new File(tmpImageStoredFolder.getAbsolutePath()
                                                +File.separatorChar
                                                + PROFILE_PICTURE_FILE_NAME
                                                +"."
                                                + FilenameUtils.getExtension(uploadedFile.getOriginalFilename()));
            
            LOG.info("Temporary file will be saved to {}", tmpProfileImageFile.getAbsolutePath());
            
            try(BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(tmpProfileImageFile.getAbsolutePath())))){
                stream.write(bytes);
            }
            
            profileImageUrl = this.storeProfileImageToS3(tmpProfileImageFile, username);
            
            //cleanup the temp folder
            tmpProfileImageFile.delete();
        }
        return profileImageUrl;
    }

    private String storeProfileImageToS3(File resource, String username) {
        String resourceUrl = null;
        
        if(!resource.exists()) {
            LOG.error("The file {} doesnot exists, throwing exception..", resource.getAbsolutePath());
            throw new IllegalArgumentException(" The file "+resource.getAbsolutePath() + " doesnot exists!");
        }
        
        String rootBucketUrl = this.ensureBucketExists(bucketName);
        
        if (null == rootBucketUrl) {
            LOG.error ("The bucket {} doesnot exists and the application was not able to create it. Hence, image wont be stored with profile", rootBucketUrl);
        } else {
            AccessControlList acl = new AccessControlList();
            acl.grantPermission(GroupGrantee.AllUsers, Permission.Read);
            String key = username + "/" +PROFILE_PICTURE_FILE_NAME + "." +FilenameUtils.getExtension(resource.getName());
            try {
                s3Client.putObject(new PutObjectRequest(bucketName, key, resource).withAccessControlList(acl));
                resourceUrl = s3Client.getResourceUrl(bucketName, key);
            } catch(AmazonClientException ace) {
                LOG.error("A client exception occured while trying to store the profile image {} on s3."
                        + "Profile image wont be stored", resource.getAbsolutePath(), ace);
                
            }
        }
        return resourceUrl;
    }

    private String ensureBucketExists(String bucketName) {
        String bucketUrl = null;
        try {
            if(!s3Client.doesBucketExist(bucketName)) {
                LOG.info("Bucket {} does not exist.. creating one");
                s3Client.createBucket(bucketName);
                LOG.info("Created bucket:{}", bucketName);
            }
            bucketUrl = s3Client.getResourceUrl(bucketName, null) + bucketName;
        } catch(AmazonClientException ace) {
            LOG.error("An error occured while connecting to s3. will not execute action for bucket: {}",bucketName, ace);
        }
        return bucketUrl;
    }
}
