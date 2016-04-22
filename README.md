###X-aurora
This project aims to develop a productivity tool that allows users to copy and paste the text content on the webpage more efficiently by minimizing the number of windows switching in the copy and paste process. 

##current status:
#Logic (Completed)
	|_ Features
		|_ Receive Text Extracted From Web Browser
		|_ Construct the Encrypted text data files
		|_ Pre-process and construct the correct Indexing entries
		|_ Prefix Searching
		|_ Term Searching
		|_ Email and Number Recognition
		|_ Email and Number Searching
		|_ Auto-Deletion for expired files
		|_ User login (Not dropbox authentication)
		|_ User switching
		|_ Sending Suggestions towards editor Plugin
	|_ Robustness
		|_ Unit Testing
			|_ Data Encryption
			|_ Data Decryption
			|_ Data File Creation
			|_ Data File Deletion
			|_ Indexing Document (Lucene Document) Insertion
			|_ Indexing Document Deletion
			|_ Document Searching (Based on Index and Keyword)
			|_ Data File Expiry Setting (in both hours and seconds)
			|_ User Login
			|_ Switch User
		|_ Input Validity Check
		|_ Assertions
		|_ Logging (Log4j)
			|_ Master_log (Log of everything)
			|_ IO_log (log of File IO)
			|_ Text_log (log of Indexing)
			|_ Error_log (log of exceptions)
	|_ Usability
		|_ Prefix Matching
		|_ Name and Email Recognition
		|_ Sorting by Relevance and Reference Time
	|_ Performance
		|_ Lighter weight API (350+ MB Stanford G.A.T.E library -> 1.6 MB JavaCC Parser, lose in recogniton precison but increase the loading performance)
		|_ No iterative String Concatenation (Java String builder/passing data in byte arrays, Eliminate Unnecessary Memory Copy)
		|_ Incremental Database construction (Eliminate Unnecessary File I/O)
		|_ Only 1 query in every operation towards the indexing system (Eliminate Unnecessary Database Access)
	|_ Security
		|_ Data Security 
			|_ AES Encryption of text data files (Confidentiality)
			|_ Key is constructed with MD5 hashed User Name and Email and Unique Salt of Each User (Authenticity)
			|_ Initial Vector (IV) is determined by Filename of the document, which is the MD5 hashed data source and extraction time (Integrity, as modification of filename or content will lead to failure in decryption)
			|_ User Unique .ks (Key Set) files (Synchronized)
			|_ Hard Coded Master Key to encrypt user Key Set Files
		|_ Runtime Security (Data Confidentiality/Integrity at Runtime)
			|_ Disallow Clone for all defined objects
			|_ Disallow Serialization and DeSerialization of all defined object
			|_ Disallow sub-Class Creation of all defined objects (all classes are final)
			|_ Disallow overwriting of all defined public method (all public method are final)
			|_ All Members are defined as private
			|_ Consider Input Parameters as final if necessary (to avoid accidential data writing/corruption)
	|_ Library Build Path (For Logic)
		|_ Lucene 5.4.1 (Indexing)
		|_ JavaCC Eclipse Plugin
		|_ Log4J Framework (log4j-1.2.17)
		|_ Apache Common IO (For File IO)
		|_ Junit 4.10

#UI (Incomplete)
#Web-Browser Plugin (Incomplete)
#Editor Plugin (Incomplete)
#Dropbox Synchronization (Incomplete)

##System Set up

	#Logic
	|_ Requirement : Eclipse 4.5
	|_ Installation of JavaCC Ecplise Plugin:
		|_ Help -> Install New Software
		|_ input http://eclipse-javacc.sourceforge.net/ -> add
		|_ installation will be completed automatically
	|_Installation of Junit 4.10 Unit Testing Framework
		|_ Download Junit archive
			|_ Windows	junit4.10.jar
			|_ Linux	junit4.10.jar
			|_ Mac OSX	junit4.10.jar
		|_ Build Path
			|_ Open Eclipse
			|_ Right click on project
			|_ Click on property
			|_ Build Path -> Configure Build Path
			|_ Add External Jars -> Add the junit4.10.jar
	|_ Running
		|_ prepare the parser
			|_ Project Package View -> xaurora.text -> XauroraParser.jj
			|_ Right click-> Compile with JavaCC
		|_ Run Main.java

	#UI (Incomplete)
	#Web-Browser Plugin (Incomplete)
	#Editor Plugin (Incomplete)
	#Dropbox Synchronization (Incomplete)

##Testing
	#Logic
		Unit Testing
		|_ Run all testing programs under xaurora.test package
		Manual Testing
		|_ Run the web plugin and Main.java
		|_ Check through the /local_data/ folder for encrypted data files
		|_ Check through the /conf_data/user/ folder for .ks files and indexing system files
		|_ Check through the /conf_data/system/log/ foler for all the log files

##Current Problem:
	1. Other Components May not be connected and Working
	2. Hard to demostrate the correctness of Logic Components (credit by Dr.Bimlesh Wadhwa)
	3. Still can Improve on Performance and Usability of Logic (like early garbage collection, increase the precision of recognition) (credit by Dr.Bimlesh Wadhwa)
	4. Extend the security consideration towards other components like UI preference etc
