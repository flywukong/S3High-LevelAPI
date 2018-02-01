package BucketOperation;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;

import common.RGWClient;

import com.amazonaws.services.s3.model.CannedAccessControlList;
/**
 * 
 *  Class Name: SetBucketAcl.java
 *  Function: 
 *   set CannedAccessControlList ACL of the bucket. 
 *   You can choose "Private","PublicRead","PublicReadWrite","AuthenticatedRead","BucketOwnerFullControl" and so on
 *   https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/s3/model/CannedAccessControlList.html
 *  @author chen  DateTime 2018年1月18日 上午15:24
 *  @version 1.0
 */
public class SetBucketAcl {
 
	static final String ACCESS_KEY = "*********";
	static final String SECRET_KEY = "*********";
	static final String endpoint = "*********"; 
	public static void main(String[] args) {
		
		
		RGWClient	client = new RGWClient(ACCESS_KEY, SECRET_KEY, endpoint);
    	AmazonS3 s3 = client.createConnect();
		final String USAGE = "\n" +
	            "To run this example, supply the name of an S3 bucket and ACL stringto\n" +
	            "set the ACL of bucket .\n" +
	            "\n" +
	            "Ex: SetBucketAcl <bucketname><acl>\n";

         if (args.length < 2) {
	       System.out.println(USAGE);
	       System.exit(1);
         }   
    	String bucket_name = args[0];
    	String acl_name = args[1];
    	System.out.format("setting ACL %s to S3 bucket %s...\n", acl_name, bucket_name);
    	CannedAccessControlList ACL = CannedAccessControlList.valueOf(acl_name);
    	boolean flag = s3.doesBucketExistV2(bucket_name);// 查询bucket
		if (flag) {
			try {
				s3.setBucketAcl(bucket_name, ACL);	
			} catch (AmazonServiceException e) {
				System.err.println(e.getErrorMessage());
        		System.exit(1);	
			}
			 System.out.println("Done!");
		}
		else {
			System.out.println("Bucket Not exists");
		}
	}

}
