******** G00362383 - Michael Mulholland ********
***** Data Structures & Algorithms Project *****

*** A Multithreaded Language Detector ***

*** Description ***
A Java API that can rapidly compare a query text file against a n-gram collection 
of subject texts and determine the natural language of the query file. 

*** Features ***
The menu will be displayed and this will ask the user to input the name of the language data set file .

Menu options include:

	Enter WiLI Data Location

Once the user enters in the language data set file, the application will then split the file at the '@' symbol. The text and the language will then be added to a database. Once the database is complete, the user will be asked to enter a query file to analyse.

	Enter Query Location:
	
Once the query is entered, the application will then split the file at the '@' symbol. The text will then be broken into kmers of size 2, 3, 4 and added to the database along with the language type.

The two Maps are the compared to each other to find the smallest out of place metric. The smallest out of place metric will be the natural language of the query file.