package common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.retry.PredefinedRetryPolicies;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AbortMultipartUploadRequest;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.UploadPartRequest;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.ClientConfiguration;

/**
 * 
 *  Class Name: RGWClient.java
 *  Function: 
 *     build a rgwclient, use the key and endpoint.
 *     Modifications:   
 *     
 *  @author chen  DateTime 2018年1月17日 上午9:00
 *  @version 1.0
 */
public class RGWClient {

		private String accessKey;
		private String secretKey;
		private String hostname;
		
		
	    public RGWClient(String accessKey, String secretKey, String hostname) {
	        this.accessKey = accessKey;
	        this.secretKey = secretKey;
	        this.hostname = hostname;
	    }
	    
	    /**
	     * 使用账号访问口令与密钥以及站点地址进行远程连接
	     * @return
	     */
	    public AmazonS3 createConnect() {
	        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
	   
	        //Set ClientConfiguration
	        ClientConfiguration clientConf = new ClientConfiguration();
	        int timeout = 30000;
	        clientConf.setConnectionTimeout(timeout);
	        clientConf.setSocketTimeout(timeout);
	        clientConf.setProtocol(Protocol.HTTP);
	        clientConf.withUseExpectContinue(false);
	        clientConf.withSignerOverride("S3SignerType");
	        clientConf.setRetryPolicy(
	    			PredefinedRetryPolicies.getDefaultRetryPolicyWithCustomMaxRetries(5));
	       	//build the connection  
	        AmazonS3ClientBuilder standard = AmazonS3ClientBuilder.standard();
			AWSCredentialsProvider credentialsProvider= new AWSStaticCredentialsProvider(credentials);
			standard.setCredentials(credentialsProvider);
			standard.setEndpointConfiguration(new EndpointConfiguration(hostname, null));
			standard.setClientConfiguration(clientConf);
			//Configures the client to use path-style access for all requests
			standard.withPathStyleAccessEnabled(Boolean.TRUE);
	
			AmazonS3 conn = standard.build();
			
	        return conn;
	    }

	 
}
