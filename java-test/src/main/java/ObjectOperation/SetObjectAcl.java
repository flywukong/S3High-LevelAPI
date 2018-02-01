package ObjectOperation;

import java.nio.file.Paths;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.CannedAccessControlList;

import common.RGWClient;
/**
 * 
 *  Class Name: SetBucketAcl.java
 *  Function: 
 *   set CannedAccessControlList ACL of the object. The ACL of object is independent of the ACL of Bucket
 *   You can choose "Private","PublicRead","PublicReadWrite","AuthenticatedRead","BucketOwnerFullControl" and so on
 *   https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/s3/model/CannedAccessControlList.html
 *  @author chen  DateTime 2018年1月18日 上午15:24
 *  @version 1.0
 */
public class SetObjectAcl {

	static final String ACCESS_KEY = "*********";
	static final String SECRET_KEY = "*********";
	static final String endpoint = "*********"; 
	public static void main(String[] args) {
		
		
		RGWClient	client = new RGWClient(ACCESS_KEY, SECRET_KEY, endpoint);
    	AmazonS3 s3 = client.createConnect();
		final String USAGE = "\n" +
	            "To run this example, supply the name of an S3 bucket ,key and ACL stringto\n" +
	            "set the ACL of objects .\n" +
	            "\n" +
	            "Ex: SetObjectAcl <bucketname><file><acl>\n";

         if (args.length < 3) {
	       System.out.println(USAGE);
	       System.exit(1);
         }
    	String bucket_name = args[0];
	    String key_name = args[1];
	    String acl_name = args[2];
    	
    	System.out.format("setting ACL %s to object %s of S3 bucket %s...\n", acl_name, key_name,bucket_name);
    	CannedAccessControlList ACL = CannedAccessControlList.valueOf(acl_name);
    	boolean doesBucketExist = s3.doesBucketExistV2(bucket_name);
    	boolean doesObjectExist = s3.doesObjectExist(bucket_name,key_name);
    	if (doesBucketExist && doesObjectExist )
    	{
			
			try {
				s3.setObjectAcl(bucket_name,key_name,ACL);
			} catch (AmazonServiceException e) {
				System.err.println(e.getErrorMessage());
        		System.exit(1);	
			}
			 System.out.println("Done!");
		}
		else {
			System.out.println("Object Not exists");
		}
	}

}
