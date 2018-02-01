package BucketOperation;
import java.util.List;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CannedAccessControlList;

import common.RGWClient;

/**
 * 
 *  Class Name:CreateBucket.java
 *  Function: 
 *    create a new bucket , if bucket exists, return the old one.
 * 
 *  @author chen  DateTime 2018年1月19日 上午10:24
 *  @version 1.0
 */
public class CreateBucket {
	
	static final String ACCESS_KEY = "*********";
	static final String SECRET_KEY = "*********";
	static final String endpoint = "*********"; 
	
	public static Bucket getBucket(String bucket_name) {
		RGWClient	client = new RGWClient(ACCESS_KEY, SECRET_KEY, endpoint);
    	AmazonS3 s3 = client.createConnect();
        Bucket named_bucket = null;
        List<Bucket> buckets = s3.listBuckets();
        for (Bucket b : buckets) {
            if (b.getName().equals(bucket_name)) {
                named_bucket = b;
            }
        }
        return named_bucket;
    }

    public static Bucket createBucket(String bucket_name) {
    	
    	RGWClient	client = new RGWClient(ACCESS_KEY, SECRET_KEY, endpoint);
    	AmazonS3 s3 = client.createConnect();
        Bucket b = null;
        if (s3.doesBucketExistV2(bucket_name)) {
            System.out.format("Bucket %s already exists.\n", bucket_name);
            b = getBucket(bucket_name);
        } else {
            try {
                b = s3.createBucket(bucket_name);
            } catch (AmazonS3Exception e) {
                System.err.println(e.getErrorMessage());
            }
        }
        return b;
    }
	
	public static void main(String[] args) {
		
		   
        String bucket_name = "testabc";
        System.out.format("\nCreating S3 bucket: %s\n", "test");
        
        RGWClient	client = new RGWClient(ACCESS_KEY, SECRET_KEY, endpoint);
    	AmazonS3 s3 = client.createConnect();
    	  
        
        Bucket b = createBucket(bucket_name);
        if (b == null) {
            System.out.println("Error creating bucket!\n");
        } else {
            System.out.println("Done!\n");
        }
    	
	}
}
    	
	


