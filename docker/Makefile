build_images: prepare_easydb_executable
	docker-compose build
	docker-compose -f docker-mongo-compose.yml build

prepare_easydb_executable:
	rm -rf ./build
	../gradlew clean distZip -p ../
	mkdir -p build
	cp ../build/distributions/*.zip ./build
	unzip ./build/*.zip -d ./build
	rm ./build/*.zip
	mv ./build/easydb-0.0.1-SNAPSHOT/bin ./build/
	mv ./build/easydb-0.0.1-SNAPSHOT/lib ./build/
	rm -rf ./build/easydb-0.0.1-SNAPSHOT
	rsync -av --exclude "application*.yml" ../src/main/resources ./build/

run-app:
	docker-compose -f docker-compose.yml up

run-mongo:
	docker-compose -f docker-mongo-compose.yml up

init-mongo:
	docker exec -it mongo-replicaset-node1 bash -c "mongo localhost:27017 < /tmp/initShardedReplica.js"
	docker exec -it mongo-configserver-node1 bash -c "mongo localhost:27017 < /tmp/initConfigServers.js"
	docker exec -it mongo-router1 bash -c "mongo localhost:27017 < /tmp/initRouters.js"
	docker exec -it mongo-router1 bash -c "mongo localhost:27017 < /tmp/mongoCreateAdminUser.js"
	docker exec -it mongo-router1 bash -c "mongo localhost:27017 < /tmp/mongoCreateUser.js"

stop-app:
	@echo "Killing running easydb docker processes..."
	@docker kill `docker ps --filter "label=type=Easydb Cluster" -q` 2> /dev/null || true

	@echo "Clearing easydb docker processes traces..."
	@docker rm `docker ps -a --filter "label=type=Easydb Cluster"  -q` -f 2> /dev/null || true

stop-mongo:
	@echo "Killing running easydb docker processes..."
	@docker kill `docker ps --filter "label=type=Mongo Cluster" -q` 2> /dev/null || true

	@echo "Clearing easydb docker processes traces..."
	@docker rm `docker ps -a --filter "label=type=Easydb Cluster"  -q` -f 2> /dev/null || true

clean:
	rm -rf ./logs
	rm -rf ./data
	@echo "Removing temporary docker images..."
	@docker rmi `docker images "dangling=true" -q` -f 2> /dev/null || true
