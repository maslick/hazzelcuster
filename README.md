# hazzelcuster
simple Hazelcast k8s cluster (DNS Lookup)

## Usage
```
./gradlew clean build -x test
java -jar build/libs/hazzelcuster-0.1.jar -Dserver.port=8081
java -jar build/libs/hazzelcuster-0.1.jar -Dserver.port=8082
java -jar build/libs/hazzelcuster-0.1.jar -Dserver.port=8083
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
./gradlew dockerBuild
k apply -f deployment/k8s.yaml
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
config.network_config.addresses.append('192.168.99.100:30010')
config.network_config.smart_routing = False
client = hazelcast.HazelcastClient(config)

helloMap = client.get_map("hello")
helloMap.get("russian").result()

helloMap.set("hello", "world!")
helloMap.get("hello").result()
```

