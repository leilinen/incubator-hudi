# Hudi
Hudi (pronounced Hoodie) stands for `Hadoop Upserts anD Incrementals`. Hudi manages storage of large analytical datasets on [HDFS](http://hadoop.apache.org/docs/stable/hadoop-project-dist/hadoop-hdfs/HdfsDesign.html) and serve them out via two types of tables

 * **Read Optimized Table** - Provides excellent query performance via purely columnar storage (e.g. [Parquet](https://parquet.apache.org/))
 * **Near-Real time Table (WIP)** - Provides queries on real-time data, using a combination of columnar & row based storage (e.g Parquet + [Avro](http://avro.apache.org/docs/current/mr.html))

For more, head over [here](https://hudi.apache.org)


How to run?

prepare code and run command in terminal

```aidl
mvn clean install -DskipTests -DskipITs
```

if you need to compile a dependency jar, run the command:
```aidl
mvn clean package -Dmaven.test.skip
```