mongod --config /etc/mongodb/mongodb.conf &

mongo localhost:27017/easydb < tmp/mongoCreateUser.js
while [ $? -ne 0 ]
do
  sleep 1s
  mongo localhost:27017/easydb < tmp/mongoCreateUser.js
done
