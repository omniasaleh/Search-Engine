Open MongoDB Compass Comunity  Local host
at the begining of crawling you have to make sure :
	1- Create Collection Called State which has one document with Fields "Id":1 , "Crawling":"False" "ReadSeed":"False".
	2- File Contains Seeds (Seeds.txt) at the project folder (Next To src folder).
	3- Folder Called by "crawl" at the project folder (Next to src folder).
Variables you can change : 
	1- Numbers of Threads. (Input From user)
	2- Number of Max Iteration in Crawling. (MAX_IT In MyData class)
	3- Number of Links you want to recrawl on it. (NUM_RESEED In Master class)
	4- Number of Max Iteration in Recrawling. (MAX_RE In MyData class)
run MyScheduler.java 
	
		
