# hazzel-karst
simple Hazelcast k8s cluster (DNS Lookup)


## Installation
```
minikube start --vm-driver=xhyve --cpus 4 --memory 8192
eval $(minikube docker-env)
./gradlew clean build -x test
docker build -t maslick/hazzelkarst:0.1 build/libs
k apply -f deployment/k8s.yaml
```

## Python client
```
k run piton --image=python:3 --rm -it --restart=Never -- bash
root@piton:/# pip install hazelcast-python-client
root@piton:/# python
Python 3.7.3 (default, Jun 11 2019, 01:05:09)
[GCC 6.3.0 20170516] on linux
Type "help", "copyright", "credits" or "license" for more information. 
```

```
import hazelcast, logging
config = hazelcast.ClientConfig()
config.network_config.addresses.append('service-hazelcast-server.default.svc.cluster.local:5701')
client = hazelcast.HazelcastClient(config)

logging.basicConfig()
logging.getLogger().setLevel(logging.INFO)

helloMap = client.get_map("hello")
helloMap.get("russian").result()

helloMap.set("hello", "world!")
helloMap.get("hello").result()
```