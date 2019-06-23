# hazzelcuster
simple Hazelcast k8s cluster (DNS Lookup)


## Installation
```
minikube start --vm-driver=xhyve --cpus 4 --memory 8192
eval $(minikube docker-env)
./gradlew clean build -x test
docker build -t maslick/hazzelcuster:0.1 build/libs
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
config.network_config.addresses.append('service-hazelcast-server.default.svc.cluster.local:5701')
client = hazelcast.HazelcastClient(config)

helloMap = client.get_map("hello")
helloMap.get("russian").result()

helloMap.set("hello", "world!")
helloMap.get("hello").result()
```
* Outside Kubernetes Cluster (Dummy Routing):
```
import hazelcast, logging

logging.basicConfig()
logging.getLogger().setLevel(logging.INFO)

config = hazelcast.ClientConfig()
config.network_config.addresses.append('192.168.99.100:30447') // [minikube ip]:[nodePort]
config.network_config.smart_routing = False
client = hazelcast.HazelcastClient(config)

helloMap = client.get_map("hello")
helloMap.get("russian").result()

helloMap.set("hello", "world!")
helloMap.get("hello").result()
```

