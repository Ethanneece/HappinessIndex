I = -I /usr/lib/jvm/java-11-openjdk/include/ -I /usr/lib/jvm/java-11-openjdk/include/linux -I /usr/lib/swipl/include/ 

PRELOAD = /usr/lib/swi-prolog/lib/x86_64-linux/libswipl.so

all: scala header lib 

scala: Happy.scala
	scalac Happy.scala

header:
	javac ScalaH.java
	java ScalaH Happy.class
	scalac Happy.scala
	
lib: Happy.cpp
	g++ -Wno-unused-result $(I) -shared -fPIC -o libCallPrologFromScala.so Happy.cpp
	
run:
	LD_PRELOAD=$(PRELOAD) LD_LIBRARY_PATH=./ scala Happy likes.csv sells.csv frequents.csv ratings.csv $(params)

clean:
	rm -f *.so *.class *.o data.pl Happy.h
