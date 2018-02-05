# This program is  a test of S3 multpart upload API
#*-*coding:utf-8 *-*  
import shutil
import math
import string
import io
from io import BytesIO
import os
from os import path
import sys
import traceback
import boto
import boto.s3.connection
from filechunkio import FileChunkIO
import threading
import Queue
import time
import argparse

"""
定义简单的装饰器,用来输出程序运行的所用时间
"""
def timer(func):
    def decor(*args,**kwargs):

        start_time = time.time();
        func(*args);
        end_time = time.time();
        d_time = end_time - start_time
        print "run the func use {} seconds".format(d_time)


    return decor;

class Chunk:
    num = 0
    offset = 0
    len = 0
    def __init__(self, n, o, l):  
        self.num = n
        self.offset = o
        self.len = l

chunksize = 8 << 20


#初始化文件，对文件固定大小切片，存放在队列
def init_queue(filesize):
    chunkcnt = int(math.ceil(filesize*1.0/chunksize))
    q = Queue.Queue(maxsize = chunkcnt)
    for i in range(0,chunkcnt):
        offset = chunksize*i
        len = min(chunksize, filesize-offset)
        c = Chunk(i+1, offset, len)
        q.put(c)
    return q

#使用FileChunkIO切分文件流，分段上传
def upload_chunk(filepath, mp, q, id):
    while (not q.empty()):
        chunk = q.get()
        fp = FileChunkIO(filepath, 'r', offset=chunk.offset, bytes=chunk.len)
        mp.upload_part_from_file(fp, part_num=chunk.num)
        fp.close()
        q.task_done()

#使用多线程进行分段上传
@timer
def upload_file_multipart(filepath, keyname, bucket, threadcnt=8):
    filesize = os.stat(filepath).st_size
    mp = bucket.initiate_multipart_upload(keyname)
    q = init_queue(filesize)
    for i in range(0, threadcnt):
        t = threading.Thread(target=upload_chunk, args=(filepath, mp, q, i))
        t.setDaemon(True)
        t.start()
    q.join()

#使用Range头的特性完成分段下载，range指定下载的文件内容范围
def download_chunk(filepath, bucket, key, q, id):
    while (not q.empty()):
        chunk = q.get()
        offset = chunk.offset
        len = chunk.len
        resp = bucket.connection.make_request("GET", bucket.name, key.name, headers={"Range":"bytes=%d-%d" % (offset, offset+len)})
        data = resp.read(len)
        fp = FileChunkIO(filepath, 'r+', offset=offset, bytes=len)
        fp.write(data)
        fp.close()
        q.task_done()

#使用多线程进行下载
@timer
def download_file_multipart(key, bucket, filepath, threadcnt):
    if type(key) == str:
        key=bucket.get_key(key)
    filesize=key.size
    if os.path.exists(filepath):
        os.remove(filepath)
    os.mknod(filepath)
    q = init_queue(filesize)
    for i in range(0, threadcnt):
        t = threading.Thread(target=download_chunk, args=(filepath, bucket, key, q, i))
        t.setDaemon(True)
        t.start()
    q.join()

#生成测试文件
def gen_file(path,size):  
    #首先以路径path新建一个文件，并设置模式为写  
    file = open(path,'w')  
    #根据文件大小，偏移文件读取位置   
    file.seek(1024*1024*1024*size)
    file.write('\x00')  
    file.close()  
      
      

if __name__ == '__main__':

    parser = argparse.ArgumentParser()
    parser.add_argument('-H', '--hostname', help='radosgw hostname', required=True)
    parser.add_argument('-a', '--access-key', help='S3 access key', required=True)
    parser.add_argument('-s', '--secret-key', help='S3 secret key', required=True)
    parser.add_argument('-f', '--filesize',help ='test file size(G)',required=True)
    parser.add_argument('-t', '--threadcnt',help ='thread numeber',required=True)
    args = parser.parse_args()
    

    conn = boto.connect_s3(
        aws_access_key_id = args.access_key,
        aws_secret_access_key = args.secret_key,
        host = args.hostname,
        is_secure=False,
        calling_format = boto.s3.connection.OrdinaryCallingFormat(),
    )
    
    filepath = os.getcwd()+"/"+"testfile"
    keyname = "testkey"
    print "creating bucket"

    bucket = conn.create_bucket('test-bucket') 
    
    print "generating testfile"
    gen_file(filepath,int(args.filesize))

   

    threadcnt = 8
    print "uploading file using {} theads".format(args.threadcnt)
    upload_file_multipart(filepath, keyname, bucket, threadcnt)

    key = bucket.get_key(keyname)
    print "downloading file using {} theads".format(args.threadcnt)
    download_file_multipart(key, bucket, filepath,threadcnt)

  

