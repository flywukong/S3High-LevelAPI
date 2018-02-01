package ObjectOperation;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.List;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;

import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import common.RGWClient;
public class FolderOperation {
	
	private static final String SUFFIX = "/";
	static final String ACCESS_KEY = "*********";
	static final String SECRET_KEY = "*********";
	static final String endpoint = "*********"; 

	public static void main(String[] args) {
		
		RGWClient	client = new RGWClient(ACCESS_KEY, SECRET_KEY, endpoint);
	    AmazonS3 s3client = client.createConnect();
		// create bucket - name must be unique for all S3 users
		String bucketName = "javatutorial-net-example-bucket";
		s3client.createBucket(bucketName);		
		// list buckets
		for (Bucket bucket : s3client.listBuckets()) {
			System.out.println(" - " + bucket.getName());
		}
		
		// create folder into bucket
		String folderName = "testfolder";
		createFolder(bucketName, folderName, s3client);
		
		// upload file to folder and set it to public
		String fileName = folderName + SUFFIX + "testvideo.mp4";
		
		s3client.putObject(new PutObjectRequest(bucketName, fileName, 
				new File("D://fdsf"))
				.withCannedAcl(CannedAccessControlList.PublicRead));
		
      // deleteFolder(bucketName, folderName, s3client);
		

	}
	
	public static void createFolder(String bucketName, String folderName, AmazonS3 client) {
		// create meta-data for your folder and set content-length to 0
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(0);
		// create empty content
		InputStream emptyContent = new ByteArrayInputStream(new byte[0]);
		// create a PutObjectRequest passing the folder name suffixed by /
		PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName,
				folderName + SUFFIX, emptyContent, metadata);
		// send request to S3 to create folder
		client.putObject(putObjectRequest);
	}
	/**
	 * This method first deletes all the files in given folder and than the
	 * folder itself
	 */
	public static void deleteFolder(String bucketName, String folderName, AmazonS3 client) {
		List<S3ObjectSummary> fileList = 
				client.listObjects(bucketName, folderName).getObjectSummaries();
		
		for (S3ObjectSummary file : fileList) {
			client.deleteObject(bucketName, file.getKey());
		}
		client.deleteObject(bucketName, folderName);
	}
}