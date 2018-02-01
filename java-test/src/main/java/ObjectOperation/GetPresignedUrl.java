package ObjectOperation;

import java.net.URL;
import java.util.Date;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;

import common.RGWClient;
/**
 * 
 *  Class Name: GetPresignedUrl.java
 *  Function: 
 *   get Presignedurl of the exists object,  the object must be "Private"
 *   The parameter of  expiration means the Effective time of url 
 *  @author chen  DateTime 2018年1月18日 上午15:24
 *  @version 1.0
 */
public class GetPresignedUrl{
	static final String ACCESS_KEY = "*********";
	static final String SECRET_KEY = "*********";
	static final String endpoint = "*********"; 
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		final String USAGE = "\n" +
	            "To run this example, supply the name of an S3 bucket and a file to\n" +
	            "get the PresignedUrl .\n" +
	            "\n" +
	            "Ex: GetPresignedUrl <bucketname> <filename>\n";

        if (args.length < 2) {
	       System.out.println(USAGE);
	       System.exit(1);
        }
		
       String bucket_name = args[0];
       String key_name = args[1];
	   RGWClient	client = new RGWClient(ACCESS_KEY, SECRET_KEY, endpoint);
       AmazonS3 s3 = client.createConnect();
	   Date expiration = new Date();
   	   long msec = expiration.getTime();
   	   msec += 1000 * 60 * 10; //10分钟
   	   expiration.setTime(msec);
                   
   	   GeneratePresignedUrlRequest generatePresignedUrlRequest = 
                    new GeneratePresignedUrlRequest(bucket_name,key_name);
   	   generatePresignedUrlRequest.setMethod(HttpMethod.GET); // Default.
   	   generatePresignedUrlRequest.setExpiration(expiration);
   	   URL url = s3.generatePresignedUrl(generatePresignedUrlRequest);
   	   
       System.out.println(url.toString());
	}

}
