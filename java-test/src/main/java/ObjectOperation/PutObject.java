package ObjectOperation;

import java.nio.file.Paths;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;

import com.amazonaws.services.s3.model.Bucket;

import common.RGWClient;
/**
 * 
 *  Class Name: PutObject.java
 *  Function: 
 *   upload object as a whole , Ceph RGW limit the max size of object which upload as a whole of 5G .
 *  
 *  @author chen  DateTime 2018年1月17日 上午10:24
 *  @version 1.0
 */
public class PutObject {

	static final String ACCESS_KEY = "*********";
	static final String SECRET_KEY = "*********";
	static final String endpoint = "*********"; 
	public static void main(String[] args) {
		
	    final String USAGE = "\n" +
		            "To run this example, supply the name of an S3 bucket and a file to\n" +
		            "upload to it.\n" +
		            "\n" +
		            "Ex: PutObject <bucketname> <filename>\n";

	    if (args.length < 2) {
		       System.out.println(USAGE);
		       System.exit(1);
	    }

		String bucket_name = args[0];
	    String file_path = args[1];
	    String key_name = Paths.get(file_path).getFileName().toString();

		RGWClient	client = new RGWClient(ACCESS_KEY, SECRET_KEY, endpoint);
    	AmazonS3 s3 = client.createConnect();
    	
        System.out.format("Uploading %s to S3 bucket %s...\n", file_path, bucket_name);
        boolean flag = s3.doesBucketExistV2(bucket_name); 
        if (flag)
        {
        	try {
        		s3.putObject(bucket_name, key_name, file_path);
        	} catch (AmazonServiceException e) {
        		System.err.println(e.getErrorMessage());
        		System.exit(1);
        	}
        }
        System.out.println("Done!");
	}

}
