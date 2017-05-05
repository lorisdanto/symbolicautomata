A symbolic automata library
================
This efficient automata library allows you to represent large (or infinite) alphabets succinctly.

Symbolic automata
----------------
In a *symbolic automaton* transitions carry predicates instead of concrete symbols.
This allows you to represent large characters sets like UTF.
For example a transition 0-[a-z]->1 represents a transition for going from state 0 to state 1 with every symbol in the interval [a-z].

You can read more about symbolic automata here:
*http://pages.cs.wisc.edu/~loris/symbolicautomata.html*

The library
----------------
The library supports:
- *Symbolic automata* and all their algorithms (intersection, equivalence, minimization, etc.)
- *Symbolic visibly pushdown automata* and all their algorithms
- *Symbolic streaming string transducers*
- The character theory of intervals

Before installing
----------------
Use the following instructions if you want to run the benchmarks from 
*https://github.com/lorisdanto/automatark*

After cloning run:
- git submodule init
- git submodule update

Instructions with Eclipse
----------------
*Requirements*: Java SE >= 1.8

The easiest way to use the libraries and build them is to open them in Eclipse. You need to use a recent version of Eclipse (> Mars) otherwise you might see some problems. Import all the libraries in Eclipse as existing maven projects (Right click, import, existing maven projects). Right click on each project -> Maven -> Update project.

The main library resides in the project SVPALib. 
The character theory of interval resides in the project BooleanAlgebras.
To see usage examples of the library check the project TestSVPA.

Instructions from command line
----------------
*Requirements*: 
1. Java SE >= 1.8
2. Apache Maven >= 3.2.1

Just run "mvn clean install" in the symbolicautomata directory. 


