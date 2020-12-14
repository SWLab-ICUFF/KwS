# Constructing RDF Keyword Search Benchmarks

This project implementats an algorithm to construct RDF Keyword Search Benchmarks. The datasets used in the benchmark are available at https://doi.org/10.6084/m9.figshare.11347676. 

The Java class BuildBenchmark scans the src/main/resources/benchmarks/ESWC2021/**dataset name**/query.txt files and runs, for each file, a sequence of SPARQL scripts available in src/main/resources/sparql/KwS/v5/1/1, to compute the solution generators. 
  
Solution generators computed for each keyword query are available in src/main/resources/benchmarks/ESWC2021/**dataset name**/**query number**.nq.gz.
  
Project dependency: https://github.com/lapaesleme/SWLabJenaARQExtensions
