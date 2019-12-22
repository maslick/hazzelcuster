import hazelcast, logging

if __name__ == "__main__":
    logging.basicConfig()
    logging.getLogger().setLevel(logging.DEBUG)
    config = hazelcast.ClientConfig()
    config.network_config.addresses.append('localhost:5701')
    config.group_config.name = "dev"
    config.group_config.password = "dev-pass"
    client = hazelcast.HazelcastClient(config)

    helloMap = client.get_map("hello")
    helloMap.set("hello", "world!")
    print(helloMap.get("hello").result())
