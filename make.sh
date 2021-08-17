cd plugin
./gradlew base_plugin:gitPull
./gradlew base_plugin:uploadArchives

#./gradlew auto-trycatch:uploadArchives
#
./gradlew hook-method-lib:uploadArchives
./gradlew hook-method-plugin:uploadArchives
#
#./gradlew sample-plugin:uploadArchives

./gradlew strmix-plugin:uploadArchives
./gradlew strmix-plugin-lib:uploadArchives

./gradlew base_plugin:publishToGitMaven

get_char()
{
SAVEDSTTY=`stty -g`
stty -echo
stty raw
dd if=/dev/tty bs=1 count=1 2> /dev/null
stty -raw
stty echo
stty $SAVEDSTTY
}

echo "Press any key to exit..."
char=`get_char`