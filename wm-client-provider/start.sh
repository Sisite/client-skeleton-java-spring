
cd target
nohup java -jar $(find . -maxdepth 1 -name wm-client-provider-\*.jar | sort | tail -n1) &> sout_wmprovider.log &
echo WMprovider Started.
