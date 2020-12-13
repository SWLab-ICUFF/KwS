# Java app for computing benchmarks for RDF Keyword Search

This project is an implementation of an algorithm to compute benchmarks for RDF Keyword Search. The datasets used in the benchmark are available at \url{https://doi.org/10.6084/m9.figshare.11347676}. 

The Java class BuildBenchmark scans the src/main/resources/benchmarks/ESWC2021/<<dataset name>>/query.txt files and runs, for each file, a sequence of SPARQL scripts available in src/main/resources/sparql/KwS/v5/1/1, to compute the solution generators. 
  
Solution generators computed for each keyword query are available in src/main/resources/benchmarks/ESWC2021/<<dataset name>>/<<query number>>.nq.gz.
  
Dependency: https://github.com/lapaesleme/SWLabJenaARQExtensions
