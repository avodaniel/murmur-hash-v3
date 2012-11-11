#
# Inspired by https://github.com/xerial/snappy-java/blob/develop/Makefile .
#

OBJ=obj
MVN=mvn
NATIVES-TARGET=src/main/resources/NATIVE/$(shell bin/os-arch.sh)/$(shell bin/os-name.sh)

all: build
build: $(OBJ)/libmurmur-hash-v3-java.so class

$(OBJ)/MurmurHashV3.o: $(addprefix src/main/java/com/logentries/murmur/, MurmurHashV3.cpp MurmurHashV3.h)
	mkdir -p $(OBJ)
	$(CXX) -O3 -g -fPIC -I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/linux -c src/main/java/com/logentries/murmur/MurmurHashV3.cpp -o $(OBJ)/MurmurHashV3.o

$(OBJ)/libmurmur-hash-v3-java.so: $(OBJ)/MurmurHashV3.o
	$(CXX) -shared -Wl,-soname,libmurmur-hash-v3-java.so -o $(OBJ)/libmurmur-hash-v3-java.so $(OBJ)/MurmurHashV3.o

class: build-class

build-class: target/libmurmur-hash-v3-1.0-SNAPSHOT.jar

target/libmurmur-hash-v3-1.0-SNAPSHOT.jar: add-so
	$(MVN) package -Dmaven.test.skip=true

add-so: $(OBJ)/libmurmur-hash-v3-java.so
	mkdir -p $(NATIVES-TARGET)
	cp $(OBJ)/libmurmur-hash-v3-java.so $(NATIVES-TARGET)

test:
	$(JAVA_HOME)/bin/javac MurmurHashV3.java
	mkdir -p src/test/resources/ie/ucd/murmur
	mv MurmurHashV3.class src/test/resources/ie/ucd/murmur
	$(MVN) test

#download-murmur: .download-murmur.stamp
#
#.download-murmur.stamp:
#	mkdir -p src/test/resources
#	wget 'https://github.com/trevorah/murmurhash-java/archive/master.tar.gz'
#	tar xvf master.tar.gz
#	touch .download-murmur.stamp
#
#compile-murmur: download-murmur
#	$(JAVA_HOME)/bin/javac murmurhash-java-master/src/main/java/ie/ucd/murmur/MurmurHash.java
#	mkdir -p src/test/resources/ie/ucd/murmur/
#	mv murmurhash-java-master/src/main/java/ie/ucd/murmur/MurmurHash.class src/test/resources/ie/ucd/murmur/

clean:
	rm -fr obj
	rm -fr target
	rm -fr src/main/resources/NATIVE
	rm -fr src/test/resources/ie
	rm -f .*.stamp
	rm -f src/main/java/com/logentries/murmur/MurmurHashV3.class
