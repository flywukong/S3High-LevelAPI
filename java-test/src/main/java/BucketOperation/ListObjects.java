package BucketOperation;

import java.util.List;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import common.RGWClient;

/**
 * 
 *  Class Name: ListObjects.java
 *  Function: 
 *    list all the objects of the bucket.
 * 
 *  @author chen  DateTime 2018年1月19日 上午10:24
 *  @version 1.0
 */
public class ListObjects {

	static final String ACCESS_KEY = "*********";
	static final String SECRET_KEY = "*********";
	static final String endpoint = "*********"; 
	
	public static void main(String[] args) {
		final String USAGE = "\n" +
	            "To run this example, supply the name of an S3 bucket to\n" +
	            "list all the objects .\n" +
	            "\n" +
	            "Ex: ListObjects <bucketname>\n";
		
         if (args.length < 1) {
	       System.out.println(USAGE);
	       System.exit(1);
         }
         
		 RGWClient	client = new RGWClient(ACCESS_KEY, SECRET_KEY, endpoint);
	     AmazonS3 s3 = client.createConnect();
	    	
	     String bucket_name = args[0];
	     boolean flag = s3.doesBucketExistV2(bucket_name);// 查询bucket是否存在1
         
		 if (flag) {
			 try {
				    System.out.format("list the objects of bucket %s...\n",bucket_name);
				    ObjectListing objlist = s3.listObjects(bucket_name);
				    List<S3ObjectSummary> objects = objlist.getObjectSummaries();
			        for (S3ObjectSummary os: objects) {
			            System.out.println("* " + os.getKey());
			        }
	        	} catch (AmazonServiceException e) {
	        		System.err.println(e.getErrorMessage());
	        		System.exit(1);
	        	}
	        }
	        System.out.println("Done!");
	}

}
