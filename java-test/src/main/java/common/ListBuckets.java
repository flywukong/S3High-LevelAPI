package common;

import java.util.List;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;

/**
 * List your Amazon S3 buckets.
 *
 */
public class ListBuckets
{
	
//	static final String ACCESS_KEY = "*********";
//	static final String SECRET_KEY = "*********";
//	static final String endpoint = "*********"; 
	static final String ACCESS_KEY = "633FP5V4V3HI31O9D9PH";
	static final String SECRET_KEY = "DWLsoZOY9d1Jwee72jAsndLmhYXxzSzMwL1LKAoN";
	static final String endpoint = "http://cos.iflytek.com"; 
    public static void main(String[] args)
    {

    	RGWClient	client = new RGWClient(ACCESS_KEY, SECRET_KEY, endpoint);
    	AmazonS3 conn = client.createConnect();
    	
        List<Bucket> buckets = conn.listBuckets();
        System.out.println("Your Amazon S3 buckets are:");
        for (Bucket b : buckets) {
            System.out.println("* " + b.getName());
        }
    }

}

