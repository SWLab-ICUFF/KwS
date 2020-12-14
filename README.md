# Constructing RDF Keyword Search Benchmarks

This project contains an algorithm implementation to construct RDF Keyword Search Benchmarks and four computed benchmarks. The benchmarks were inspired by Coffman and Weaver [1] and Dossoand Silvello[2]. The used datasets are available at https://doi.org/10.6084/m9.figshare.11347676.

The Java class BuildBenchmark scans the src/main/resources/benchmarks/ESWC2021/**dataset name**/query.txt files and runs, for each file, a sequence of SPARQL scripts available at src/main/resources/sparql/KwS/v5/1/1, to compute the solution generators. 
  
The solution generators computed for each keyword query are available at src/main/resources/benchmarks/ESWC2021/**dataset name**/**query number**.nq.gz.
  
Project dependency: https://github.com/lapaesleme/SWLabJenaARQExtensions


[1] Coffman, Joel, and Alfred C Weaver. “An Empirical Performance Evaluation of Relational Keyword Search Techniques.” IEEE Transactions on Knowledge and Data Engineering 26, no. 1 (2012): 30–42. https://doi.org/10.1109/TKDE.2012.228.

[2] Dosso, Dennis, and Gianmaria Silvello. “Search Text to Retrieve Graphs: A Scalable RDF Keyword-Based Search System.” IEEE Access 8 (2020): 14089–111. https://doi.org/10.1109/ACCESS.2020.2966823.
