build_images: prepare_easydb_executable
	export TAG="latest"
	docker-compose build
	docker build -t easydb/mongo:${TAG} -f Dockerfile.mongo .

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

