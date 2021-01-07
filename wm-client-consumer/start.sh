
cd target
#nohup java -jar $(find . -maxdepth 1 -name wm-client-consumer-\*.jar | sort | tail -n1) &> sout_wmconsumer.log &
java -jar $(find . -maxdepth 1 -name wm-client-consumer-\*.jar | sort | tail -n1)
echo WMconsumer Started.
