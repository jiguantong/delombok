java -cp lombok-1.18.10.jar;tools.jar lombok.launch.Main delombok src -d src-delomboked

java -jar lombok.jar delombok src -d src-delomboked
java -jar lombok.jar delombok -p MyJavaFile.java

java -cp "lombok.jar;tools.jar" lombok.launch.Main delombok src -d src-delomboked -e UTF-8 -f indent:4 -f generateDelombokComment:skip 

java -cp "lombok.jar;tools.jar" lombok.launch.Main delombok src/main/java -d src-delomboked
java -cp "lombok.jar;tools.jar" lombok.launch.Main delombok  --format-help