# hazzelcuster
simple Hazelcast k8s cluster (DNS Lookup)

[![Build Status](https://travis-ci.org/maslick/hazzelcuster.svg?branch=master)](https://travis-ci.org/maslick/hazzelcuster)
[![Docker image](https://shields.beevelop.com/docker/image/image-size/maslick/hazzelcuster/latest.svg?style=flat-square)](https://cloud.docker.com/u/maslick/repository/docker/maslick/hazzelcuster)

## Usage
```
./gradlew clean build -x test
java -Dserver.port=8081 -jar build/libs/hazzelcuster-0.1.jar
java -Dserver.port=8082 -jar build/libs/hazzelcuster-0.1.jar
java -Dserver.port=8083 -jar build/libs/hazzelcuster-0.1.jar
```

or 
```
./gradlew bootRun -Dserver.port=8081
./gradlew bootRun -Dserver.port=8082
./gradlew bootRun -Dserver.port=8083
```

## K8s installation
```
minikube start --vm-driver=xhyve --cpus 4 --memory 8192
eval $(minikube docker-env)
```

* using official Hazelcast Docker image:
```
k create ns hazelcast
k apply -f deployment/k8s-hazelcast.yaml -n hazelcast
```

* using custom Hazelcast server:
```
./gradlew dockerBuild
k apply -f deployment/k8s-hazzelcuster.yaml
k apply -f deployment/k8s-mgmt-center.yaml
```
## Python client
* Within the cluster:
```
k run piton --image=python:3 --rm -it --restart=Never -- bash
pip install hazelcast-python-client
python
```

```
import hazelcast, logging

logging.basicConfig()
logging.getLogger().setLevel(logging.INFO)

config = hazelcast.ClientConfig()
config.network_config.addresses.append('hazzelcuster-headless.default.svc.cluster.local:5701')
client = hazelcast.HazelcastClient(config)

helloMap = client.get_map("hello")
helloMap.get("russian").result()

helloMap.set("hello", "world!")
helloMap.get("hello").result()
```
* Outside Kubernetes Cluster (Dummy Routing):
```
python3
import hazelcast, logging

logging.basicConfig()
logging.getLogger().setLevel(logging.INFO)

config = hazelcast.ClientConfig()
config.network_config.addresses.append('minikube-ip:30010') # use '$ minikube ip' to get your node ip
config.network_config.smart_routing = False
client = hazelcast.HazelcastClient(config)

helloMap = client.get_map("hello")
helloMap.get("russian").result()

helloMap.set("hello", "world!")
helloMap.get("hello").result()
```

## Deployment to Openshift (using vanilla Hazelcast image)
Deploy Hazelcast Server and Management Center:
```
oc new-project hazelcast
oc new-app -p NAMESPACE=$(oc project -q) -f deployment/openshift-hazelcast.yaml
oc create route edge management-center --service management-center-service --path /hazelcast-mancenter
```

You can also save this template to the local (per project/namespace) Openshift catalog:
```
oc create -f deployment/openshift-hazelcast.yaml
```

Build hazelcast python client image:
```
FROM python:3
RUN pip3 install hazelcast-python-client
```

```
docker build -t python-hazelcast-client .
```

Start a test python client:
```
k run piton --image=python-hazelcast-client:latest --rm -it --restart=Never --image-pull-policy=IfNotPresent -- bash
python
import hazelcast, logging

logging.basicConfig()
logging.getLogger().setLevel(logging.INFO)

config = hazelcast.ClientConfig()
config.network_config.addresses.append('hazelcast-service.hazelcast:5701')
client = hazelcast.HazelcastClient(config)

helloMap = client.get_map("hello")
helloMap.set("hello", "world!")
helloMap.get("hello").result()
```

## Deployment to GKE (using Terraform)
```
brew install terraform
terraform -install-autocomplete
. ~/.zshrc

PROJECT=hazelcuster
SA=$PROJECT-service-account
cd terraform

gcloud projects create $PROJECT
gcloud config set project $PROJECT

gcloud alpha billing accounts list
gcloud alpha billing projects link $PROJECT --billing-account XXXXX-YYYYYYY-ZZZZZZ
gcloud services enable container.googleapis.com

gcloud beta iam service-accounts create $SA --display-name "$SA"
gcloud projects add-iam-policy-binding $PROJECT --member serviceAccount:$SA@$PROJECT.iam.gserviceaccount.com --role roles/editor
gcloud iam service-accounts keys create creds/key.json --iam-account $SA@$PROJECT.iam.gserviceaccount.com

terraform init
terraform plan
terraform apply

gcloud container clusters get-credentials $PROJECT-gke-cluster --zone=europe-west3-a
k create ns hazelcast
k apply -f deployment/k8s-hazelcast.yaml -n hazelcast
```

Now you can expose the Hazelcast Management Center to the outside world via:
1. ``k port-forward -n hazelcast management-center-xxxxxx 8081:8080``
2. via NodePort (get node's public IP, add firewall rule and open port 30100)
3. via Ingress

```
terraform destroy
gcloud projects delete $PROJECT
```
