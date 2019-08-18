.PHONY: deploy-on-minikube

MINIKUBE_IP = $(shell minikube ip)

deploy-on-minikube: init-minikube deploy

init-minikube:
	ssh -i ~/.minikube/machines/minikube/id_rsa docker@$(MINIKUBE_IP) 'bash -s' < ./mongo/minikube.sh

deploy: deploy-mongo deploy-app

deploy-app:
	cd easydb && sh deploy.sh && cd -

deploy-mongo:
	cd mongo && sh deploy.sh && cd -

clean-hard: stop
	rm mongo/tmp.yml || true
	rm mogno/volumes-tmp.yml || true
	rm easydb/tmp.yml || true

stop: stop-app stop-mongo

stop-app:
	kubectl delete -f easydb/tmp.yml || true

stop-mongo:
	kubectl delete -f mongo/tmp.yml || true
