# url-screenshot-tool
A Jar Application that captures the image of the url and stores it to an AWS S3 

•	Running the Application
	 On the command line enter the following:
     java -jar url-screenshot-project-1.0-SNAPSHOT.jar <arguments>
     The <arguments> are the series of commands that is needed to execute the behavior of the application
•	Commands
      list - Displays all of the url screenshot stored in AWS S3
      command syntax: 
      list <bucket-name> for displaying all of screenshots data stored in AWS S3 under the Bucket Name
        example: java -jar url-screenshot-project-1.0-SNAPSHOT.jar list scanner-service-test-bucket
      
      list <bucket-name> <keyword> displays screenshots data stored in AWS S3 under Bucket Name that has a keyword
        example: java -jar url-screenshot-project-1.0-SNAPSHOT.jar list scanner-service-test-bucket amazon
     
     get - Downloads the url screenshot stored in AWS S3
      command syntax: get <bucket-name> <keyname> <download-path> downloads the screenshot <keyname> that is stored in AWS S3 
      under the Bucket Name to the <download-path>
        example: java -jar url-screenshot-project-1.0-SNAPSHOT.jar get scanner-service-test-bucket amazon_2019-10-06.15.29.54.054 C:\Users\User\Downloads
 
    capture - Screenshots the URL and Stores the backup screenshot to AWS S3
      command syntax: 
      capture <bucket-name> in <url-1>,<url-2>,...<url-n> screenshots and stores to AWS S3 under Bucket Name 
      for comma separated inline urls
        example: java -jar url-screenshot-project-1.0-SNAPSHOT.jar capture scanner-service-test-bucket in http://www.youtube.com,https://www.google.com
      
      capture <bucket-name> file <list-of-url-text-file-filepath> screenshots and stores to AWS S3 under Bucket Name for urls 
      that are stored in a text file.  A full path of the file must be given for using this command
        example:	java -jar url-screenshot-project-1.0-SNAPSHOT.jar capture scanner-service-test-bucket file C:\list-of-url-website.txt

•	Please take note that this feature uses Selenium geckodriver which is very sensitive for url formats. 
Please make sure that when entering a website url for capture should be using the full website url 
(e.g. http://www.google.com or https://www.google.com) omitting the http: or https: format will throw an error

•	If the bucket name is not yet existing in S3. It will automatically create a new bucket with the mentioned bucket name in the S3

