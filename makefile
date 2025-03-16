all: clean compile run

clean:
	rm -f *.class

compile:
	javac MarksManagement.java

run:
	java -cp ".:postgresql-42.7.5.jar" Main
