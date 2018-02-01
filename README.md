About

This is a simple program to test S3 high-level API using Java SDK and Python

 *  As Big Data grows in popularity, it becomes more important to move large data sets to and from Amazon S3. You can improve the speed of uploads by parallelizing them. 
 *  You can break an individual file into multiple parts and upload those parts in parallel  by using TransferManager High API
 *  The Multipart upload API enables you to upload large objects in parts. You can use this API to upload new large objects


Start using

#java funcion

require: aws-java-sdk-core-2.43 aws-java-sdk-s3-2.43

1.upload one file of big size as a whole and download using threads
2.upload severl files as a list 

#python function

require:The program  makes use of the FileChunkIO module, so pip install FileChunkIO if it isn’t already installed

upload one file of big size as a whole and download using threads



