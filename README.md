
What's that ?
---
Just a mongo helper via morphia
You can see exemple on lab package


PlayFramework
---
If you use playframework you hae to put on th Global

MorphiaLoggerFactory.reset()
MorphiaLoggerFactory.registerLogger(classOf[SLF4JLogrImplFactory])



Maven
---
```xml
 <dependency>
   <groupId>com.mongoline</groupId>
   <artifactId>mongoline</artifactId>
   <version>0.0.1-SNAPSHOT</version>
 </dependency>
```


Ivy
--

 "com.mongoline"  % "mongoline" % "0.0.1-SNAPSHOT"


