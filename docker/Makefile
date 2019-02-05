build_images: prepare_easydb_executable
	docker-compose build

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

run:
	docker-compose up

stop:
	@echo "Killing running easydb docker processes..."
	@docker kill `docker ps -f "ancestor=easydb/app" -q` 2> /dev/null || true
	@docker kill `docker ps -f "ancestor=easydb/monitoring" -q` 2> /dev/null || true
	@docker kill `docker ps -f "ancestor=easydb/mongo" -q` 2> /dev/null || true
	@docker kill `docker ps -f "ancestor=easydb/zookeeper" -q` 2> /dev/null || true
	@docker kill `docker ps -f "ancestor=easydb/load_balancer" -q` 2> /dev/null || true

	@echo "Clearing easydb docker processes traces..."
	@docker rm `docker ps -a -f "ancestor=easydb/app" -q` 2> /dev/null || true
	@docker rm `docker ps -a -f "ancestor=easydb/monitoring" -q` 2> /dev/null || true
	@docker rm `docker ps -a -f "ancestor=easydb/mongo" -q` 2> /dev/null || true
	@docker rm `docker ps -a -f "ancestor=easydb/load_balancer" -q` 2> /dev/null || true

clean:
	rm -rf ./logs
	rm -rf ./data
	@echo "Removing temporary docker images..."
	@docker rmi `docker images "dangling=true" -q` -f 2> /dev/null || true
