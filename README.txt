ChatServer Code In Java

Main Classes: ChatServer, ChatClient 
Assumes you have unzipped src zip file and are at the root of the tree (Symphony)

Build: 

1. Use Gradle 
	- Run gradle clean build distzip from top level directory (assumes you have downloaded gradle-1.12 distro (recommended version))
	- generates a zip file in <root>/build/distributions
	- copy the zip file to a specific location and unzip

2. Use runnable jar files included with the distro
	- ChatServer.jar (Server code)
	- ChatClient.jar (client code)
	
Run:

1. Using Symphony-1.0.zip (from gradle build)
when zip file is unzipped, it will add a bin/, conf/ and lib/ folder with necessary files
conf/ folder contains sample server and client properties

From new zipfiles root dir, edit bin/ChatServer and add a cmdline argument specifying ../conf/server.txt to the last line of the script
From new zipfiles root dir, edit bin/ChatClient and add a cmdline argument specifying ../conf/user1.txt to the last line of the script

More clients can be added

To run simply go to root level directory of ziptree and run bin/ChatServer& (background) and/or redirect output from console
Edit conf/chatserver-log4j.xml for changing the logging level 

Similarly run bin/ChatClient& for running a client. Each client must have its own properties file (like user1.txt)

2. Using runnable jars

Navigate to folder where jars are found... separate jar files
Copy src distro tree src/main/resources/chatserver-log4j.xml to the same folder as the jar files

To run a client:
java -Dlog4j.configuration=file:./chatserver-log4j.xml -jar chatserver.jar /scratch/server.txt


To run a client:
java -Dlog4j.configuration=file:./chatserver-log4j.xml -jar chatclient.jar /scratch/user1.txt



More Information
For a list of supported ChatCommands, please see src/main/resources/ChatCommands.txt from the src zip file
