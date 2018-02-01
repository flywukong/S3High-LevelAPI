package BucketOperation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;

import com.amazonaws.services.s3.model.BucketCrossOriginConfiguration;
import com.amazonaws.services.s3.model.CORSRule;

import common.RGWClient;
/**
 * 
 *  Class Name: BucketCORset.java
 *  Function: 
 *    set the designed cors rules to a existed bucket.
 * 
 *  @author chen  DateTime 2018年1月19日 上午10:24
 *  @version 1.0
 */
public class BucketCORSset {


	static final String ACCESS_KEY = "*********";
	static final String SECRET_KEY = "*********";
	static final String endpoint = "*********"; 
	public static void main(String[] args) {
	
		String bucketName = "testUpload";
		
		RGWClient	client = new RGWClient(ACCESS_KEY, SECRET_KEY, endpoint);
    	AmazonS3 s3 = client.createConnect();
        
		BucketCrossOriginConfiguration configuration = new BucketCrossOriginConfiguration();

		List<CORSRule> rules = new ArrayList<CORSRule>();

		CORSRule rule1 = new CORSRule()
		    .withId("CORSRule1")
		    .withAllowedMethods(Arrays.asList(new CORSRule.AllowedMethods[] { 
		            CORSRule.AllowedMethods.PUT, CORSRule.AllowedMethods.POST, CORSRule.AllowedMethods.DELETE}))
		    .withAllowedOrigins(Arrays.asList(new String[] {"http://*.example.com"}))
		    .withExposedHeaders(Arrays.asList(new String[] {"x-amz-server-side-encryption"}));
		    
		CORSRule rule2 = new CORSRule()
		.withId("CORSRule2")
		.withAllowedMethods(Arrays.asList(new CORSRule.AllowedMethods[] { 
		        CORSRule.AllowedMethods.GET}))
		.withAllowedOrigins(Arrays.asList(new String[] {"*"}))
		.withMaxAgeSeconds(3000)
		.withExposedHeaders(Arrays.asList(new String[] {"x-amz-server-side-encryption"}));

		configuration.setRules(Arrays.asList(new CORSRule[] {rule1, rule2}));


		s3.setBucketCrossOriginConfiguration(bucketName, configuration);
	}

}
