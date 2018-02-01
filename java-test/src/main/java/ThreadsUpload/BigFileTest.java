package ThreadsUpload;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.TransferManagerConfiguration;
import com.amazonaws.services.s3.transfer.Upload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URLEncoder;
import java.nio.file.Paths;
import java.util.concurrent.Executors;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;

/**
 * 
 *  Class Name: BigFileTest
 *  Function: 
 *  使用高级接口分段上传下载大文件进行测试, 在参数中指定测试文件 对应的bucket, 生成的路径，大小，上传线程数，分片大小，程序会按照这些参数生成文件并进行上传，下载操作，输出测试时刻
 *  Use threads to upload large size files， supply the name of an S3 bucket , a file path to generate , the size of test file and the thread number \n" +
	 the test progam will generate a Big file of the size and use threads  to upload to it
	 
	
 *  As Big Data grows in popularity, it becomes more important to move large data sets to and from Amazon S3.
 *  You can improve the speed of uploads by parallelizing them. 
 *  You can break an individual file into multiple parts and upload those parts in parallel 
 *  by using TransferManager High API
 *  The Multipart upload API enables you to upload large objects in parts. 
 *  You can use this API to upload new large objects 
 *  
 * 
 *  @author chen  DateTime 2018年1月30日 上午10:24
 *  @version 1.0
 */
public class BigFileTest {


	
	static final String ACCESS_KEY = "***********";
	static final String SECRET_KEY = "*****************;
	static final String endpoint = "http://cos.iflytek.com"; 
	
	
	static final long multipartUploadThreshold = 5*1024*1024;
	
	public static void create(File file, long length) throws IOException{  
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
        System.out.println(end-start);  
          
    }  
	
	public static void uploadFile(AmazonS3 client, File file, String key , long partSize, String bucket, int threadnum) throws FileNotFoundException
	{
		
		 //Configure about the TransferManger to control your upload.
    	  //When we create the TransferManager, we give it an execution pool of 15 threads. 
    	 //By default, the TransferManager creates a pool of 10,but you can set this to scale the pool size.
    	 //MultipartUploadThreshold defines the size at which the AWS SDK for Java should start breaking apart the files (in this case, 5 MiB).
    	 ///MinimumUploadPartSize defines the minimum size of each part. It must be at least 5 MiB; otherwise, you will get an error when you try to upload it.
		 TransferManager tm = TransferManagerBuilder.standard()
	    	        .withExecutorFactory(() -> Executors.newFixedThreadPool(threadnum))
	    	        .withMinimumUploadPartSize(partSize)
	    	        .withMultipartUploadThreshold( (long) multipartUploadThreshold)
	    	        .withS3Client(client)
	    	        .build();
	        //PART_SIZE was set to 5M.  if object.size < 10M , upload object as a whole , else  The object is divided into 5M pieces 
	        System.out.println("Hello");
	      
	        InputStream inputStream = new FileInputStream(file);
	        ObjectMetadata objectMetadata = new ObjectMetadata();
			objectMetadata.setContentLength(file.length());

			
	        PutObjectRequest putObjectrequest = new PutObjectRequest(
	 			   bucket, key, inputStream, objectMetadata);
	        
	        Upload upload = tm.upload(putObjectrequest.withCannedAcl(CannedAccessControlList.PublicRead));
	        
	        System.out.println("Hello2");
	 
	        try {
	            // Or you can block and wait for the upload to finish
	            upload.waitForCompletion();
	            System.out.println("Upload complete.");

	           
	        } catch (AmazonClientException amazonClientException) {
	            System.out.println("Unable to upload file, upload was aborted.");
	            amazonClientException.printStackTrace();
	        } catch (InterruptedException e) {
				
				e.printStackTrace();
			}
	        finally
	        {
	             tm.shutdownNow();
	        }
	    }
	
	
	public static void main(String[] args) throws IOException {
		
		final String USAGE = "\n" +
	            "To run this example, supply the name of an S3 bucket , a file path to generate , the size of test file "
	            + "and the thread number \n" +
	            " the test progam will generate a Big file of the size and use threads  to upload to it.\n" +
	            "\n" +
	            "Ex: BigFileTest <bucketname> <filepath><filesize><threadnum><partSize>\n";

        if (args.length < 4) {
	       System.out.println(USAGE);
	       System.exit(1);
        }
      
    	String bucket_name = args[0];
	    String file_path = args[1];
	    String filesize = args[2];
	    String threadnum = args[3];
	    String PartSize = args[4];
	    
	    String key = Paths.get(file_path).getFileName().toString();
 
    	File file=new File(file_path);
    	long size = Long.parseLong(filesize);
    	long partSize = Long.parseLong(PartSize);
    	create(file, size);
    	
    	RGWClient	client = new RGWClient(ACCESS_KEY, SECRET_KEY, endpoint);
    	AmazonS3 s3 = client.createConnect();
	    
    	Bucket b = null;
    	if (s3.doesBucketExistV2(bucket_name)) {
            System.out.format("Bucket %s already exists.\n", bucket_name);
        } else {
            try {
                 b = s3.createBucket(bucket_name);
            } catch (AmazonS3Exception e) {
                System.err.println(e.getErrorMessage());
            }
        }
    
    	int num =  Integer.parseInt(threadnum);
//    	upload Big file by sharding and using threads
    	long startTime=System.nanoTime();   //获取开始时间  
    	
    	uploadFile(s3,file,key, partSize,  bucket_name, num);
    	long endTime=System.nanoTime(); //获取结束时间  
        System.out.println("run time： "+(endTime-startTime)+"ns");  

    	
        //upload file as a whole without using threads
    	
    	
        long startTime2=System.nanoTime();   //获取开始时间  
        try {
    		s3.putObject(bucket_name, key, file_path);
    	} catch (AmazonServiceException e) {
    		System.err.println(e.getErrorMessage());
    		System.exit(1);
    	}
    	long endTime2=System.nanoTime(); //获取结束时间  
    	System.out.println("run time1： "+(endTime-startTime)/1000/1000/1000+"s");  
        System.out.println("run time2： "+(endTime2-startTime2)/1000/1000/1000+"ns");  
     
}
}

