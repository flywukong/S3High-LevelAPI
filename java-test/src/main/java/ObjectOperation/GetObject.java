package ObjectOperation;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

import common.RGWClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
/**
 * 
 *  Class Name: PutObject.java
 *  Function: 
 *   Get an object within an Amazon S3 bucket.
 *   
 *  @author chenwu  DateTime 2018年1月17日 10:24 am 
 *  @version 1.0
 */
public class GetObject {

	static final String ACCESS_KEY = "*********";
	static final String SECRET_KEY = "*********";
	static final String endpoint = "*********"; 
	public static void main(String[] args) {
		
		final String USAGE = "\n" +
	            "To run this example, supply the name of an S3 bucket and a file to\n" +
	            "download it .\n" +
	            "\n" +
	            "Ex: GetObject <bucketname> <filename>\n";

         if (args.length < 2) {
	       System.out.println(USAGE);
	       System.exit(1);
         }
         String bucket_name = args[0];
         String key_name = args[1];

         System.out.format("Downloading %s from S3 bucket %s...\n", key_name, bucket_name);
         
     	 RGWClient	client = new RGWClient(ACCESS_KEY, SECRET_KEY, endpoint);
    	 AmazonS3 s3 = client.createConnect();
    	
         try {
        	 //get the content of object
             S3Object obj = s3.getObject(bucket_name, key_name);
             S3ObjectInputStream s3is = obj.getObjectContent();
             //write the content to file
             FileOutputStream fos = new FileOutputStream(new File("D://"+key_name));
             byte[] read_buf = new byte[1024];
             int read_len = 0;
             while ((read_len = s3is.read(read_buf)) > 0) {
                 fos.write(read_buf, 0, read_len);
             }
             s3is.close();
             fos.close();
         } catch (AmazonServiceException e) {
             System.err.println(e.getErrorMessage());
             System.exit(1);
         } catch (FileNotFoundException e) {
             System.err.println(e.getMessage());
             System.exit(1);
         } catch (IOException e) {
             System.err.println(e.getMessage());
             System.exit(1);
         }
         System.out.println("Done!");
     }
	}


