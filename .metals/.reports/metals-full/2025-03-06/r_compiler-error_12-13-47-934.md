file://<WORKSPACE>/src/main/java/org/exemple/demo/multiProducer.java
### java.util.NoSuchElementException: next on empty iterator

occurred in the presentation compiler.

presentation compiler configuration:


action parameters:
offset: 597
uri: file://<WORKSPACE>/src/main/java/org/exemple/demo/multiProducer.java
text:
```scala
package org.exemple.demo;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

public class multiProducer {


    class building{
        private String name;
        private Dictionary < String , Double> rooms ;
        public building(String name){
            this.name = name;
            rooms = new Hashtable<>();
        }
        public void addRoom(String roomName, Double tempeture){
            this.rooms.put(roomName, tempeture);
        }
        public void removeRom(String roomName){
            this.rooms.remove(roomName);
        }
        pp@@
    } 
    
}

```



#### Error stacktrace:

```
scala.collection.Iterator$$anon$19.next(Iterator.scala:973)
	scala.collection.Iterator$$anon$19.next(Iterator.scala:971)
	scala.collection.mutable.MutationTracker$CheckedIterator.next(MutationTracker.scala:76)
	scala.collection.IterableOps.head(Iterable.scala:222)
	scala.collection.IterableOps.head$(Iterable.scala:222)
	scala.collection.AbstractIterable.head(Iterable.scala:935)
	dotty.tools.dotc.interactive.InteractiveDriver.run(InteractiveDriver.scala:164)
	dotty.tools.pc.MetalsDriver.run(MetalsDriver.scala:45)
	dotty.tools.pc.completions.CompletionProvider.completions(CompletionProvider.scala:50)
	dotty.tools.pc.ScalaPresentationCompiler.complete$$anonfun$1(ScalaPresentationCompiler.scala:146)
```
#### Short summary: 

java.util.NoSuchElementException: next on empty iterator