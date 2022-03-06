<div align="center">
<img src="assets/logo.png" height="196" />
</div>


---

`AutoSubExtact` is a tool to automatically extract preferred subtitle and save it with same name as source video file.

# Usage

```sh
AutoSubExtract v.0.1
Jagadeesh Kotra <jagadeesh@stdin.top>
usage: java -jar autosubextract.jar -i movie.mkv [-l eng] [--all]
       --all               Process all video files on current directory.
    -h,--help              Print Help and Exit.
    -i,--input <arg>       Video Input.
    -l,--language <arg>    language of the subtitle to extract.
```


# Building

AutoSubExtract uses [Maven Build System](https://maven.apache.org/) and target JDK/JRE 11.

To build executable JAR file:

```
mvn package
```

JAR file will be generated to `target/` folder. `-shaded.jar` prefixed JAR is executable.
