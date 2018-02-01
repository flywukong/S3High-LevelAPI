package ObjectOperation;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

import org.apache.commons.codec.language.bm.Lang;

import com.amazonaws.event.ProgressEventType;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.event.ProgressEvent;
import  org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.TransferManagerConfiguration;
import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.services.s3.model.*;
import common.RGWClient;
/**
 * 
 *  Class Name: ThreadsPutObjects.java
 *  Function: 
 *  使用高级接口分段上传 API
 *   Use threads to upload large collections of files,each of the files are devided into  UPLOAD_PART_SIZE.
 *  If those files are close in size to the multipart threshold, you need to submit multiple files to the TransferManager at the same time
 *  to get the benefits of parallelization. 
 *  
 *  You can use this API to upload new large objects 
 *  
 * 
 *  @author chen  DateTime 2018年1月24日 上午9:20
 *  @version 1.0
 */
public final class ThreadsPutObjects {

	static final String ACCESS_KEY = "*********";
	static final String SECRET_KEY = "**********";
	static final String endpoint = "**********"; 
	static final long UPLOAD_PART_SIZE = 5* 1024 * 1024;
	static final long multipartUploadThreshold = 5*1024*1024;
	public static void main(String[] args) {
		
		RGWClient	client = new RGWClient(ACCESS_KEY, SECRET_KEY, endpoint);
    	AmazonS3 s3 = client.createConnect();
    	
    	String bucketName = "exbucket321";
		s3.createBucket(bucketName);		
		
    	List<PutObjectRequest> objectList = new ArrayList<PutObjectRequest>();
    	objectList.add(new PutObjectRequest(bucketName, "myfile1",
    			new File("D:\\fiasf")));
    	objectList.add(new PutObjectRequest(bucketName, "myfile3",
    			new File("D:\\History")));
    	objectList.add(new PutObjectRequest(bucketName, "myfile3",
    			new File("D:\\test3")));
    	
		CountDownLatch doneSignal = new CountDownLatch(objectList.size());
		
		TransferManager tm = TransferManagerBuilder.standard()
    	        .withExecutorFactory(() -> Executors.newFixedThreadPool(15))
    	        .withMinimumUploadPartSize(UPLOAD_PART_SIZE)
    	        .withMultipartUploadThreshold( (long) multipartUploadThreshold)
    	        .withS3Client(s3)
    	        .build();
   
        //We use a CountDownLatch, initializing it to the number of files to upload.
		//We register a general progress listener with each PutObjectRequest, so we can capture major events, including completion and failures that will count down the CountDownLatch.
		//After we have submitted all of the uploads, we use the CountDownLatch to wait for the uploads to complete.
		ArrayList uploads = new ArrayList();
		for (PutObjectRequest object: objectList) {
			ProgressListener lis = new UploadCompleteListener(object.getFile(),object.getBucketName()+"/"+object.getKey(),doneSignal);
			object.setGeneralProgressListener(lis);
			uploads.add(tm.upload(object));
		}
		try {
			doneSignal.await();
			
		} catch ( InterruptedException e ) {
			e.printStackTrace();
		}
		
		tm.shutdownNow();
		System.out.println("shutdown");
		
		}
		
}
	//com.amazonaws.event.ProgressListener instead of com.amazonaws.services.s3.model.ProgressListener
   //Listener interface for transfer progress events.
   //高级别分段上传 API 提供了一个侦听接口 (ProgressListener)，以便在使用TransferManager 类上传数据时跟踪上传进度。
	class UploadCompleteListener implements ProgressListener
	{
		
		private  final Log log =  LogFactory.getLog(UploadCompleteListener.class);
		
		CountDownLatch doneSignal;
		File f;
		String target;
		
		public UploadCompleteListener(File f,String target,
										CountDownLatch doneSignal) {
			this.f=f;
			this.target=target;
			this.doneSignal=doneSignal;
		}
		
		public void progressChanged(ProgressEvent progressEvent) {
			if (progressEvent.getEventType() 
					== ProgressEventType.TRANSFER_STARTED_EVENT) {
	        	log.info("Started to upload: "+f.getAbsolutePath()
	        		+ " -> "+this.target);
	        }
	        if (progressEvent.getEventType()
	        		== ProgressEventType.TRANSFER_COMPLETED_EVENT) {
	        	log.info("Completed upload: "+f.getAbsolutePath()
	        		+ " -> "+this.target);
	        	doneSignal.countDown();
	        }
	        
	        if (progressEvent.getEventType() == 
	        		ProgressEventType.TRANSFER_FAILED_EVENT) {
	        	log.info("Failed upload: "+f.getAbsolutePath()
	        		+ " -> "+this.target);
	        	doneSignal.countDown();
	        }
	    }
	}

	



