../gradlew clean distZip -p ../
mkdir -p build
cp ../build/distributions/*.zip ./build
unzip ./build/*.zip -d ./build
rm ./build/*.zip
mv ./build/easydb-0.0.1-SNAPSHOT/bin ./build/
mv ./build/easydb-0.0.1-SNAPSHOT/lib ./build/
rm -rf ./build/easydb-0.0.1-SNAPSHOT
rsync -av --exclude "application*.yml" ../src/main/resources ./build/

docker login --username $DOCKER_USER --password $DOCKER_PASS
if [ "$TRAVIS_BRANCH" = "master" ]; then
export TAG="latest"
else
export TAG="$TRAVIS_BRANCH"
fi

docker-compose -f ../.docker/docker-compose.yml build
docker tag easydb/discovery_scraper $DOCKER_REPO-discovery-scraper:$TAG
docker tag easydb/load_balancer $DOCKER_REPO-load-balancer:$TAG
docker tag easydb/app $DOCKER_REPO-app:$TAG
docker tag easydb/monitoring $DOCKER_REPO-monitoring:$TAG
docker tag easydb/zookeeper $DOCKER_REPO-zookeeper:$TAG

docker-compose -f ../.docker/docker-mongo-compose.yml build
docker tag easydb/mongo $DOCKER_REPO-mongo:$TAG

docker push $DOCKER_REPO-discovery-scraper
docker push $DOCKER_REPO-load-balancer
docker push $DOCKER_REPO-app
docker push $DOCKER_REPO-monitoring
docker push $DOCKER_REPO-zookeeper
docker push $DOCKER_REPO-mongo