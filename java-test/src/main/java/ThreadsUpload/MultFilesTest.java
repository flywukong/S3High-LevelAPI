package ThreadsUpload;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

import org.apache.commons.codec.language.bm.Lang;

import com.amazonaws.event.ProgressEventType;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.AmazonClientException;
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
 *  @author chen  DateTime 2018年1月31日 上午9:20
 *  @version 1.0
 */
public final class MultFilesTest {

	static final String ACCESS_KEY = "633FP5V4V3HI31O9D9PH";
	static final String SECRET_KEY = "DWLsoZOY9d1Jwee72jAsndLmhYXxzSzMwL1LKAoN";
	static final String endpoint = "http://cos.iflytek.com"; 
	

	
	static final long multipartUploadThreshold = 5*1024*1024;
	
	public static void createfile(File file, long length) throws IOException{  
        long start = System.currentTimeMillis();  
        RandomAccessFile r = null;  
        try {  
            r = new RandomAccessFile(file, "rw");  
            r.setLength(length);  
        } finally{  
            if (r != null) {  
                try {  
                    r.close();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
        }  
        long end = System.currentTimeMillis();  
      
          
    }  
	public static void uploadFile(AmazonS3 s3client, List<PutObjectRequest> objectList, String bucketName, int threadnum ,long partSize) throws FileNotFoundException
	{
		
		 //Configure about the TransferManger to control your upload.
    	  //When we create the TransferManager, we give it an execution pool of 15 threads. 
    	 //By default, the TransferManager creates a pool of 10,but you can set this to scale the pool size.
    	 //MultipartUploadThreshold defines the size at which the AWS SDK for Java should start breaking apart the files (in this case, 5 MiB).
    	 ///MinimumUploadPartSize defines the minimum size of each part. It must be at least 5 MiB; otherwise, you will get an error when you try to upload it.
		
    	
		CountDownLatch doneSignal = new CountDownLatch(objectList.size());
		
		TransferManager tm = TransferManagerBuilder.standard()
    	        .withExecutorFactory(() -> Executors.newFixedThreadPool(15))
    	        .withMinimumUploadPartSize(partSize)
    	        .withMultipartUploadThreshold( (long) multipartUploadThreshold)
    	        .withS3Client(s3client)
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
	
	public static void main(String[] args) throws IOException {
		
		
		final String USAGE = "\n" +
	            "To run this example, supply the name of an S3 bucket , a file path to generate , the size of test file "
	            + " the number of objects , the thread number, and the multpart size \n" +
	            " the test progam will generate a Big file of the size and use threads  to upload to it.\n" +
	            "\n" +
	            "Ex: BigFileTest <bucketname> <filepath><filenum><filesize><threadnum><partSize>\n";

        if (args.length < 4) {
	       System.out.println(USAGE);
	       System.exit(1);
        }
      
    	String bucket_name = args[0];
	    String file_path = args[1];
	    String file_num = args[2];
	    String filesize = args[3];
	    String threadnum = args[4];
	    String PartSize = args[5];
	    
    	long size = Long.parseLong(filesize);
    	long partSize = Long.parseLong(PartSize);
    	
    	int fileNum = Integer.parseInt(file_num);
    	int threadNum = Integer.parseInt(threadnum);
        List<PutObjectRequest> objectList = new ArrayList<PutObjectRequest>();
		
        StringBuilder sn=new StringBuilder();
    	String file_prefix = sn.append(file_path).append("\\").append("testfile").toString();
        for (int i=0; i <fileNum; i++ )
    	{
        	System.out.println(file_prefix);
            String filePath = file_prefix+String.valueOf(i);
        	System.out.println("filePath"+filePath);
        	
        	String key = Paths.get(filePath).getFileName().toString();
        	System.out.println("key"+key);

        	File file=new File(filePath);
        	createfile(file,size);
        	objectList.add(new PutObjectRequest(bucket_name, key, file));
    	
    	}
		RGWClient	client = new RGWClient(ACCESS_KEY, SECRET_KEY, endpoint);
    	AmazonS3 s3 = client.createConnect();

      
       long startTime=System.nanoTime();   //获取开始时间  
   	
       uploadFile(s3, objectList, bucket_name, threadNum, partSize );
      long endTime=System.nanoTime(); //获取结束时间  
       System.out.println("run time： "+(endTime-startTime)+"ns");  
			
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

	



