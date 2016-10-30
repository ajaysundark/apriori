JFLAGS = -g -d . -cp .:./karup002
JC = javac

.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java


CLASSES = \
	ItemSet.java \
	Transaction.java \
	GenericKItemSets.java \
	FrequentKItemSets.java \
	KHashTree.java \
	HashTreeManager.java \
	TransactionManager.java \
	Apriori.java 

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class
